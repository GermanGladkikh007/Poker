package org.example.model.table;

import org.example.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Банк
 */
public class Pot {
    private int amount; // количество фишек
    private Set<Player> eligiblePlayers; // список игроков, имеющих право на выйгрыш

    public Pot(int amount, Set<Player> eligiblePlayers) {
        this.amount = amount;
        this.eligiblePlayers = eligiblePlayers;
    }

    /**
     * Добавление фишек в банк
     * @param chips
     */
    public void add(int chips){
        if(chips < 0) throw new IllegalArgumentException("Нельзя добавить отрицательное количество");
        amount += chips;
    }

    public int getAmount() {return amount;}
    public Set<Player> getEligiblePlayers() {return eligiblePlayers;}

    public void reset(){
        amount = 0;
        eligiblePlayers.clear();
    }

    /**
     * Деление банка между победителей с одинаковой рукой
     * @param winners
     * @return
     */
    public Map<Player, Integer> splitPot(List<Player> winners) {
        if(winners.isEmpty()) throw new IllegalArgumentException("Нет победителей");

        int share = amount / winners.size();
        int remainder = amount % winners.size();

        Map<Player, Integer> result = new HashMap<Player, Integer>();
        for(int i = 0; i < winners.size(); i++){
            int amountForPlayer = share + (i < remainder ? 1 : 0);
            result.put(winners.get(i), amountForPlayer);
        }
        return result;
    }

}
