package org.example.model.game;

import org.example.ConsoleView;
import org.example.model.action.Action;
import org.example.model.game.events.GameEvent;
import org.example.model.player.Player;

import java.util.List;


/**
 * Точка входа в игру
 */
public class Game {

    private final GameEngine gameEngine;
    private final GameConfig gameConfig;
    private final ConsoleView consoleView;

    public Game(GameConfig gameConfig, ConsoleView consoleView) {
        this.gameConfig = gameConfig;
        this.consoleView = consoleView;
        this.gameEngine = null;
    }

    /**
     * Запуск игры
     */
    public void play() {
        consoleView.renderMessage("=== Игра начинается! ===");
        playHand();
        consoleView.renderMessage("=== Игра окончена! ===");
    }


    /**
     * Запуск раздачи
     */
    private void playHand(){
        List<GameEvent> events = gameEngine.startHand();
        consoleView.render(events);

        while(!gameEngine.isHandOver()){
            // если все all in, вскрываем, если нет просто обычный ход игрока
            if(gameEngine.getGameState().getCurrentPlayerIndex() ==-1){

                List<GameEvent> autoEvents = gameEngine.advanceGame();
                consoleView.render(autoEvents);
            }else{
                step();
            }

        }

        consoleView.renderMessage("Раздача окончена");
    }


    /**
     * Ход игрока
     */
    private void step() {
        // Если никто не ходит, значит либо все ALL IN либо OUT
        if(gameEngine.getGameState().getCurrentPlayerIndex() == -1){
            List<GameEvent> events = gameEngine.advanceGame();
            consoleView.render(events);
            return;
        }

        Player current = gameEngine.getCurrentPlayer();
        List<Action> legalActions = gameEngine.getLegalActions();
        Action action = current.decide(gameEngine.getGameState(), legalActions);

        List<GameEvent> events = gameEngine.applyAction(action);

        consoleView.render(events);
    }

}
