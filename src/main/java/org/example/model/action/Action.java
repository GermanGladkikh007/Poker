package org.example.model.action;


/**
 * Характеристика действия
 * @param actionType
 * @param amount - на сколько изменить, используется только для Raise
 */
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

    public Action(ActionType actionType){
        this(actionType, 0);
    }

    @Override
    public String toString() {
        return switch (actionType) {
            case FOLD   -> "сбрасывает карты";
            case CHECK  -> "чек";
            case CALL   -> "уравнивает";
            case RAISE  -> "повышает до " + amount;
            case ALL_IN -> "идёт ва-банк";
        };
    }
}


