package org.example.model.game.events;

public record HandStarted() implements GameEvent {
    @Override
    public String describe() {
        return "Раздача произведена";
    }
}
