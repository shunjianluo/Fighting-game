package com.model;

public class SkillResult {

    private final int damageDealt;
    private final int selfHpChange;
    private final boolean selfDamaged;
    private final String message;

    public SkillResult(int damageDealt, int selfHpChange, boolean selfDamaged, String message) {
        this.damageDealt = damageDealt;
        this.selfHpChange = selfHpChange;
        this.selfDamaged = selfDamaged;
        this.message = message;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getSelfHpChange() {
        return selfHpChange;
    }

    public boolean isSelfDamaged() {
        return selfDamaged;
    }

    public String getMessage() {
        return message;
    }
}