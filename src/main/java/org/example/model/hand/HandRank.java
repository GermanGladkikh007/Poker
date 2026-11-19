package org.example.model.hand;

public enum HandRank {
    ROYAL_FLUSH("Роял-флеш", 10),
    STRAIGHT_FLUSH("Стрит-флеш", 9),
    FOUR_OF_A_KIND("Каре", 8),
    FULL_HOUSE("Фулл-хаус", 7),
    FLUSH("Флеш", 6),
    STRAIGHT("Стрит", 5),
    THREE_OF_A_KIND("Сет (тройка)", 4),
    TWO_PAIR("Две пары", 3),
    ONE_PAIR("Пара", 2),
    HIGH_CARD("Старшая карта", 1);

    private final String russianName;
    private final int strength;

    HandRank(String russianName, int strength) {
        this.russianName = russianName;
        this.strength = strength;
    }

    public int getStrength() { return strength; }

    @Override
    public String toString() { return russianName; }
}
