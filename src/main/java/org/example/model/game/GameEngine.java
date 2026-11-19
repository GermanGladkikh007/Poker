package org.example.model.game;

import org.example.model.action.Action;
import org.example.model.game.events.GameEvent;
import org.example.model.player.PlayerStatus;
import org.example.model.game.events.PlayerActed;
import org.example.model.game.events.StreetAdvanced;
import org.example.model.player.Player;
import org.example.model.table.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {

    private final GameConfig gameConfig;
    private final GameState gameState;

    private final Random random;
    private Deck deck;

    public GameEngine(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.gameState = new GameState();
        this.random = new Random(gameConfig.seed());
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<GameEvent> startHand(){
        List<GameEvent> events = new ArrayList<>();
        this.deck = new Deck(random);
        return events;
    }

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

        if(isRoundComplete()){
            events.addAll(new StreetAdvanced(street))
        }
        return events;
    }


    private void handleFold(Player player, List<GameEvent> events, Action action) {
        player.setStatus(PlayerStatus.FOLDED);
        events.add(new PlayerActed(player,action));
    }

    private void handleCheck(Player player, List<GameEvent> events, Action action) {

    }

    private void handleCall(Player player, List<GameEvent> events, Action action) {
    }

    private void handleRaise(Player player, List<GameEvent> events, Action action) {
    }

    private void handleAllIn(Player player, List<GameEvent> events, Action action) {
    }


    private boolean isRoundComplete() {}

    private boolean isHandOver(){
        if(gameState.isHandOver()){
            return true;
        }
    }

    public Player getCurrentPlayer(){
        return gameState.getPlayers().get(gameState.getCurrentPlayerIndex());
    }

    public List<Action> getLegalActions(){
        List<Action> legalActions = new ArrayList<>();

        return legalActions;
    }
}


