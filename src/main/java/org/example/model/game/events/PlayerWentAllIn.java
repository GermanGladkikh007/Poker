package org.example.model.game.events;

import org.example.model.game.GameEvent;
import org.example.model.player.Player;

// 5. All-in (доп.)
public record PlayerWentAllIn(Player player, int amount) implements GameEvent {
    @Override
    public String describe() {
        return player.getName() + " идёт ва-банк (" + amount + ")";
    }
}
