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
        public Action decide(GameState state, List<Action> legalActions) {
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

        System.out.println(engine.getGameState().getStreet());
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

    /*
    @Test
    void testSplitPotOnShowdownTie() {
        List<Player> twoPlayers = new ArrayList<>();
        twoPlayers.add(new Player(1, "P1", 1000, new StubDecisionMaker()));
        twoPlayers.add(new Player(2, "P2", 1000, new StubDecisionMaker()));

        GameConfig config = new GameConfig(42, 100, 50);
        GameEngine twoPlayerEngine = new GameEngine(config, new GameState(twoPlayers,new Board(),0,0));
        twoPlayerEngine.startHand();

        // Принудительно задаем одинаковые руки (например, оба имеют старшую карту A)
        // (Для этого можно подменить карты на борде и в руках, но так как у нас нет прямого доступа,
        // можно использовать рефлексию или сеттеры, если они есть).

        // Пропускаем торговлю (все чек до ривера)
        twoPlayerEngine.applyAction(new Action(ActionType.CALL));
        twoPlayerEngine.applyAction(new Action(ActionType.CHECK));
        // ... (флоп, тёрн, ривер - все чек)

        // Проверяем, что банк разделен поровну (если карты совпали)
        // (Этот тест лучше делать, когда есть возможность подмены колоды).
    } */
}
