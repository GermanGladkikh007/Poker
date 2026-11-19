package org.example;

import org.example.model.game.events.GameEvent;

import java.util.List;

public class ConsoleView {

    public void renderMessage(String message) {
        System.out.println(message);
    }

    public void render(List<GameEvent> events) {
        for(GameEvent event : events) {
            System.out.println(event.describe());
        }
    }

}
