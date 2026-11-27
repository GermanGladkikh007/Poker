import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.GameConfig;
import org.example.model.game.GameEngine;
import org.example.model.game.GameState;
import org.example.model.game.Street;
import org.example.model.game.events.GameEvent;
import org.example.model.player.DecisionMaker;
import org.example.model.player.Player;
import org.example.model.player.PlayerStatus;
import org.example.model.table.Board;
import org.example.model.table.Card;
import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class GameProcessTest {

    private GameEngine engine;
    private List<Player> players;

    private static class StubDecisionMaker implements DecisionMaker {
        @Override
        public Action decide(GameState state, List<ActionType> legalActions) {
            return new Action(ActionType.FOLD);
        }
    }

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player(1, "P1", 1000, new StubDecisionMaker()));
        players.add(new Player(2, "P2", 1000, new StubDecisionMaker()));
        players.add(new Player(3, "P3", 1000, new StubDecisionMaker()));

        GameConfig config = new GameConfig(42,50,100);
        GameState gameState = new GameState(players,new Board(),0,0);
        engine = new GameEngine(config,gameState);
    }

    private List<Card> parseCards(String... cardStrings) {
        List<Card> cards = new ArrayList<>();
        for(String s : cardStrings) {
            String rankPart = s.substring(0,s.length()-1);
            String suitPart = s.substring(s.length()-1);

            Rank rank = switch (rankPart) {
                case "2" -> Rank.TWO; case "3" -> Rank.THREE; case "4" -> Rank.FOUR;
                case "5" -> Rank.FIVE; case "6" -> Rank.SIX; case "7" -> Rank.SEVEN;
                case "8" -> Rank.EIGHT; case "9" -> Rank.NINE; case "10" -> Rank.TEN;
                case "J" -> Rank.JACK; case "Q" -> Rank.QUEEN; case "K" -> Rank.KING;
                case "A" -> Rank.ACE;
                default -> throw new IllegalArgumentException("Неизвестный ранг: " + rankPart);
            };

            Suit suit = switch (suitPart) {
                case "♥" -> Suit.HEARTS; case "♦" -> Suit.DIAMONDS;
                case "♣" -> Suit.CLUBS; case "♠" -> Suit.SPADES;
                default -> throw new IllegalArgumentException("Неизвестная масть: " + suitPart);
            };

            cards.add(new Card(rank, suit));
        }
        return cards;
    }

    @Test
    void testStartHandPostsBlinds(){
        List<GameEvent> events = engine.startHand();

        assertFalse(events.isEmpty());

        Player sbPlayer = players.get(1);
        Player bbPlayer = players.get(2);

        assertEquals(950, sbPlayer.getChips());
        assertEquals(900, bbPlayer.getChips());
        assertEquals(150,engine.getGameState().getPots().get(0).getAmount());
        assertEquals(0,engine.getGameState().getCurrentPlayerIndex());
    }

    @Test
    void testFoldEndsHand(){
        engine.startHand();
        engine.applyAction(new Action(ActionType.FOLD));
        engine.applyAction(new Action(ActionType.FOLD));
        assertTrue(engine.isHandOver());
        assertEquals(1050,players.get(2).getChips());
    }


    @Test
    void testCallAndCheck(){
        engine.startHand();

        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CHECK));

        assertEquals(Street.FLOP, engine.getGameState().getStreet());
        assertEquals(3, engine.getGameState().getBoard().getCards().size());
        assertEquals(1, engine.getGameState().getCurrentPlayerIndex());
    }

    @Test
    void testRaiseUpdatesMinRaise(){
        engine.startHand();

        engine.applyAction(new Action(ActionType.RAISE,300));

        assertEquals(300, engine.getGameState().getCurrentBet());
        assertEquals(200,engine.getGameState().getMinRaise());

        engine.applyAction(new Action(ActionType.RAISE,500));
        assertEquals(500, engine.getGameState().getCurrentBet());
        assertEquals(200,engine.getGameState().getMinRaise());
    }

    @Test
    void testHandOverWithAllIn(){
        players.get(0).setChips(200);
        players.get(1).setChips(200);
        players.get(2).setChips(200);

        engine.startHand();

        engine.applyAction(new Action(ActionType.ALL_IN));
        engine.applyAction(new Action(ActionType.ALL_IN));
        engine.applyAction(new Action(ActionType.ALL_IN));

        while (engine.getGameState().getCurrentPlayerIndex() == -1 && !engine.isHandOver()) {
            engine.advanceGame(); // Прокручиваем улицы
        }

        assertTrue(engine.getGameState().getStreet() == Street.SHOWDOWN);
        assertTrue(engine.isHandOver());
    }

    @Test
    void testStartHandDealsCardsAndBlinds(){
        engine.startHand();

        for(Player p : players){
            assertEquals(2,p.getHoleCards().size());
        }

        assertEquals(150,engine.getGameState().getPots().get(0).getAmount());
    }

    @Test
    void testCallDeductsChipsAndMatchesBet(){
        engine.startHand();
        Player p0 = players.get(0);

        engine.applyAction(new Action(ActionType.CALL));

        assertEquals(900, p0.getChips());
        assertEquals(100,p0.getCurrentBet());
        assertEquals(250,engine.getGameState().getPots().get(0).getAmount());
    }

    @Test
    void testRaiseUpdatesState(){
        engine.startHand();
        Player p0 = players.get(0);

        engine.applyAction(new Action(ActionType.RAISE,300));

        assertEquals(700,p0.getChips());
        assertEquals(300,engine.getGameState().getCurrentBet());
        assertEquals(200,engine.getGameState().getMinRaise());

    }

    @Test
    void testFoldSetStatus(){
        engine.startHand();
        Player p0 = players.get(0);
        engine.applyAction(new Action(ActionType.FOLD));
        assertEquals(PlayerStatus.FOLDED, p0.getStatus());
    }

    @Test
    void testAllInDeductsAllChips(){
        engine.startHand();
        Player p0 = players.get(0);
        p0.setChips(150);

        engine.applyAction(new Action(ActionType.ALL_IN));

        assertEquals(0,p0.getChips());
        assertEquals(PlayerStatus.ALL_IN,p0.getStatus());
    }

    @Test
    void testRoundCompetitionAdvancesToFlop(){
        engine.startHand();

        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CHECK));

        assertEquals(Street.FLOP,engine.getGameState().getStreet());
        assertEquals(3,engine.getGameState().getBoard().getCards().size());
    }

    @Test
    void testFullStreetProgressionToShowdown() {
        engine.startHand();

        // Префлоп (все колл/чек)
        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CHECK));

        // Флоп (все чек)
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));


        // Тёрн (все чек)
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));


        // Ривер (все чек)
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));

        // Должно дойти до Showdown и завершиться
        assertTrue(engine.isHandOver());
        assertEquals(Street.SHOWDOWN, engine.getGameState().getStreet());
    }

    @Test
    void testWinWithoutShowdown(){
        engine.startHand();
        engine.applyAction(new Action(ActionType.FOLD));
        engine.applyAction(new Action(ActionType.FOLD));
        assertTrue(engine.isHandOver());
        assertEquals(1050,players.get(2).getChips());
    }


    @Test
    void testSplitPotOnShowdownTie() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "P1", 1000, new StubDecisionMaker()));
        players.add(new Player(2, "P2", 1000, new StubDecisionMaker()));

        GameConfig config = new GameConfig(42, 50,100);
        GameState state = new GameState(players, new Board(parseCards("A♥", "K♥", "Q♥", "J♥", "10♥")),0,0);
        GameEngine engine = new GameEngine(config,state);
        engine.startHand();

        // 2. Подменяем карты на столе на Роял-флеш
        // Оба игрока его соберут, так как он лежит на столе целиком.
        engine.getGameState().getBoard();

        // 3. Задаем игрокам любые карты (они не будут использоваться в комбинации)
        players.get(0).setHoleCards(parseCards("2♣", "3♦"));
        players.get(1).setHoleCards(parseCards("4♣", "5♦"));

        // 4. Торговля до Ривера (все чек/колл)
        // Префлоп
        engine.applyAction(new Action(ActionType.CALL));
        engine.applyAction(new Action(ActionType.CHECK));
        // Флоп
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        // Тёрн
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        // Ривер
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));

        // 5. Проверяем результат
        assertTrue(engine.isHandOver());

        // Банк был 150. Делится по 75.
        // P1 (SB): 1000 - 50 + 75 = 1025
        // P2 (BB): 1000 - 100 + 75 = 975
        assertEquals(1000, players.get(0).getChips());
        assertEquals(1000, players.get(1).getChips());
    }

    @Test
    void testSidePotsWithThreePlayers(){

        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "P1", 100, new StubDecisionMaker()));  // Маленький стек
        players.add(new Player(2, "P2", 500, new StubDecisionMaker()));  // Средний стек
        players.add(new Player(3, "P3", 1000, new StubDecisionMaker())); // Большой стек

        GameConfig config = new GameConfig(42, 50,100);
        GameState state = new GameState(players, new Board(parseCards("A♠", "K♠", "Q♠", "J♠", "2♣")), 0,0);
        GameEngine engine = new GameEngine(config,state);

        engine.startHand();

        players.get(0).setHoleCards(parseCards("10♥", "3♦"));
        players.get(1).setHoleCards(parseCards("A♥", "2♦"));
        players.get(2).setHoleCards(parseCards("4♥", "5♦"));

        engine.applyAction(new Action(ActionType.ALL_IN));
        engine.applyAction(new Action(ActionType.ALL_IN));
        engine.applyAction(new Action(ActionType.CALL));

        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));
        engine.applyAction(new Action(ActionType.CHECK));


        assertTrue(engine.isHandOver());
        assertEquals(300,players.get(0).getChips());
        assertEquals(800,players.get(1).getChips());
        assertEquals(500,players.get(2).getChips());

    }

}
