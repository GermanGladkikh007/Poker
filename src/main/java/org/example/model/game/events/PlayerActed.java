package org.example.model.game.events;

import org.example.model.action.Action;
import org.example.model.player.Player;

// 4. Действие игрока
public record PlayerActed(Player player, Action action) implements GameEvent {
    @Override
    public String describe() {
        return player.getName() + " " + action.toString();
    }
}
