package org.example.model.game;

import org.example.model.player.PlayerStatus;
import org.example.model.table.Board;
import org.example.model.player.Player;
import org.example.model.table.Pot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Состояние игры(игроки, раздача, банки)
 */
public class GameState {
    private final List<Player> players;
    private final Board board;
    private List<Pot> pots;
    private Street street;
    private int currentPlayerIndex;
    private int dealerIndex;
    private int currentBet; // максимальная ставка, которую нужно уровнять
    private boolean isHandOver;
    private final int constMinRaise;
    private int minRaise;
    private int actionsTakenThisRound = 0;

    public GameState(int minRaise) {
        this.players = new ArrayList<>();
        this.board = new Board();
        this.pots = new ArrayList<>();
        this.street = Street.PREFLOP;
        this.currentBet = 0;
        this.isHandOver = false;
        this.constMinRaise = minRaise;
        this.minRaise = minRaise;
    }

    public GameState(List<Player> players, Board board,
                     int dealerIndex, int minRaise) {
        this.players = players;
        this.board = board;
        this.pots = new ArrayList<>();
        pots.add(new Pot(0,new HashSet<>(players)));
        this.street = Street.PREFLOP;
        this.dealerIndex = dealerIndex;
        this.currentBet = 0;
        this.isHandOver = false;
        this.constMinRaise = minRaise;
        this.minRaise = minRaise;
    }

    public void resetForNewHand() {
        board.clear();
        pots.clear();
        pots.add(new Pot(0,new HashSet<>(players)));
        street = Street.PREFLOP;
        currentBet = 0;
        isHandOver = false;
        minRaise = constMinRaise;
        actionsTakenThisRound = 0;
        for (Player player : players) {
            player.getHoleCards().clear();
            player.setCurrentBet(0);
            player.setTotalContribution(0);
            player.setStatus(player.getChips() > 0 ? PlayerStatus.ACTIVE : PlayerStatus.OUT);
        }
    }

    public int getActionsTakenThisRound() {
        return actionsTakenThisRound;
    }

    public void setActionsTakenThisRound(int actionsTakenThisRound) {
        this.actionsTakenThisRound = actionsTakenThisRound;
    }

    public boolean isHandOver() {
        return isHandOver;
    }

    public void setHandOver(boolean handOver) {
        this.isHandOver = handOver;
    }

    public int getConstMinRaise() {
        return constMinRaise;
    }

    public void setMinRaise(int minRaise) {
        this.minRaise = minRaise;
    }

    public int getMinRaise() {
        return minRaise;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public List<Pot> getPots() {
        return pots;
    }

    public Street getStreet() {
        return street;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getDealerIndex() {
        return dealerIndex;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setPots(List<Pot> pots) {
        this.pots = pots;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }


}
