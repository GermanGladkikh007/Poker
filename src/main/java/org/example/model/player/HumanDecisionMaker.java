package org.example.model.player;

import org.example.model.action.Action;
import org.example.model.action.ActionType;
import org.example.model.game.GameState;

import java.util.List;
import java.util.Scanner;

public class HumanDecisionMaker implements DecisionMaker {

    private int currentPlayer;
    public HumanDecisionMaker(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    @Override
    public Action decide(GameState state, List<ActionType> legalActions) {
        Player me = state.getPlayers().get(currentPlayer); // (предполагая, что ты - игрок 0)
        System.out.println("Твои карты: " + me.getHoleCards());

        System.out.println("На столе: " + state.getBoard().getCards());
        System.out.println("Текущая ставка: " + state.getCurrentBet());
        System.out.println("Твоя ставка: " + me.getCurrentBet());

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== ТВОЙ ХОД ===");

        for (int i = 0; i < legalActions.size(); i++) {
            System.out.println((i + 1) + ". " + legalActions.get(i));
        }

        while (true) {
            System.out.print("Выбери действие (номер): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= legalActions.size()) {
                    ActionType chosen = legalActions.get(choice - 1);

                    // Если это рейз, спрашиваем сумму
                    if (chosen == ActionType.RAISE) {
                        System.out.print("До скольки повысить? ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        return new Action(ActionType.RAISE,amount); // Возвращаем новый Action с суммой
                    }
                    return new Action(chosen);
                }
                System.out.println("Неверный номер, попробуй снова.");
            } catch (NumberFormatException e) {
                System.out.println("Введи число!");
            }
        }

    }
}
