package org.example.model.game.events;

// 9. Конец раздачи
public record HandEnded() implements GameEvent {
    @Override
    public String describe() {
        return "=== Раздача #" + " окончена ===";
    }
}
