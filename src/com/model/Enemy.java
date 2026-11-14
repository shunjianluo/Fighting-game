package com.model;

import java.util.List;

public class Enemy {

    private final String name;
    private final Element element;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final List<EnemySkill> skills;
    private int currentHp;
    private int attackBoost;

    public Enemy(String name, int maxHp, int attack, int defense) {
        this(name, Element.GRASS, maxHp, attack, defense, EnemySkill.basicSet(Element.GRASS));
    }

    public Enemy(String name, Element element, int maxHp, int attack, int defense) {
        this(name, element, maxHp, attack, defense, EnemySkill.basicSet(element));
    }

    public Enemy(String name, Element element, int maxHp, int attack, int defense, List<EnemySkill> skills) {
        this.name = name;
        this.element = element == null ? Element.GRASS : element;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.skills = List.copyOf(skills);
        this.currentHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp));
    }

    public void damage(int amount) {
        setCurrentHp(currentHp - Math.max(0, amount));
    }

    public void heal(int amount) {
        setCurrentHp(currentHp + Math.max(0, amount));
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public int getAttack() {
        return attack + attackBoost;
    }

    public int getBaseAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getDefensePercent() {
        return defense * PlayerStats.DEFENSE_REDUCTION_PERCENT;
    }

    public int reduceByDefense(int incoming) {
        int actual = incoming - (int) Math.floor(incoming * getDefensePercent() / 100.0);
        return Math.max(1, actual);
    }

    public void raiseAttack(int amount) {
        attackBoost += Math.max(0, amount);
    }

    public boolean isWounded() {
        return currentHp < maxHp / 2;
    }

    public List<EnemySkill> getSkills() {
        return skills;
    }

    public EnemySkill chooseSkill(java.util.Random random) {
        List<EnemySkill> usable = skills.stream().filter(skill -> skill.isUsable(this)).toList();
        if (usable.isEmpty()) {
            return EnemySkill.SCRATCH;
        }
        int totalWeight = usable.stream().mapToInt(EnemySkill::getWeight).sum();
        int roll = random.nextInt(totalWeight);
        for (EnemySkill skill : usable) {
            roll -= skill.getWeight();
            if (roll < 0) {
                return skill;
            }
        }
        return usable.get(usable.size() - 1);
    }
}