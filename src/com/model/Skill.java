package com.model;

import java.util.ArrayList;
import java.util.List;

public class Skill {

    public static final int MAX_SKILL_SLOTS = 4;

    public static final Skill NORMAL_ATTACK = new Skill("普通攻击", "造成攻击力 100% 的伤害，无冷却",
            ElementType.NORMAL, 100, 0, 0, 0);
    public static final Skill POWER_STRIKE = new Skill("强力一击", "造成攻击力 150% 的伤害，自损 8% 最大生命，冷却 1 回合",
            ElementType.NORMAL, 150, 0, 8, 1);
    public static final Skill LIFE_DRAIN = new Skill("生命吸取", "造成攻击力 70% 的伤害并回复等量生命，冷却 1 回合",
            ElementType.NORMAL, 70, 50, 0, 1);
    public static final Skill QUICK_ATTACK = new Skill("电光一闪", "造成攻击力 60% 的伤害并回复 5 点生命，无冷却",
            ElementType.NORMAL, 60, 0, 0, 0);
    public static final Skill IRON_DEFENSE = new Skill("铁壁", "本回合获得 30% 额外减伤，冷却 2 回合",
            ElementType.NORMAL, 0, 0, 0, 2);
    public static final Skill HEALING_WISH = new Skill("治愈之愿", "回复最大生命 25%，冷却 3 回合",
            ElementType.NORMAL, 0, 0, 0, 3);
    public static final Skill PIERCING_SHOT = new Skill("破甲一击", "造成攻击力 120% 的伤害，无视敌方半数减伤，冷却 2 回合",
            ElementType.NORMAL, 120, 0, 0, 2);
    public static final Skill BERSERK = new Skill("狂暴", "造成攻击力 200% 的伤害，自损 12% 最大生命，冷却 3 回合",
            ElementType.NORMAL, 200, 0, 12, 3);
    public static final Skill FOCUS_POWER = new Skill("聚气", "永久提升自身 10% 基础攻击力，冷却 3 回合",
            ElementType.NORMAL, 0, 0, 0, 3);

    public static final Skill FLAMETHROWER = new Skill("喷射火焰", "火系技能，造成攻击力 110% 的伤害，冷却 1 回合",
            ElementType.FIRE, 110, 0, 0, 1);
    public static final Skill WATER_GUN = new Skill("水枪", "水系技能，造成攻击力 110% 的伤害，冷却 1 回合",
            ElementType.WATER, 110, 0, 0, 1);
    public static final Skill RAZOR_LEAF = new Skill("飞叶快刀", "草系技能，造成攻击力 110% 的伤害，冷却 1 回合",
            ElementType.GRASS, 110, 0, 0, 1);

    public static final List<Skill> LEARNABLE = List.of(
            POWER_STRIKE, LIFE_DRAIN, QUICK_ATTACK, IRON_DEFENSE,
            HEALING_WISH, PIERCING_SHOT, BERSERK, FOCUS_POWER);

    private final String name;
    private final String description;
    private final ElementType elementType;
    private final int percent;
    private final int lifestealPercent;
    private final int selfCostPercent;
    private final int cooldown;

    private Skill(String name, String description, ElementType elementType,
                  int percent, int lifestealPercent, int selfCostPercent, int cooldown) {
        this.name = name;
        this.description = description;
        this.elementType = elementType;
        this.percent = percent;
        this.lifestealPercent = lifestealPercent;
        this.selfCostPercent = selfCostPercent;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isElementSkill() {
        return elementType != ElementType.NORMAL;
    }

    public static Skill elementSkillFor(Element element) {
        if (element == null) {
            return NORMAL_ATTACK;
        }
        switch (element) {
            case FIRE -> {
                return FLAMETHROWER;
            }
            case WATER -> {
                return WATER_GUN;
            }
            case GRASS -> {
                return RAZOR_LEAF;
            }
            default -> {
                return NORMAL_ATTACK;
            }
        }
    }

    public static List<Skill> starterSet(Element element) {
        return new ArrayList<>(List.of(elementSkillFor(element), NORMAL_ATTACK));
    }

    public static List<Skill> learnableFor(PlayerStats player) {
        List<Skill> result = new ArrayList<>(LEARNABLE);
        result.removeIf(skill -> player.getSkills().contains(skill));
        return result;
    }

    public SkillResult use(PlayerStats self, Enemy target) {
        SkillResult result;
        if (this == LIFE_DRAIN) {
            result = lifeDrain(self, target);
        } else if (this == IRON_DEFENSE) {
            result = ironDefense(self);
        } else if (this == HEALING_WISH) {
            result = healingWish(self);
        } else if (this == PIERCING_SHOT) {
            result = piercingShot(self, target);
        } else if (this == FOCUS_POWER) {
            result = focusPower(self);
        } else if (this == QUICK_ATTACK) {
            result = quickAttack(self, target);
        } else {
            result = attack(self, target);
        }
        if (cooldown > 0) {
            self.startCooldown(this, cooldown);
        }
        return result;
    }

    private int calcSelfCost(PlayerStats self) {
        int cost = (int) Math.floor(self.getMaxHp() * selfCostPercent / 100.0);
        int cap = Math.max(1, (int) Math.floor(self.getCurrentHp() * 0.15));
        cost = Math.min(cost, cap);
        cost = Math.min(cost, Math.max(0, self.getCurrentHp() - 1));
        return Math.max(1, cost);
    }

    private SkillResult attack(PlayerStats self, Enemy target) {
        Damage damage = calcDamage(self.getAttack(), percent, target, elementType.toElement());
        target.damage(damage.amount);
        int selfCost = selfCostPercent > 0 ? calcSelfCost(self) : 0;
        if (selfCost > 0) {
            self.damage(selfCost);
            if (!self.isAlive()) {
                self.heal(1 - self.getCurrentHp());
            }
            return new SkillResult(damage.amount, -selfCost, true,
                    "造成 " + damage.amount + " 点伤害，自身损失 " + selfCost + " 点生命" + damage.effectText());
        }
        return new SkillResult(damage.amount, 0, false,
                "造成 " + damage.amount + " 点伤害" + damage.effectText());
    }

    private SkillResult quickAttack(PlayerStats self, Enemy target) {
        Damage damage = calcDamage(self.getAttack(), percent, target, null);
        target.damage(damage.amount);
        int healed = Math.min(5, self.getMaxHp() - self.getCurrentHp());
        self.heal(healed);
        return new SkillResult(damage.amount, healed, false,
                "造成 " + damage.amount + " 点伤害，回复 " + healed + " 点生命");
    }

    private SkillResult lifeDrain(PlayerStats self, Enemy target) {
        Damage damage = calcDamage(self.getAttack(), percent, target, null);
        target.damage(damage.amount);
        int healed = Math.min((int) Math.floor(damage.amount * lifestealPercent / 100.0),
                self.getMaxHp() - self.getCurrentHp());
        self.heal(healed);
        return new SkillResult(damage.amount, healed, false,
                "造成 " + damage.amount + " 点伤害，回复 " + healed + " 点生命");
    }

    private SkillResult ironDefense(PlayerStats self) {
        self.setShieldPercent(30);
        return new SkillResult(0, 0, false, "架起护盾，本回合受到伤害减少 30%");
    }

    private SkillResult healingWish(PlayerStats self) {
        int healed = (int) Math.floor(self.getMaxHp() * 0.25);
        healed = Math.min(healed, self.getMaxHp() - self.getCurrentHp());
        self.heal(healed);
        return new SkillResult(0, healed, false, "回复了 " + healed + " 点生命");
    }

    private SkillResult piercingShot(PlayerStats self, Enemy target) {
        int raw = Math.max(1, self.getAttack() * percent / 100);
        double multiplier = Element.multiplierFor(self.getElement(), target.getElement());
        int scaled = Math.max(1, (int) Math.floor(raw * multiplier));
        int reducedPercent = target.getDefensePercent() / 2;
        int actual = scaled - (int) Math.floor(scaled * reducedPercent / 100.0);
        actual = Math.max(1, actual);
        target.damage(actual);
        String effect = multiplier > 1.0 ? "（效果绝佳！）" : multiplier < 1.0 ? "（收效甚微……）" : "";
        return new SkillResult(actual, 0, false,
                "无视半数防御，造成 " + actual + " 点伤害" + effect);
    }

    private SkillResult focusPower(PlayerStats self) {
        int gain = Math.max(1, (int) Math.floor(PlayerStats.BASE_ATTACK * 0.1));
        self.raiseAttack(gain);
        return new SkillResult(0, 0, false, "攻击力永久提升 " + gain + " 点");
    }

    private Damage calcDamage(int attack, int percent, Enemy target, Element attacker) {
        int raw = Math.max(1, attack * percent / 100);
        double multiplier = Element.multiplierFor(attacker, target.getElement());
        int scaled = Math.max(1, (int) Math.floor(raw * multiplier));
        int amount = target.reduceByDefense(scaled);
        return new Damage(amount, multiplier);
    }

    @Override
    public String toString() {
        return name;
    }

    public enum ElementType {

        NORMAL("一般") {
            @Override
            Element toElement() {
                return null;
            }
        },
        FIRE("火") {
            @Override
            Element toElement() {
                return Element.FIRE;
            }
        },
        WATER("水") {
            @Override
            Element toElement() {
                return Element.WATER;
            }
        },
        GRASS("草") {
            @Override
            Element toElement() {
                return Element.GRASS;
            }
        };

        private final String label;

        ElementType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        abstract Element toElement();
    }

    private static class Damage {

        private final int amount;
        private final double multiplier;

        private Damage(int amount, double multiplier) {
            this.amount = amount;
            this.multiplier = multiplier;
        }

        private String effectText() {
            if (multiplier > 1.0) {
                return "（效果绝佳！）";
            }
            if (multiplier < 1.0) {
                return "（收效甚微……）";
            }
            return "";
        }
    }
}