package com.ui;

import com.model.Element;
import com.model.PlayerStats;
import com.model.PlayerStats.Attribute;
import com.model.Skill;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

public class CharacterCreate extends JFrame {

    private final PlayerStats stats;
    private final Runnable onConfirmed;
    private final boolean freshCreate;
    private final JLabel pointsLabel = new JLabel();
    private final JLabel hpLabel = new JLabel();
    private final JLabel attackLabel = new JLabel();
    private final JLabel defenseLabel = new JLabel();
    private final JLabel defenseEffectLabel = new JLabel();
    private final JComboBox<Element> elementBox = new JComboBox<>(Element.values());
    private final JLabel elementHintLabel = new JLabel();

    public CharacterCreate(String username) {
        this(new PlayerStats(username), null, true);
    }

    public CharacterCreate(PlayerStats stats, Runnable onConfirmed, boolean freshCreate) {
        this.stats = stats;
        this.onConfirmed = onConfirmed;
        this.freshCreate = freshCreate;

        setTitle(freshCreate ? "属性分配" : "属性分配（闯关奖励）");
        setSize(560, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel content = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        JLabel title = new JLabel(freshCreate ? "角色属性分配" : "闯关奖励属性分配", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 26));
        title.setForeground(new Color(46, 26, 58));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 8, 0));
        content.add(title, BorderLayout.NORTH);

        pointsLabel.setHorizontalAlignment(JLabel.CENTER);
        pointsLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(true);
        form.setBackground(new Color(255, 255, 255, 210));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 235), 2),
                BorderFactory.createEmptyBorder(18, 30, 18, 30)));
        form.add(pointsLabel);
        form.add(Box.createVerticalStrut(16));
        form.add(createElementRow());
        form.add(Box.createVerticalStrut(6));
        form.add(createElementHintRow());
        form.add(Box.createVerticalStrut(14));
        form.add(createAttributeRow("生命值", PlayerStats.BASE_HP, hpLabel, Attribute.HP));
        form.add(Box.createVerticalStrut(14));
        form.add(createAttributeRow("攻击力", PlayerStats.BASE_ATTACK, attackLabel, Attribute.ATTACK));
        form.add(Box.createVerticalStrut(14));
        form.add(createAttributeRow("防御值", PlayerStats.BASE_DEFENSE, defenseLabel, Attribute.DEFENSE));
        form.add(Box.createVerticalStrut(6));
        form.add(createDefenseEffectRow());

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);
        content.add(center, BorderLayout.CENTER);

        JButton confirmButton = new JButton("确认");
        JButton backButton = new JButton("返回");
        confirmButton.addActionListener(e -> doConfirm());
        backButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);
        buttons.add(confirmButton);
        buttons.add(backButton);
        content.add(buttons, BorderLayout.SOUTH);

        refresh();
        refreshElementHint();
    }

    private JPanel createElementRow() {
        JLabel nameLabel = new JLabel("属性");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        nameLabel.setPreferredSize(new Dimension(70, 30));

        elementBox.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        elementBox.setPreferredSize(new Dimension(150, 30));
        elementBox.setSelectedItem(stats.getElement());
        elementBox.setEnabled(freshCreate);
        elementBox.addActionListener(e -> refreshElementHint());

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(nameLabel);
        row.add(elementBox);
        row.add(Box.createHorizontalGlue());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return row;
    }

    private JPanel createElementHintRow() {
        elementHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        elementHintLabel.setForeground(new Color(88, 76, 96));

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 0));
        row.add(elementHintLabel);
        row.add(Box.createHorizontalGlue());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return row;
    }

    private JPanel createDefenseEffectRow() {
        defenseEffectLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        defenseEffectLabel.setForeground(new Color(88, 76, 96));

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 0));
        row.add(defenseEffectLabel);
        row.add(Box.createHorizontalGlue());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return row;
    }

    private void refreshElementHint() {
        Element selected = (Element) elementBox.getSelectedItem();
        elementHintLabel.setText(describeElement(selected));
    }

    private String describeElement(Element element) {
        if (element == null) {
            return "";
        }
        return "克制" + counterTarget(element) + "属性，被" + reverseSource(element) + "属性克制";
    }

    private String counterTarget(Element element) {
        for (Element candidate : Element.values()) {
            if (element.multiplierAgainst(candidate) > 1.0) {
                return candidate.getLabel();
            }
        }
        return "";
    }

    private String reverseSource(Element element) {
        for (Element candidate : Element.values()) {
            if (candidate.multiplierAgainst(element) > 1.0) {
                return candidate.getLabel();
            }
        }
        return "";
    }

    private JPanel createAttributeRow(String name, int base, JLabel valueLabel, Attribute attribute) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        nameLabel.setPreferredSize(new Dimension(70, 30));

        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        valueLabel.setPreferredSize(new Dimension(150, 30));

        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        minus.setPreferredSize(new Dimension(46, 30));
        plus.setPreferredSize(new Dimension(46, 30));
        minus.addActionListener(e -> {
            stats.allocate(attribute, -1);
            refresh();
        });
        plus.addActionListener(e -> {
            stats.allocate(attribute, 1);
            refresh();
        });

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(nameLabel);
        row.add(valueLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(minus);
        row.add(Box.createHorizontalStrut(6));
        row.add(plus);
        row.add(Box.createHorizontalGlue());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return row;
    }

    private void refresh() {
        String bonusHint = stats.getBonusPoints() > 0 ? "（含奖励点数 " + stats.getBonusPoints() + "）" : "";
        pointsLabel.setText("剩余可分配点数：" + stats.getRemainingPoints() + bonusHint);
        hpLabel.setText(stats.getMaxHp() + "  （+" + stats.getHpPoints() * PlayerStats.HP_PER_POINT + "）");
        attackLabel.setText(stats.getAttack() + "  （+" + stats.getAttackPoints() * PlayerStats.ATTACK_PER_POINT + "）");
        defenseLabel.setText(stats.getDefense() + "  （+" + stats.getDefensePoints() * PlayerStats.DEFENSE_PER_POINT + "）");
        defenseEffectLabel.setText("当前减伤：" + stats.getDefensePercent() + "%（每点防御减少 5% 受到的伤害）");
    }

    private void doConfirm() {
        stats.setElement((Element) elementBox.getSelectedItem());
        if (stats.getRemainingPoints() > 0) {
            int result = JOptionPane.showConfirmDialog(this,
                    "还有 " + stats.getRemainingPoints() + " 点未分配，确定要开始冒险吗？",
                    "提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
        }
        StringBuilder message = new StringBuilder();
        message.append(freshCreate ? "角色创建完成！\n" : "属性分配完成！\n")
                .append("属性：").append(stats.getElement().getLabel()).append("\n")
                .append("生命值：").append(stats.getMaxHp()).append("（当前 ")
                .append(stats.getCurrentHp()).append("）\n")
                .append("攻击力：").append(stats.getAttack()).append("\n")
                .append("防御值：").append(stats.getDefense())
                .append("（减伤 ").append(stats.getDefensePercent()).append("%）\n\n")
                .append("技能（").append(stats.getSkills().size()).append(" / ")
                .append(Skill.MAX_SKILL_SLOTS).append("）：\n");
        for (Skill skill : stats.getSkills()) {
            message.append("- ").append(skill.getName()).append("：")
                    .append(skill.getDescription()).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "确认", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        if (onConfirmed != null) {
            onConfirmed.run();
        } else {
            new BattlePage(stats).start();
        }
    }

    public void start() {
        setVisible(true);
        System.out.println("属性分配界面打开了");
    }
}