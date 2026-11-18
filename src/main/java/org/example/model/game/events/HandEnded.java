package org.example.model.game.events;

import org.example.model.game.GameEvent;

// 9. Конец раздачи
public record HandEnded(int handNumber) implements GameEvent {
    @Override
    public String describe() {
        return "=== Раздача #" + handNumber + " окончена ===";
    }
}
