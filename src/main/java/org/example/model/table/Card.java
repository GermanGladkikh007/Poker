package org.example.model.table;

import org.example.model.enums.Rank;
import org.example.model.enums.Suit;

/**
 * Карта - достоинство и масть
 * @param rank
 * @param suit
 */
public record Card(Rank rank, Suit suit) {
}
