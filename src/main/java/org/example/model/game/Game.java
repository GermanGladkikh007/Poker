package org.example.model.game;

import org.example.ConsoleView;
import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.events.GameEvent;
import org.example.model.player.Player;
import org.example.model.table.Board;

import java.util.List;


/**
 * Точка входа в игру
 */
public class Game {

    private final GameEngine gameEngine;
    private final GameConfig gameConfig;
    private final ConsoleView consoleView;
    private List<Player> players;

    public Game(GameConfig gameConfig, List<Player> players, ConsoleView consoleView) {
        this.gameConfig = gameConfig;
        this.consoleView = consoleView;
        this.players = players;
        this.gameEngine = new GameEngine(gameConfig,new GameState(players,new Board(),0,0));
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
        System.out.println("DEBUG: Ход игрока " + current.getName() + ", индекс " + gameEngine.getGameState().getCurrentPlayerIndex());
        List<ActionType> legalActions = gameEngine.getLegalActions();
        Action action = current.decide(gameEngine.getGameState(), legalActions);

        List<GameEvent> events = gameEngine.applyAction(action);

        consoleView.render(events);
    }

}
