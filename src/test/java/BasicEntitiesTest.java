import org.example.model.player.Player;
import org.example.model.table.Card;
import org.example.model.table.Deck;
import org.example.model.table.Pot;
import org.example.model.table.card_characteristics.Rank;
import org.example.model.table.card_characteristics.Suit;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BasicEntitiesTest {

    // ТЕСТЫ ДЛЯ КОЛОДЫ
    @Test
    void deckShouldHave52UniqueCards() {
        Deck deck = new Deck(new Random(42));

        Set<Card> uniqueCards = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            uniqueCards.add(deck.dealCard());
        }

        assertEquals(52, uniqueCards.size());
    }

    @Test
    void deckShouldBeDeterministicWithSameSeed(){
        Deck deck1 = new Deck(new Random(42));
        Deck deck2 = new Deck(new Random(42));

        for(int i = 0; i < 52; i++){
            assertEquals(deck1.dealCard(), deck2.dealCard());
        }
    }

    @Test
    void cardToStringShouldBeCorrect(){
        Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
        assertEquals(aceOfSpades.toString(), "A♠");
    }


    // ТЕСТЫ ДЛЯ БАНКА
    @Test
    void potSplitShouldDivideEvenly(){
        Player p1 = new Player(1, "P1", 1000, null);
        Player p2 = new Player(2, "P2", 1000, null);

        Pot pot = new Pot(101, new HashSet<>(List.of(p1,p2)));

        var shares = pot.splitPot(List.of(p1, p2));

        assertEquals(51, shares.get(p1));
        assertEquals(50, shares.get(p2));
        assertEquals(101, shares.values()
                .stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void potSplitShouldWorkForThreePlayers() {
        Player p1 = new Player(1, "P1", 1000, null);
        Player p2 = new Player(2, "P2", 1000, null);
        Player p3 = new Player(3, "P3", 1000, null);

        Pot pot = new Pot(100, new HashSet<>(List.of(p1, p2, p3)));

        var shares = pot.splitPot(List.of(p1, p2, p3));

        assertEquals(34, shares.get(p1));
        assertEquals(33, shares.get(p2));
        assertEquals(33, shares.get(p3));
        assertEquals(100, shares.values().stream().mapToInt(Integer::intValue).sum());
    }
}
