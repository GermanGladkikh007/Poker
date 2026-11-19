package org.example.model.game.events;

// 9. Конец раздачи
public record HandEnded(int handNumber) implements GameEvent {
    @Override
    public String describe() {
        return "=== Раздача #" + handNumber + " окончена ===";
    }
}
