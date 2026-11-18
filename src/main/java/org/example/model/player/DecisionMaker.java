package org.example.model.player;

import org.example.model.Action;
import org.example.model.game.GameState;

import java.util.List;

public interface DecisionMaker {
    public Action decide(GameState state, List<Action> legalActions);
}

