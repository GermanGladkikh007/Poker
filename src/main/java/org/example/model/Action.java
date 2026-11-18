package org.example.model;

import org.example.model.enums.ActionType;

public record Action(ActionType actionType, int amount) {

    public Action {
        if (actionType == null) {
            throw new IllegalArgumentException("Тип действия не может быть null");
        }
        if (actionType == ActionType.RAISE && amount <= 0) {
            throw new IllegalArgumentException("RAISE требует amount > 0");
        }
        if (actionType != ActionType.RAISE && amount != 0) {
            throw new IllegalArgumentException("amount может быть только для RAISE");
        }
    }

}


