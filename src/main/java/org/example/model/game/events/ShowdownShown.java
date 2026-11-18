package org.example.model.game.events;

import org.example.model.game.GameEvent;
import org.example.model.player.Player;
import org.example.model.table.Card;

import java.util.List;

// 7. Вскрытие
public record ShowdownShown(Player player, List<Card> holeCards, String handDescription) implements GameEvent {
    @Override
    public String describe() {
        return player.getName() + " показывает " + holeCards + " (" + handDescription + ")";
    }
}
