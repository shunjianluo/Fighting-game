package com.ui;

import com.model.PlayerStats;
import com.model.Skill;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

public class SkillLearnPage extends JFrame {

    private final PlayerStats player;
    private final Runnable onFinished;
    private final JPanel slotPanel = new JPanel();
    private final JPanel learnPanel = new JPanel();
    private JLabel hintLabel = new JLabel();

    public SkillLearnPage(PlayerStats player, Runnable onFinished) {
        this.player = player;
        this.onFinished = onFinished;

        setTitle("技能学习");
        setSize(620, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        BackgroundPanel content = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        JLabel title = new JLabel("技能学习与遗忘", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(46, 26, 58));
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 6, 0));
        content.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(0, 26, 0, 26));

        cards.add(createCard("当前技能（最多 " + Skill.MAX_SKILL_SLOTS + " 个）", slotPanel, 190));
        cards.add(Box.createVerticalStrut(12));
        cards.add(createCard("可学习技能（选择 1 个）", learnPanel, 210));

        JScrollPane scroll = new JScrollPane(cards);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        content.add(scroll, BorderLayout.CENTER);

        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        hintLabel.setHorizontalAlignment(JLabel.CENTER);
        hintLabel.setForeground(new Color(240, 240, 240));

        JButton finishButton = new JButton("完成");
        finishButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
        finishButton.addActionListener(e -> doFinish());

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(hintLabel);
        bottom.add(Box.createVerticalStrut(6));
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setOpaque(false);
        buttonRow.add(finishButton);
        bottom.add(buttonRow);
        bottom.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));
        content.add(bottom, BorderLayout.SOUTH);

        refreshAll();
    }

    private JPanel createCard(String title, JPanel body, int preferredHeight) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setOpaque(true);
        card.setBackground(new Color(255, 255, 255, 205));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 235), 2),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(new Color(46, 26, 58));
        card.add(titleLabel, BorderLayout.NORTH);

        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(0, preferredHeight - 46));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void refreshAll() {
        slotPanel.removeAll();
        for (Skill skill : player.getSkills()) {
            slotPanel.add(createSlotRow(skill));
        }
        learnPanel.removeAll();
        List<Skill> learnable = Skill.learnableFor(player);
        if (learnable.isEmpty()) {
            JLabel empty = new JLabel("（暂无可学习的新技能）");
            empty.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            learnPanel.add(empty);
        } else {
            for (Skill skill : learnable) {
                learnPanel.add(createLearnRow(skill));
            }
        }
        updateHint();
        slotPanel.revalidate();
        slotPanel.repaint();
        learnPanel.revalidate();
        learnPanel.repaint();
    }

    private JPanel createSlotRow(Skill skill) {
        JLabel name = new JLabel(skill.getName());
        name.setFont(new Font("微软雅黑", Font.BOLD, 14));
        name.setPreferredSize(new Dimension(110, 26));

        JLabel desc = new JLabel(skill.getDescription());
        desc.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        desc.setForeground(new Color(80, 80, 80));

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.add(name);
        row.add(desc);
        row.add(Box.createHorizontalGlue());
        if (skill != Skill.NORMAL_ATTACK) {
            JButton forget = new JButton("遗忘");
            forget.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            forget.setPreferredSize(new Dimension(64, 24));
            forget.addActionListener(e -> doForget(skill));
            row.add(Box.createHorizontalStrut(8));
            row.add(forget);
        } else {
            JLabel locked = new JLabel("（不可遗忘）");
            locked.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            locked.setForeground(new Color(140, 140, 140));
            row.add(locked);
        }
        return row;
    }

    private JPanel createLearnRow(Skill skill) {
        JLabel name = new JLabel(skill.getName());
        name.setFont(new Font("微软雅黑", Font.BOLD, 14));
        name.setPreferredSize(new Dimension(110, 26));

        JLabel desc = new JLabel(skill.getDescription());
        desc.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        desc.setForeground(new Color(80, 80, 80));

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.add(name);
        row.add(desc);
        row.add(Box.createHorizontalGlue());
        JButton learn = new JButton("学习");
        learn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        learn.setPreferredSize(new Dimension(64, 24));
        learn.addActionListener(e -> doLearn(skill));
        row.add(Box.createHorizontalStrut(8));
        row.add(learn);
        return row;
    }

    private void doLearn(Skill skill) {
        if (player.getSkills().contains(skill)) {
            JOptionPane.showMessageDialog(this, "已经学会了这个技能。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!player.hasFreeSlot()) {
            JOptionPane.showMessageDialog(this,
                    "技能栏已满（最多 " + Skill.MAX_SKILL_SLOTS + " 个）。\n请先在上方遗忘一个技能再学习。",
                    "技能栏已满", JOptionPane.WARNING_MESSAGE);
            return;
        }
        player.learnSkill(skill);
        refreshAll();
    }

    private void doForget(Skill skill) {
        int result = JOptionPane.showConfirmDialog(this,
                "确定遗忘「" + skill.getName() + "」吗？\n遗忘后本次奖励中仍可重新学习。",
                "遗忘技能", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            player.forgetSkill(skill);
            refreshAll();
        }
    }

    private void updateHint() {
        if (player.hasFreeSlot()) {
            hintLabel.setText("技能栏： " + player.getSkills().size() + " / " + Skill.MAX_SKILL_SLOTS
                    + "，本关可学习 1 个新技能");
        } else {
            hintLabel.setText("技能栏已满，需遗忘 1 个技能后才能学习新技能");
        }
    }

    private void doFinish() {
        dispose();
        if (onFinished != null) {
            onFinished.run();
        }
    }

    public void start() {
        setVisible(true);
    }
}