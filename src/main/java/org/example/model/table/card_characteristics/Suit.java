package org.example.model.table.card_characteristics;

/**
 *  Масть карты
 */
public enum Suit {
    HEARTS("♥"),
    DIAMONDS("♦"),
    CLUBS("♣"),
    SPADES("♠");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return symbol; }
}