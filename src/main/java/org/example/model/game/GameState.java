package org.example.model.game;

import org.example.model.table.Board;
import org.example.model.player.Player;
import org.example.model.table.Pot;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<Player> players;
    private final Board board;
    private List<Pot> pots;
    private Street street;
    private int currentPlayerIndex;
    private int dealerIndex;
    private int currentBet;


    public GameState() {
        this.players = new ArrayList<>();
        this.board = new Board();
    }

    public GameState(List<Player> players, Board board, List<Pot> pots,
                     Street street, int currentPlayerIndex, int dealerIndex,
                     int currentBet, long seed) {
        this.players = players;
        this.board = board;
        this.pots = pots;
        this.street = street;
        this.currentPlayerIndex = currentPlayerIndex;
        this.dealerIndex = dealerIndex;
        this.currentBet = currentBet;
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
