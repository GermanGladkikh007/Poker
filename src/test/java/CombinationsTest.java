import org.example.model.hand.HandEvaluator;
import org.example.model.hand.HandRank;
import org.example.model.hand.HandValue;
import org.example.model.player.Player;
import org.example.model.table.Board;
import org.example.model.table.Card;
import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombinationsTest {

    private final HandEvaluator evaluator = new HandEvaluator(new Board());

    private List<Card> parseCards(String... cardStrings) {
        List<Card> cards = new ArrayList<>();
        for(String s : cardStrings) {
            String rankPart = s.substring(0,s.length()-1);
            String suitPart = s.substring(s.length()-1);

            Rank rank = switch (rankPart) {
                case "2" -> Rank.TWO; case "3" -> Rank.THREE; case "4" -> Rank.FOUR;
                case "5" -> Rank.FIVE; case "6" -> Rank.SIX; case "7" -> Rank.SEVEN;
                case "8" -> Rank.EIGHT; case "9" -> Rank.NINE; case "10" -> Rank.TEN;
                case "J" -> Rank.JACK; case "Q" -> Rank.QUEEN; case "K" -> Rank.KING;
                case "A" -> Rank.ACE;
                default -> throw new IllegalArgumentException("Неизвестный ранг: " + rankPart);
            };

            Suit suit = switch (suitPart) {
                case "♥" -> Suit.HEARTS; case "♦" -> Suit.DIAMONDS;
                case "♣" -> Suit.CLUBS; case "♠" -> Suit.SPADES;
                default -> throw new IllegalArgumentException("Неизвестная масть: " + suitPart);
            };

            cards.add(new Card(rank, suit));
        }
        return cards;
    }

    private HandValue evaluate5(String... cards){
        Player p = new Player(1,"Test", 1000,null);
        p.setHoleCards(parseCards(cards));
        return evaluator.evaluate(p);
    }



    //  ТЕСТЫ КОМБИНАЦИЙ
    @Test
    void testRoyalFlush() {
        HandValue hand = evaluate5("A♥", "K♥", "Q♥", "J♥", "10♥");
        assertEquals(HandRank.ROYAL_FLUSH, hand.getHandRank());
    }

    @Test
    void testStraightFlush() {
        HandValue hand = evaluate5("9♣", "8♣", "7♣", "6♣", "5♣");
        assertEquals(HandRank.STRAIGHT_FLUSH, hand.getHandRank());
    }

    @Test
    void testFourOfAKind() {
        HandValue hand = evaluate5("A♥", "A♦", "A♣", "A♠", "K♥");
        assertEquals(HandRank.FOUR_OF_A_KIND, hand.getHandRank());
    }

    @Test
    void testFullHouse() {
        HandValue hand = evaluate5("K♥", "K♦", "K♣", "2♠", "2♥");
        assertEquals(HandRank.FULL_HOUSE, hand.getHandRank());
    }

    @Test
    void testFlush() {
        HandValue hand = evaluate5("A♦", "J♦", "8♦", "5♦", "2♦");
        assertEquals(HandRank.FLUSH, hand.getHandRank());
    }

    @Test
    void testStraight() {
        HandValue hand = evaluate5("9♥", "8♦", "7♣", "6♠", "5♥");
        assertEquals(HandRank.STRAIGHT, hand.getHandRank());
    }

    @Test
    void testAceLowStraight() {
        // A-2-3-4-5 (Уил-стрит) — это стрит, но старшая карта 5
        HandValue hand = evaluate5("A♥", "2♦", "3♣", "4♠", "5♥");
        assertEquals(HandRank.STRAIGHT, hand.getHandRank());
    }

    @Test
    void testThreeOfAKind() {
        HandValue hand = evaluate5("Q♥", "Q♦", "Q♣", "8♠", "4♥");
        assertEquals(HandRank.THREE_OF_A_KIND, hand.getHandRank());
    }

    @Test
    void testTwoPair() {
        HandValue hand = evaluate5("J♥", "J♦", "6♣", "6♠", "A♥");
        assertEquals(HandRank.TWO_PAIR, hand.getHandRank());
    }

    @Test
    void testOnePair() {
        HandValue hand = evaluate5("10♥", "10♦", "K♣", "8♠", "3♥");
        assertEquals(HandRank.ONE_PAIR, hand.getHandRank());
    }

    @Test
    void testHighCard() {
        HandValue hand = evaluate5("A♥", "K♦", "9♣", "7♠", "4♥");
        assertEquals(HandRank.HIGH_CARD, hand.getHandRank());
    }
}
