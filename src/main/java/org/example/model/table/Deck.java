package org.example.model.table;

import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Колода
 */
public class Deck {
    private final List<Card> cards;

    public Deck(Random random) {
        this.cards = createDeck();
        Collections.shuffle(this.cards, random);
    }

    private List<Card> createDeck(){
        List<Card> deck = new ArrayList<>();
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){
                deck.add(new Card(rank, suit));
            }
        }
        return deck;
    }

    /**
     * Выдать карту
     * @return
     */
    public Card dealCard() {
        if(cards.isEmpty()){
            throw new IllegalArgumentException("Колода пуста");
        }
        return cards.remove(cards.size() - 1);
    }
}
