package org.example.model.game.events;

import org.example.model.game.Street;
import org.example.model.table.Card;

import java.util.List;

// 6. Новая улица
public record StreetAdvanced(Street street, List<Card> boardCards) implements GameEvent {
    @Override
    public String describe() {
        return street + ": " + boardCards;
    }
}
