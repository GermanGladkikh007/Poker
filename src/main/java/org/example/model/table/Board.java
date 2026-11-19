package org.example.model.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Текущая раздача
 */
public class Board {
    private final List<Card> cards;

    public Board() {
        this.cards = new ArrayList<>();
    }

    /**
     * Открыть флоп - первые 3 карты
     * @param c1
     * @param c2
     * @param c3
     */
    public void openFlop(Card c1, Card c2, Card c3) {
        if(!cards.isEmpty()) throw new IllegalArgumentException("Флоп уже открыт");
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
    }

    /**
     * Открыть терн - следующую карту после флопа
     * @param c
     */
    public void openTurn(Card c) {
        if(cards.size() != 3) throw new IllegalArgumentException("Тёрн можно открыть только после флопа");
        cards.add(c);
    }

    /**
     * Открыть ривер - последнюю карту
     * @param c
     */
    public void openRiver(Card c) {
        if(cards.size() != 4) throw new IllegalArgumentException("Ривер можно открыть только после тёрна");
        cards.add(c);
    }

    /**
     * Read-only список карт раздачи
     * @return
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /**
     * Сколько карт открыто
     * @return
     */
    public int size(){
        return cards.size();
    }

    /**
     * Очистить стол для новой раздачи
     */
    public void clear(){
        cards.clear();
    }
}


