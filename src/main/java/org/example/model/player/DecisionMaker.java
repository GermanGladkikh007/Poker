package org.example.model.player;

import org.example.model.action.Action;
import org.example.model.game.GameState;

import java.util.List;

/**
 * Интерфейс, позволяющий отделить действия игрока от действий ботов
 */
public interface DecisionMaker {
    /**
     * Принять решение, сделать ход
     * @param state
     * @param legalActions
     * @return
     */
    public Action decide(GameState state, List<Action> legalActions);
}

