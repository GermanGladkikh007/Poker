package org.example.model.table;

import org.example.model.player.Player;

import java.util.Set;


/**
 * Банк
 */
public class Pot {
    private int amount; // количество фишек
    private Set<Player> eligiblePlayers; // список игроков, имеющих право на выйгрыш
}
