package org.example.model.game.events;

import org.example.model.game.GameEvent;

// 3. Карты розданы
public record CardsDealt() implements GameEvent {
    @Override
    public String describe() {
        return "Карты розданы";
    }
}
