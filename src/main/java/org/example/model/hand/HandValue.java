package org.example.model.hand;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс силы руки при вскрытии(комбинация, кикеры)
 */
public class HandValue implements Comparable<HandValue> {

    private HandRank handRank;
    private List<Integer> kickers;

    public HandValue() {
        this.handRank = null;
        this.kickers = new ArrayList<Integer>();
    }

    public HandValue(HandRank handRank, List<Integer> kickers) {
        this.handRank = handRank;
        this.kickers = kickers;
    }

    public HandRank getHandRank() { return handRank; }
    public List<Integer> getKickers() { return kickers; }

    public void setHandRank(HandRank handRank) {
        this.handRank = handRank;
    }

    public void setKickers(List<Integer> kickers) {
        this.kickers = kickers;
    }

    @Override
    public int compareTo(HandValue o) {
        int cmp = Integer.compare(this.handRank.getStrength(), o.getHandRank().getStrength());
        if(cmp != 0) return cmp;

        // если есть кикеры, сравниваем их по одному
        for(int i = 0; i < Math.min(this.kickers.size(), o.getKickers().size()); i++) {
            cmp = Integer.compare(this.kickers.get(i), o.getKickers().get(i));
            if(cmp != 0) return cmp;
        }

        return 0;
    }
}
