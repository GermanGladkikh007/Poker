package org.example;

import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.Game;
import org.example.model.game.GameConfig;
import org.example.model.game.GameState;
import org.example.model.player.BotDecisionMaker;
import org.example.model.player.HumanDecisionMaker;
import org.example.model.player.Player;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ConsoleView view = new ConsoleView();

        // Seed для генерации
        long seed = System.currentTimeMillis();

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Игрок 1", 1000, new HumanDecisionMaker(0)));
        players.add(new Player(2, "Aggressive Bot", 1000, new BotDecisionMaker(0.2)));
        players.add(new Player(3, "Tight Bot", 1000, new BotDecisionMaker(0.4)));
        GameState state = new GameState(10);
        GameConfig config = new GameConfig(seed,50, 100);

        Game game = new Game(config,players,view);
        game.play();
    }
}