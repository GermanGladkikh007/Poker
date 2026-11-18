package org.example.model.game;

import org.example.ConsoleView;
import org.example.model.Action;
import org.example.model.player.Player;

import java.util.List;

public class Game {

    private final GameEngine gameEngine;
    private final GameConfig gameConfig;
    private final ConsoleView consoleView;

    public Game(GameConfig gameConfig, ConsoleView consoleView) {
        this.gameConfig = gameConfig;
        this.consoleView = consoleView;
        this.gameEngine = new GameEngine(gameConfig);
    }

    public void play() {
        consoleView.renderMessage("=== Игра начинается! ===");
        playHand();
        consoleView.renderMessage("=== Игра окончена! ===");
    }

    private void playHand(){


        List<GameEvent> events = gameEngine.startHand();
        consoleView.render(events);

        while(!gameEngine.isHandOver()){
            step();
        }

        consoleView.renderMessage("Раздача окончена");
    }

    private void step() {
        Player current = gameEngine.getCurrentPlayer();
        List<Action> legalActions = gameEngine.getLegalActions();
        Action action = current.decide(gameEngine.getState(), legalActions);

        List<GameEvent> events = gameEngine.applyAction(action);

        consoleView.render(events);
    }

    private boolean isGameOver() {

    }
}
