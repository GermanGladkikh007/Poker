package org.example.model.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Колода
 */
public class Deck {
    private final List<Card> cards;
    private final Random random;
    private int currentIndex;

    public Deck(Random random) {
        this.random = random;
        this.cards = new ArrayList<>();
    }
}
