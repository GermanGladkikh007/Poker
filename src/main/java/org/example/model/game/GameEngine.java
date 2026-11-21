package org.example.model.game;

import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.events.*;
import org.example.model.hand.HandEvaluator;
import org.example.model.hand.HandValue;
import org.example.model.player.PlayerStatus;
import org.example.model.player.Player;
import org.example.model.table.Deck;
import org.example.model.table.Pot;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Движок игры - управляет действиями игроков, проверяет состояние, этап игры.
 */
public class GameEngine {

    private final GameConfig gameConfig;
    private final GameState gameState;

    private final Random random;
    private Deck deck;

    public GameEngine(GameConfig gameConfig, GameState gameState) {
        this.gameConfig = gameConfig;
        this.gameState = gameState;
        this.random = new Random(gameConfig.seed());
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * Инициализация раздачи(события, колода)
     * @return
     */
    public List<GameEvent> startHand(){
        List<GameEvent> events = new ArrayList<>();
        this.deck = new Deck(random);
        gameState.resetForNewHand();
        events.add(new HandStarted());


        for (Player player : gameState.getPlayers()) {
            if(player.getStatus() == PlayerStatus.ACTIVE){
                player.getHoleCards().add(deck.dealCard());
                player.getHoleCards().add(deck.dealCard());
            }
        }

        events.add(new CardsDealt());

        // Позиции блайндов
        int sbIndex;
        int bbIndex;

        if(gameState.getPlayers().size() == 2){
            sbIndex = gameState.getDealerIndex();
            bbIndex = (gameState.getDealerIndex() + 1) % gameState.getPlayers().size();
        }else{
            sbIndex = (gameState.getDealerIndex() + 1) % gameState.getPlayers().size();
            bbIndex = (gameState.getDealerIndex() + 2) % gameState.getPlayers().size();
        }


        Player sbPlayer = gameState.getPlayers().get(sbIndex);
        Player bbPlayer = gameState.getPlayers().get(bbIndex);

        postBlind(sbPlayer, gameConfig.smallBlind(), events, true);
        postBlind(bbPlayer, gameConfig.bigBlind(), events, false);

        gameState.setCurrentBet(gameConfig.bigBlind());


        int firstToAct;
        if(gameState.getPlayers().size() == 2){
            firstToAct = gameState.getDealerIndex();
        }else{
            firstToAct = (bbIndex + 1) % gameState.getPlayers().size();
        }

        gameState.setCurrentPlayerIndex(firstToAct);

        return events;
    }

    /**
     * Обработка действия игрока
     * @param action
     * @return
     */
    public List<GameEvent> applyAction(Action action){
        Player player = getCurrentPlayer();

        List<GameEvent> events = new ArrayList<>();

        switch (action.actionType()){
            case FOLD   -> handleFold(player, events,action);
            case CHECK  -> handleCheck(player, events,action);
            case CALL   -> handleCall(player, events,action);
            case RAISE  -> handleRaise(player, events,action);
            case ALL_IN -> handleAllIn(player, events,action);
        }

        gameState.setActionsTakenThisRound(gameState.getActionsTakenThisRound() + 1);

        if(isHandOver()){
            events.addAll(awardPotAndEndHand());
        }else if(isRoundComplete()){
            events.addAll(advanceStreet());
        }else{
            advanceToNextPlayer();
        }
        return events;
    }


    // передача хода
    private void advanceToNextPlayer(){
        int totalPlayers = gameState.getPlayers().size();
        int currentIndex = gameState.getCurrentPlayerIndex();

        for(int i = 1; i <= totalPlayers; i++){
            int nextPlayerIndex = (currentIndex + i) % totalPlayers;
            Player nextPlayer = gameState.getPlayers().get(nextPlayerIndex);

            if(nextPlayer.getStatus() == PlayerStatus.ACTIVE){
                gameState.setCurrentPlayerIndex(nextPlayerIndex);
                return;
            }
        }
        gameState.setCurrentPlayerIndex(-1);

    }

    // Определение победителей
    private List<GameEvent> awardPotAndEndHand(){
        List<GameEvent> events = new ArrayList<>();

        Player winner = gameState.getPlayers().stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE
                || p.getStatus() == PlayerStatus.ALL_IN).findFirst()
                .orElseThrow(() -> new IllegalStateException("Нет победителя, все сбросили!"));

        int totalPot = gameState.getPots().stream()
                .mapToInt(Pot::getAmount).sum();

        winner.setChips(winner.getChips() + totalPot);
        events.add(new PotAwarded(winner,totalPot));
        events.add(new HandEnded());

        gameState.setHandOver(true);

        return events;
    }
    // Переход в next Round
    private List<GameEvent> advanceStreet(){
        List<GameEvent> events = new ArrayList<>();
        Street currentStreet = gameState.getStreet();

        for(Player p : gameState.getPlayers()){
            p.setCurrentBet(0);
        }

        gameState.setActionsTakenThisRound(0);
        gameState.setCurrentBet(0);
        gameState.setMinRaise(gameState.getConstMinRaise());

        switch (currentStreet){
            case PREFLOP -> {
                gameState.setStreet(Street.FLOP);
                gameState.getBoard().openFlop(deck.dealCard(), deck.dealCard(), deck.dealCard());
                events.add(new StreetAdvanced(Street.FLOP, gameState.getBoard().getCards()));
            }
            case FLOP -> {
                gameState.setStreet(Street.TURN);
                gameState.getBoard().openTurn(deck.dealCard());
                events.add(new StreetAdvanced(Street.TURN, gameState.getBoard().getCards()));
            }
            case TURN -> {
                gameState.setStreet(Street.RIVER);
                gameState.getBoard().openRiver(deck.dealCard());
                events.add(new StreetAdvanced(Street.RIVER, gameState.getBoard().getCards()));
            }
            case RIVER -> {
                gameState.setStreet(Street.SHOWDOWN);
                events.addAll(showdown()); // Вызов вскрытия (напишем позже)
                return events; // Showdown сам завершит раздачу, ход передавать не надо
            }
        }

        setFirstToActPostFlop(); // передача ходу тому, у кого small blind


        return events;

    }
    // Вскрытие
    private List<GameEvent> showdown(){
        List<GameEvent> events = new ArrayList<>();

        List<Player> contenders = gameState.getPlayers().stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE
                || p.getStatus() == PlayerStatus.ALL_IN)
                .toList();

        if(contenders.isEmpty()){
            throw new IllegalStateException("Нет игроков для вскрытия!");
        }

        HandEvaluator evaluator = new HandEvaluator(gameState.getBoard());
        Map<Player, HandValue> hands = new HashMap<>();

        for(Player p : contenders){
            HandValue value = evaluator.evaluate(p);
            hands.put(p, value);

            String handDesc = value.getHandRank().toString();
            events.add(new ShowdownShown(p,p.getHoleCards(),handDesc));
        }

        HandValue bestHandValue = hands.values().stream()
                .max(HandValue::compareTo)
                .orElseThrow();

        List<Player> winners = hands.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(bestHandValue) == 0)
                .map(Map.Entry::getKey)
                .toList();

        for(Pot pot : gameState.getPots()){
            List<Player> eligibleWinners = winners.stream()
                    .filter(pot.getEligiblePlayers()::contains)
                    .toList();

            if(eligibleWinners.isEmpty()){
                continue;
            }

            Map<Player, Integer> shares = pot.splitPot(eligibleWinners);
            for(Map.Entry<Player,Integer> entry : shares.entrySet()){
                Player winner = entry.getKey();
                int amount = entry.getValue();
                winner.setChips(winner.getChips() + amount);
                events.add(new PotAwarded(winner,amount));
            }

            events.add(new HandEnded());
            gameState.setHandOver(true);
        }

        return events;
    }
    // передача хода игроку в начале круга(small blind или тот кто после)
    private void setFirstToActPostFlop(){
        int totalPlayers = gameState.getPlayers().size();
        int dealerIndex = gameState.getDealerIndex();

        for(int i = 1; i <= totalPlayers; i++){
            int nextIndex = (dealerIndex + i) % totalPlayers;
            Player player = gameState.getPlayers().get(nextIndex);
            if(player.getStatus() == PlayerStatus.ACTIVE){
                gameState.setCurrentPlayerIndex(nextIndex);
                return;
            }
        }

        gameState.setCurrentPlayerIndex(-1);
    }

    private void handleFold(Player player, List<GameEvent> events, Action action) {
        player.setStatus(PlayerStatus.FOLDED);
        events.add(new PlayerActed(player,action));
    }

    private void handleCheck(Player player, List<GameEvent> events, Action action) {
        events.add(new PlayerActed(player,action));
    }

    private void handleCall(Player player, List<GameEvent> events, Action action) {
        int toCall = gameState.getCurrentBet() - player.getCurrentBet();

        if(toCall >= player.getChips()){
            toCall = player.getChips();
            player.setStatus(PlayerStatus.ALL_IN);
            events.add(new PlayerWentAllIn(player,toCall));
        }

        player.setChips(player.getChips() - toCall);
        player.setCurrentBet(player.getCurrentBet() + toCall);
        player.setTotalContribution(player.getTotalContribution() + toCall);

        gameState.getPots().get(0).add(toCall); // ПОКА ЧТО ДОБАВЛЯЕМ В ГЛАВНЫЙ БАНК, ПОБОЧНЫЕ СДЕЛАТЬ


        events.add(new PlayerActed(player,action));
    }

    private void handleRaise(Player player, List<GameEvent> events, Action action) {
        int raiseTo = action.amount();
        int toCall = raiseTo - player.getCurrentBet();

        if(toCall >= player.getChips()){
            handleAllIn(player,events,new Action(ActionType.ALL_IN));
            return;
        }

        int raiseSize = raiseTo - gameState.getCurrentBet();
        gameState.setCurrentBet(raiseTo);
        gameState.setMinRaise(raiseSize);
        gameState.getPots().get(0).add(raiseSize);

        player.setChips(player.getChips() - toCall);
        player.setCurrentBet(raiseTo);
        player.setTotalContribution(player.getTotalContribution() + toCall);

        events.add(new PlayerActed(player,action));
    }

    private void handleAllIn(Player player, List<GameEvent> events, Action action) {
        int allChips = player.getChips();
        int newTotalBet = player.getCurrentBet() + allChips;

        player.setChips(0);
        player.setCurrentBet(newTotalBet);
        player.setTotalContribution(player.getTotalContribution() + allChips);
        player.setStatus(PlayerStatus.ALL_IN);

        gameState.getPots().get(0).add(allChips);

        if(newTotalBet > gameState.getCurrentBet()){
            int raiseSize = newTotalBet - gameState.getCurrentBet();
            gameState.setCurrentBet(newTotalBet);
            if(raiseSize >= gameState.getMinRaise()){
                gameState.setMinRaise(raiseSize);
            }
        }

        events.add(new PlayerActed(player,action));
        events.add(new PlayerWentAllIn(player,allChips));
    }


    private boolean isRoundComplete() {
        List<Player> activePlayers = gameState.getPlayers().stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE).toList();


        if(activePlayers.size() <= 1){
            return true;
        }

        boolean allAccepted = true;
        for(Player p : activePlayers){
            if(p.getCurrentBet() < gameState.getCurrentBet()){
                allAccepted = false;
                break;
            }
        }
        if(!allAccepted) return false;

        return gameState.getActionsTakenThisRound() >= activePlayers.size();
    }

    public boolean isHandOver(){
        if(gameState.isHandOver()){
            return true;
        }

        long activeCount = gameState.getPlayers().stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE
                || p.getStatus() == PlayerStatus.ALL_IN).count();

        return activeCount <= 1;
    }

    /**
     * Игрок, который на котором сейчас ход.
     * @return
     */
    public Player getCurrentPlayer(){
        return gameState.getPlayers().get(gameState.getCurrentPlayerIndex());
    }

    /**
     * Список действий, доступных игроку
     * @return
     */
    public List<Action> getLegalActions(){
        List<Action> legalActions = new ArrayList<>();
        Player player = getCurrentPlayer();

        // Fold доступен всегда
        legalActions.add(new Action(ActionType.FOLD));

        if(player.getCurrentBet() < gameState.getCurrentBet()){
            legalActions.add(new Action(ActionType.CALL));
        }

        if(player.getChips() > 0){
            legalActions.add(new Action(ActionType.ALL_IN));
        }

        int minRaise = gameState.getCurrentBet() + gameState.getMinRaise();
        int toCallForMinRaise = minRaise - player.getCurrentBet();

        if(player.getChips() >= toCallForMinRaise){
            legalActions.add(new Action(ActionType.RAISE, minRaise));
        }

        return legalActions;
    }

    /**
     * Если все ALL IN открываем все карты, если все OUT просто завершаем
     * @return
     */
    public List<GameEvent> advanceGame(){
        if(isHandOver()){
            return List.of();
        }

        if(isRoundComplete()){
            return advanceStreet();
        }

        advanceToNextPlayer();
        return List.of();
    }

    // Поставить блайнд
    private void postBlind(Player player, int amount, List<GameEvent> events, boolean isSmall) {
        int actualAmount = Math.min(amount, player.getChips());
        player.setChips(player.getChips() - actualAmount);
        player.setCurrentBet(actualAmount);
        player.setTotalContribution(player.getTotalContribution() + actualAmount);

        gameState.getPots().get(0).add(actualAmount);

        events.add(new BlindsPosted(player, actualAmount, isSmall));
    }

}