package org.example.model.player;

import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.GameState;
import org.example.model.hand.HandEvaluator;
import org.example.model.hand.HandValue;
import org.example.model.table.Card;

import java.util.List;
import java.util.Random;

public class BotDecisionMaker implements DecisionMaker {
    private final double threshold; // Порог входа (0.0 - 1.0)

    public BotDecisionMaker(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Action decide(GameState state, List<ActionType> legalActions) {
        // 1. Оцениваем силу руки (от 0.0 до 1.0)
        double handStrength = evaluateHandStrength(state);

        // 2. Если рука слабее порога — фолдим (если можем)
        if (handStrength < threshold) {
            if (legalActions.contains(ActionType.FOLD)) {
                return new Action(ActionType.FOLD);
            }
            // Если фолд невозможен (например, мы уже в олл-ине) — делаем чек/колл
            if (legalActions.contains(ActionType.CHECK)) return new Action(ActionType.CHECK);
            return new Action(ActionType.CALL);
        }

        // 3. Если рука сильная — играем агрессивно
        if (legalActions.contains(ActionType.RAISE)) {
            // Делаем минимальный рейз
            int minRaiseAmount = state.getCurrentBet() + state.getMinRaise();

            // Если minRaise почему-то 0 (на флопе), ставим хотя бы bigBlind
            if (minRaiseAmount == 0) {
                minRaiseAmount = 100; // bigBlind
            }

            // Проверяем, хватает ли фишек
            Player me = state.getPlayers().get(state.getCurrentPlayerIndex());
            if (me.getChips() >= minRaiseAmount - me.getCurrentBet()) {
                return new Action(ActionType.RAISE,minRaiseAmount);
            } else {
                // Если не хватает — идем в олл-ин
                return new Action(ActionType.ALL_IN);
            }
        } else if (legalActions.contains(ActionType.CALL)) {
            return new Action(ActionType.CALL);
        } else if (legalActions.contains(ActionType.CHECK)) {
            return new Action(ActionType.CHECK);
        }

        // Запасной вариант
        return new Action(ActionType.FOLD);
    }

    /**
     * Простая эвристика оценки руки.
     * Возвращает от 0.0 (мусор) до 1.0 (монстр).
     */
    private double evaluateHandStrength(GameState state) {
        Player me = state.getPlayers().get(state.getCurrentPlayerIndex());
        List<Card> holeCards = me.getHoleCards();

        // Если на столе есть карты (Постфлоп)
        if (!state.getBoard().getCards().isEmpty()) {
            HandEvaluator evaluator = new HandEvaluator(state.getBoard());
            HandValue value = evaluator.evaluate(me);

            // Берем силу комбинации (1..10) и делим на 10
            double rankScore = value.getHandRank().getStrength() / 10.0;

            // Добавляем бонус за старшую карту (Туз = 1.0, Король = 0.9 и т.д.)
            double highCardBonus = value.getKickers().isEmpty() ? 0 : value.getKickers().get(0) / 14.0;

            // Итоговая оценка (например, 0.3 за пару + 0.1 за кикера = 0.4)
            return rankScore + (highCardBonus * 0.1);
        }

        // Префлоп (эвристика по 2 картам)
        Card c1 = holeCards.get(0);
        Card c2 = holeCards.get(1);

        // Пара
        if (c1.rank() == c2.rank()) {
            return c1.rank().getValue() / 14.0; // AA = 1.0, 22 = 0.14
        }

        // Высокие карты (A, K, Q)
        int highCard1 = Math.max(c1.rank().getValue(), c2.rank().getValue());
        int highCard2 = Math.min(c1.rank().getValue(), c2.rank().getValue());

        double score = (highCard1 + highCard2) / 28.0; // Максимум 28 (AA)

        // Бонус за одномастность (suited)
        if (c1.suit() == c2.suit()) {
            score += 0.1;
        }

        return Math.min(1.0, score);
    }
}
