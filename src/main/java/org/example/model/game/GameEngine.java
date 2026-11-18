package org.example.model.game;

public class GameEngine {

    private final GameConfig gameConfig;
    private final GameState gameState;


    public GameEngine(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.gameState = new GameState(3);
    }

}


public class GameEngine {
    private final GameState state;

    /** Начать раздачу: карты, блайнды */
    public List<GameEvent> startHand() { ... }

    /** Применить действие игрока */
    public List<GameEvent> applyAction(Action action) { ... }

    /** Кто сейчас ходит */
    public Player getCurrentPlayer() { ... }

    /** Легальные действия для текущего игрока */
    public List<Action> getLegalActions() { ... }

    /** Закончилась ли раздача */
    public boolean isHandOver() { ... }

    /** Снимок состояния */
    public GameState getState() { return state; }
}