package com.model;

import java.util.Random;

public class EnemyFactory {

    private static final String[] PREFIXES = {"野生", "森林", "荒野", "岩窟", "幽谷", "熔岩", "深潭"};
    private static final String[] TITLES = {"精灵", "守卫", "掠食者", "首领", "霸主"};

    private EnemyFactory() {
    }

    public static Enemy createForStage(int stage, Random random) {
        return createForStage(stage, random, null);
    }

    public static Enemy createForStage(int stage, Random random, Element playerElement) {
        Element element = chooseElement(stage, random, playerElement);
        double growth = Math.pow(stage, 0.92);
        int hp = (int) Math.floor(70 + 22 * growth);
        int attack = (int) Math.floor(6 + 2.2 * Math.pow(stage, 0.85));
        int defense = stage >= 6 ? Math.min(10, (stage - 5) * 2) : 0;
        if (stage <= 2) {
            hp = Math.max(60, (int) Math.floor(hp * 0.85));
            attack = Math.max(5, (int) Math.floor(attack * 0.85));
        }

        String prefix = PREFIXES[Math.min(PREFIXES.length - 1, (stage - 1) / 2)];
        String title = TITLES[Math.min(TITLES.length - 1, (stage - 1) / 2)];
        String name = prefix + element.getLabel() + "属性" + title;

        return new Enemy(name, element, hp, attack, defense,
                EnemySkill.buildLoadout(element, stage, random));
    }

    private static Element chooseElement(int stage, Random random, Element playerElement) {
        Element candidate = Element.random(random);
        if (playerElement == null || stage > 2) {
            return candidate;
        }
        if (candidate.multiplierAgainst(playerElement) > 1.0) {
            candidate = counteredBy(playerElement);
        }
        return candidate;
    }

    private static Element counteredBy(Element element) {
        for (Element candidate : Element.values()) {
            if (candidate.multiplierAgainst(element) < 1.0) {
                return candidate;
            }
        }
        return element;
    }

    public static String describeSkills(Enemy enemy) {
        StringBuilder builder = new StringBuilder();
        for (EnemySkill skill : enemy.getSkills()) {
            builder.append("- ").append(skill.getName()).append("：")
                    .append(skill.getDescription()).append("\n");
        }
        return builder.toString();
    }
}