package org.example.model.table;

import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;

/**
 * Карта - достоинство и масть
 * @param rank
 * @param suit
 */
public record Card(Rank rank, Suit suit) {
}
