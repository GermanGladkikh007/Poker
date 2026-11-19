package org.example.model.game.events;

import org.example.model.player.Player;

// 8. Банк выдан
public record PotAwarded(Player player, int amount) implements GameEvent {
    @Override
    public String describe() {
        return player.getName() + " забирает " + amount;
    }
}
