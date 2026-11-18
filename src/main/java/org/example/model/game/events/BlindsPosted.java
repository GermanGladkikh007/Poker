package org.example.model.game.events;

import org.example.model.game.GameEvent;
import org.example.model.player.Player;

// 2. Блайнды
public record BlindsPosted(Player player, int amount, boolean isSmallBlind) implements GameEvent {
    @Override
    public String describe() {
        String type = isSmallBlind ? "малый" : "большой";
        return player.getName() + " ставит " + type + " блайнд " + amount;
    }
}
