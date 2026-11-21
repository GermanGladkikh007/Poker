package org.example.model.hand;

import org.example.model.player.Player;
import org.example.model.table.Board;
import org.example.model.table.Card;
import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;

import java.util.*;


/**
 * Класс оценки силы руки
 */
public class HandEvaluator {

    private final Board board;

    public HandEvaluator(Board board) {
        this.board = board;
    }


    /**
     * Определение силы руки
     * @param player
     * @return
     */
    public HandValue evaluate(Player player) {
        List<Card> holeCards = player.getHoleCards();
        List<Card> allCards = new ArrayList<Card>(holeCards);
        allCards.addAll(board.getCards());

        if(allCards.size() < 5){
            throw new IllegalStateException("Для оценки руки нужно минимум 5 карт");
        }
        HandValue bestHandValue = null;

        for (int i = 0; i < allCards.size() - 4; i++) {
            for (int j = i + 1; j < allCards.size() - 3; j++) {
                for (int k = j + 1; k < allCards.size() - 2; k++) {
                    for (int l = k + 1; l < allCards.size() - 1; l++) {
                        for (int m = l + 1; m < allCards.size(); m++) {

                            List<Card> fiveCards = List.of(
                                    allCards.get(i),
                                    allCards.get(j),
                                    allCards.get(k),
                                    allCards.get(l),
                                    allCards.get(m)
                            );

                            HandValue currentValue = evaluateFiveCards(fiveCards);

                            if(bestHandValue == null || currentValue.compareTo(bestHandValue) > 0){
                                bestHandValue = currentValue;
                            }
                        }
                    }
                }
            }
        }
        return bestHandValue;
    }

    private HandValue evaluateFiveCards(List<Card> fiveCards) {
        List<Card> sorted = fiveCards.stream()
                .sorted((a,b) -> Integer.compare(b.rank().getValue(), a.rank().getValue()))
                .toList();

        // считаем сколько каждого ранга карт
        Map<Integer, Integer> counts = countRanks(sorted);

        // проверка на флеш и стрит
        boolean flush = isFlush(sorted);
        boolean straight = isStraight(sorted);

        // отработка всех комбинаций от старшей к младшей
        if(flush && straight && sorted.get(0).rank() == Rank.ACE && sorted.get(1).rank() == Rank.KING){
            return new HandValue(HandRank.ROYAL_FLUSH,List.of());
        }

        if(straight && flush){
            return new HandValue(HandRank.STRAIGHT_FLUSH,List.of());
        }

        if(counts.containsValue(4)){
            int quad = getKeyByValue(counts, 4);
            int kicker = getKeyByValue(counts, 1);
            return new HandValue(HandRank.FOUR_OF_A_KIND, List.of(quad, kicker));
        }

        if (counts.containsValue(3) && counts.containsValue(2)) {
            int three = getKeyByValue(counts, 3);
            int pair = getKeyByValue(counts, 2);
            return new HandValue(HandRank.FULL_HOUSE, List.of(three, pair));
        }

        if (flush) {
            List<Integer> kickers = sorted.stream().map(c -> c.rank().getValue()).toList();
            return new HandValue(HandRank.FLUSH, kickers);
        }

        if (straight) {
            return new HandValue(HandRank.STRAIGHT, List.of(getHighCardValue(sorted)));
        }

        if (counts.containsValue(3)) {
            int three = getKeyByValue(counts, 3);
            List<Integer> kickers = new ArrayList<>();
            for (Card c : sorted) {
                if (c.rank().getValue() != three) kickers.add(c.rank().getValue());
            }
            return new HandValue(HandRank.THREE_OF_A_KIND,
                    List.of(three, kickers.get(0), kickers.get(1)));
        }

        if (counts.values().stream().filter(v -> v == 2).count() == 2) {
            List<Integer> pairs = new ArrayList<>(getKeysByValue(counts, 2));
            pairs.sort(Comparator.reverseOrder());
            int kicker = getKeyByValue(counts, 1);
            return new HandValue(HandRank.TWO_PAIR,
                    List.of(pairs.get(0), pairs.get(1), kicker));
        }


        if (counts.containsValue(2)) {
            int pair = getKeyByValue(counts, 2);
            List<Integer> kickers = new ArrayList<>();
            for (Card c : sorted) {
                if (c.rank().getValue() != pair) kickers.add(c.rank().getValue());
            }
            kickers = kickers.subList(0, 3); // 3 кикера
            return new HandValue(HandRank.ONE_PAIR,
                    List.of(pair, kickers.get(0), kickers.get(1), kickers.get(2)));
        }


        // старшая карта, если нет комбинаций
        // по-другому это отсортированные кикеры
        List<Integer> kickers = sorted.stream().map(c -> c.rank().getValue()).toList();

        return new HandValue(HandRank.HIGH_CARD, kickers);
    }

    private boolean isStraight(List<Card> sortedCards) {
        boolean normalStraight = true;
        for(int i = 1; i < sortedCards.size(); i++ ){
            int prev = sortedCards.get(i-1).rank().getValue();
            int curr = sortedCards.get(i).rank().getValue();
            if(Math.abs(curr - prev) != 1) {normalStraight =  false;break;}
        }
        if(normalStraight){return true;};

        return sortedCards.get(0).rank() == Rank.ACE
                && sortedCards.get(1).rank() == Rank.FIVE
                && sortedCards.get(2).rank() == Rank.FOUR
                && sortedCards.get(3).rank() == Rank.THREE
                && sortedCards.get(4).rank() == Rank.TWO;
    }

    private boolean isFlush(List<Card> cards) {
        Suit suit = cards.get(0).suit();
        return cards.stream().allMatch(card -> suit.equals(card.suit()));
    }

    private Map<Integer,Integer> countRanks(List<Card> cards){
        Map<Integer,Integer> counts = new HashMap<>();
        for(Card card : cards){
            counts.merge(card.rank().getValue(), 1, Integer::sum);
        }
        return counts;
    }


    private int getHighCardValue(List<Card> sorted) {
        return sorted.get(0).rank().getValue();
    }

    private int getKeyByValue(Map<Integer, Integer> map, int value) {
        return map.entrySet().stream()
                .filter(e -> e.getValue() == value)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    private List<Integer> getKeysByValue(Map<Integer, Integer> map, int value) {
        return map.entrySet().stream()
                .filter(e -> e.getValue() == value)
                .map(Map.Entry::getKey)
                .toList();
    }

}
