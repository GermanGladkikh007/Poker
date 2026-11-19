package org.example.model.game;

import org.example.model.player.Player;

import java.util.List;

public record GameConfig(
                         long seed,
                         int smallBlind,
                         int bigBlind) {
}

//public GameConfig build() {
//    if (players == null || players.size() < 2 || players.size() > 9) {
//        throw new IllegalArgumentException("Нужно от 2 до 9 игроков");
//    }
//    if (smallBlind <= 0 || bigBlind <= 0) {
//        throw new IllegalArgumentException("Блайнды должны быть > 0");
//    }
//    if (bigBlind < smallBlind) {
//        throw new IllegalArgumentException("BB должен быть >= SB");
//    }
//    return new GameConfig(this);
//}