package org.example.model.player;

import org.example.model.action.Action;
import org.example.model.table.Card;
import org.example.model.game.GameState;

import java.util.List;
import java.util.Objects;

public class Player {
    private final String name;
    private final int id;

    private int chips; // Текущий стек
    private List<Card> holeCards; // Две личные карты

    private int currentBet; // Ставка в текущем круге
    private int totalContribution; // Всего вложено в текущую раздачу

    private PlayerStatus status;
    private final DecisionMaker decisionMaker;

    public Player(int id, String name, DecisionMaker decisionMaker) {
        this.id = id;
        this.name = name;
        this.decisionMaker = decisionMaker;
    }

    public Action decide(GameState state, List<Action> legalActions) {
        return decisionMaker.decide(state, legalActions);
    }

    public String getName() {return name;}
    public List<Card> getHoleCards(){return holeCards;}


    public void setStatus(PlayerStatus status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) {return true;}
        if(!(obj instanceof Player)) {return false;}
        return id == ((Player) obj).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

