package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStats {

    public static final int TOTAL_POINTS = 20;
    public static final int BASE_HP = 100;
    public static final int HP_PER_POINT = 10;
    public static final int BASE_ATTACK = 10;
    public static final int ATTACK_PER_POINT = 2;
    public static final int BASE_DEFENSE = 0;
    public static final int DEFENSE_PER_POINT = 1;
    public static final int DEFENSE_REDUCTION_PERCENT = 5;

    private final String username;
    private Element element;
    private int hpPoints;
    private int attackPoints;
    private int defensePoints;
    private int bonusPoints;
    private int currentHp;
    private int attackBoost;
    private int shieldPercent;
    private final List<Skill> skills = new ArrayList<>();
    private final Map<Skill, Integer> cooldowns = new HashMap<>();

    public PlayerStats(String username) {
        this(username, Element.FIRE);
    }

    public PlayerStats(String username, Element element) {
        this.username = username;
        this.element = element == null ? Element.FIRE : element;
        this.skills.addAll(Skill.starterSet(this.element));
        this.currentHp = getMaxHp();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        Element newElement = element == null ? Element.FIRE : element;
        if (newElement == this.element) {
            return;
        }
        Skill oldElementSkill = Skill.elementSkillFor(this.element);
        Skill newElementSkill = Skill.elementSkillFor(newElement);
        int index = skills.indexOf(oldElementSkill);
        if (index >= 0) {
            skills.set(index, newElementSkill);
            cooldowns.remove(oldElementSkill);
        }
        this.element = newElement;
    }

    public String getUsername() {
        return username;
    }

    public int getRemainingPoints() {
        return TOTAL_POINTS + bonusPoints - hpPoints - attackPoints - defensePoints;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void addBonusPoints(int amount) {
        if (amount > 0) {
            bonusPoints += amount;
        }
    }

    public void allocate(Attribute attribute, int delta) {
        if (delta > 0 && getRemainingPoints() < delta) {
            return;
        }
        if (attribute == Attribute.HP && hpPoints + delta < 0) {
            return;
        }
        if (attribute == Attribute.ATTACK && attackPoints + delta < 0) {
            return;
        }
        if (attribute == Attribute.DEFENSE && defensePoints + delta < 0) {
            return;
        }
        int previousMaxHp = getMaxHp();
        switch (attribute) {
            case HP -> hpPoints += delta;
            case ATTACK -> attackPoints += delta;
            case DEFENSE -> defensePoints += delta;
        }
        int newMaxHp = getMaxHp();
        currentHp = Math.min(newMaxHp, currentHp + (newMaxHp - previousMaxHp));
    }

    public int getMaxHp() {
        return BASE_HP + hpPoints * HP_PER_POINT;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, getMaxHp()));
    }

    public void damage(int amount) {
        setCurrentHp(currentHp - Math.max(0, amount));
    }

    public int takeHit(int incoming) {
        int actual = reduceByDefense(incoming);
        if (shieldPercent > 0) {
            actual = Math.max(1, actual - (int) Math.floor(actual * shieldPercent / 100.0));
        }
        setCurrentHp(currentHp - actual);
        return actual;
    }

    public int reduceByDefense(int incoming) {
        int actual = incoming - (int) Math.floor(incoming * getDefensePercent() / 100.0);
        return Math.max(1, actual);
    }

    public int getDefensePercent() {
        return getDefense() * DEFENSE_REDUCTION_PERCENT;
    }

    public void healPercentOfMax(int percent) {
        heal((int) Math.floor(getMaxHp() * percent / 100.0));
    }

    public void restoreFull() {
        currentHp = getMaxHp();
    }

    public void heal(int amount) {
        setCurrentHp(currentHp + Math.max(0, amount));
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public int getAttack() {
        return BASE_ATTACK + attackPoints * ATTACK_PER_POINT + attackBoost;
    }

    public void raiseAttack(int amount) {
        attackBoost += Math.max(0, amount);
    }

    public int getAttackBoost() {
        return attackBoost;
    }

    public void setShieldPercent(int percent) {
        shieldPercent = Math.max(0, Math.min(100, percent));
    }

    public int getShieldPercent() {
        return shieldPercent;
    }

    public void clearShield() {
        shieldPercent = 0;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public boolean canUse(Skill skill) {
        return cooldowns.getOrDefault(skill, 0) <= 0;
    }

    public int getRemainingCooldown(Skill skill) {
        return Math.max(0, cooldowns.getOrDefault(skill, 0));
    }

    public void startCooldown(Skill skill, int turns) {
        if (turns > 0) {
            cooldowns.put(skill, turns);
        }
    }

    public void tickCooldowns() {
        for (Skill skill : skills) {
            int remaining = cooldowns.getOrDefault(skill, 0);
            if (remaining > 0) {
                cooldowns.put(skill, remaining - 1);
            }
        }
    }

    public void clearCooldowns() {
        cooldowns.clear();
    }

    public void learnSkill(Skill skill) {
        if (skills.contains(skill)) {
            return;
        }
        if (skills.size() >= Skill.MAX_SKILL_SLOTS) {
            return;
        }
        skills.add(skill);
    }

    public boolean forgetSkill(Skill skill) {
        if (skill == Skill.NORMAL_ATTACK || !skills.contains(skill)) {
            return false;
        }
        cooldowns.remove(skill);
        return skills.remove(skill);
    }

    public boolean hasFreeSlot() {
        return skills.size() < Skill.MAX_SKILL_SLOTS;
    }

    public int getDefense() {
        return BASE_DEFENSE + defensePoints * DEFENSE_PER_POINT;
    }

    public int getHpPoints() {
        return hpPoints;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }

    public enum Attribute {
        HP, ATTACK, DEFENSE
    }
}