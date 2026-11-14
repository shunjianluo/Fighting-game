package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum EnemySkill {

    SCRATCH("撕咬", "一般系，造成 100% 攻击力伤害", 100, 0, 0, 5) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            return strike(self, player, 100, "撕咬");
        }
    },
    FLAMETHROWER("喷射火焰", "火系，造成 110% 攻击力伤害", 110, 0, 0, 4) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            return strike(self, player, 110, "喷射火焰");
        }
    },
    WATER_GUN("水枪", "水系，造成 110% 攻击力伤害", 110, 0, 0, 4) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            return strike(self, player, 110, "水枪");
        }
    },
    RAZOR_LEAF("飞叶快刀", "草系，造成 110% 攻击力伤害", 110, 0, 0, 4) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            return strike(self, player, 110, "飞叶快刀");
        }
    },
    QUICK_JAB("电光一闪", "一般系，先制突袭，造成 70% 攻击力伤害", 70, 0, 0, 3) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            return strike(self, player, 70, "电光一闪");
        }
    },
    HEAVY_SLAM("重击", "一般系，造成 150% 攻击力伤害，命中率较低", 150, 0, 0, 2) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            if (random.nextInt(100) < 25) {
                return new SkillResult(0, 0, false, "「重击」落空了！");
            }
            return strike(self, player, 150, "重击");
        }
    },
    DRAIN_KISS("吸取之吻", "造成 60% 攻击力伤害，并回复等量生命", 60, 0, 0, 3) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            int dealt = damageTo(self, player, 60);
            int healed = Math.min(dealt, self.getMaxHp() - self.getCurrentHp());
            self.heal(dealt);
            return new SkillResult(dealt, healed, false,
                    "「吸取之吻」造成 " + dealt + " 点伤害，回复 " + healed + " 点生命");
        }
    },
    TAUNT("挑衅", "提升自身 20% 攻击力", 0, 0, 0, 2) {
        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            int gain = Math.max(1, (int) Math.floor(self.getBaseAttack() * 0.2));
            self.raiseAttack(gain);
            return new SkillResult(0, 0, false, "「挑衅」提升了攻击力 " + gain + " 点");
        }
    },
    REST("休养生息", "回复 25% 最大生命，仅在生命低于一半时使用", 0, 0, 0, 4) {
        @Override
        boolean isUsable(Enemy self) {
            return self.isWounded();
        }

        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            int healed = (int) Math.floor(self.getMaxHp() * 0.25);
            self.heal(healed);
            return new SkillResult(0, healed, false, "「休养生息」回复了 " + healed + " 点生命");
        }
    },
    FRENZY("背水一战", "生命低于 30% 时使用，造成 180% 攻击力伤害并自损 8% 最大生命", 180, 8, 0, 3) {
        @Override
        boolean isUsable(Enemy self) {
            return self.getCurrentHp() < self.getMaxHp() * 0.3;
        }

        @Override
        public SkillResult use(Enemy self, PlayerStats player, Random random) {
            int cost = (int) Math.floor(self.getMaxHp() * 0.08);
            int dealt = damageTo(self, player, 180);
            self.damage(cost);
            return new SkillResult(dealt, -cost, true,
                    "「背水一战」造成 " + dealt + " 点伤害，自身损失 " + cost + " 点生命");
        }
    };

    private final String name;
    private final String description;
    private final int weight;

    EnemySkill(String name, String description, int powerPercent, int selfCostPercent,
               int requiredStage, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getWeight() {
        return weight;
    }

    boolean isUsable(Enemy self) {
        return true;
    }

    public abstract SkillResult use(Enemy self, PlayerStats player, Random random);

    static SkillResult strike(Enemy self, PlayerStats player, int percent, String skillName) {
        int dealt = damageTo(self, player, percent);
        return new SkillResult(dealt, 0, false, "「" + skillName + "」造成 " + dealt + " 点伤害" + effectText(self, player));
    }

    static int damageTo(Enemy self, PlayerStats player, int percent) {
        int raw = Math.max(1, self.getAttack() * percent / 100);
        double multiplier = Element.multiplierFor(self.getElement(), player.getElement());
        int scaled = Math.max(1, (int) Math.floor(raw * multiplier));
        return player.takeHit(scaled);
    }

    static String effectText(Enemy self, PlayerStats player) {
        double multiplier = Element.multiplierFor(self.getElement(), player.getElement());
        if (multiplier > 1.0) {
            return "（效果绝佳！）";
        }
        if (multiplier < 1.0) {
            return "（收效甚微……）";
        }
        return "";
    }

    public static List<EnemySkill> poolFor(Element element, int stage) {
        List<EnemySkill> pool = new ArrayList<>();
        pool.add(SCRATCH);
        if (stage >= 2) {
            pool.add(QUICK_JAB);
        }
        if (stage >= 3) {
            pool.add(TAUNT);
        }
        if (stage >= 4) {
            pool.add(HEAVY_SLAM);
        }
        if (stage >= 5) {
            pool.add(REST);
            pool.add(DRAIN_KISS);
        }
        if (stage >= 6) {
            pool.add(FRENZY);
        }
        EnemySkill elementSkill = elementSkillOf(element);
        if (elementSkill != null && stage >= 3) {
            pool.add(elementSkill);
        }
        return pool;
    }

    public static EnemySkill elementSkillOf(Element element) {
        if (element == null) {
            return null;
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
                return null;
            }
        }
    }

    public static List<EnemySkill> basicSet(Element element) {
        return List.of(SCRATCH);
    }

    public static List<EnemySkill> buildLoadout(Element element, int stage, Random random) {
        List<EnemySkill> pool = poolFor(element, stage);
        List<EnemySkill> result = new ArrayList<>();
        result.add(SCRATCH);
        EnemySkill elementSkill = elementSkillOf(element);
        if (elementSkill != null && stage >= 3) {
            result.add(elementSkill);
        }
        int extraCount = Math.min(2, Math.max(0, stage / 3));
        List<EnemySkill> candidates = new ArrayList<>(pool);
        candidates.remove(SCRATCH);
        candidates.remove(elementSkill);
        for (int i = 0; i < extraCount && !candidates.isEmpty(); i++) {
            result.add(candidates.remove(random.nextInt(candidates.size())));
        }
        return result;
    }
}