package org.example.model.player;

import org.example.model.Action;
import org.example.model.table.Card;
import org.example.model.enums.PlayerStatus;
import org.example.model.game.GameState;

import java.util.List;

public class Player {
    private final String name;

    private int chips; // Текущий стек
    private List<Card> holeCards; // Две личные карты

    private int currentBet; // Ставка в текущем круге
    private int totalContribution; // Всего вложено в текущую раздачу

    private PlayerStatus status;
    private final DecisionMaker decisionMaker;

    public Player(String name, DecisionMaker decisionMaker) {
        this.name = name;
        this.decisionMaker = decisionMaker;
    }

    public Action decide(GameState state, List<Action> legalActions) {
        return decisionMaker.decide(state, legalActions);
    }

    public String getName() {return name;}
}

