package com.model;

public enum Element {

    FIRE("火"),
    GRASS("草"),
    WATER("水");

    private final String label;

    Element(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public double multiplierAgainst(Element target) {
        return multiplierFor(this, target);
    }

    public static final double ADVANTAGE = 1.5;
    public static final double DISADVANTAGE = 0.75;

    public static double multiplierFor(Element attacker, Element defender) {
        if (attacker == FIRE && defender == GRASS) {
            return ADVANTAGE;
        }
        if (attacker == GRASS && defender == WATER) {
            return ADVANTAGE;
        }
        if (attacker == WATER && defender == FIRE) {
            return ADVANTAGE;
        }
        if (attacker == GRASS && defender == FIRE) {
            return DISADVANTAGE;
        }
        if (attacker == WATER && defender == GRASS) {
            return DISADVANTAGE;
        }
        if (attacker == FIRE && defender == WATER) {
            return DISADVANTAGE;
        }
        return 1.0;
    }

    public static Element random(java.util.Random random) {
        Element[] values = values();
        return values[random.nextInt(values.length)];
    }
}