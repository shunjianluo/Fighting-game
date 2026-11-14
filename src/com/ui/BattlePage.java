package com.ui;

import com.model.Element;
import com.model.Enemy;
import com.model.EnemyFactory;
import com.model.EnemySkill;
import com.model.PlayerStats;
import com.model.Skill;
import com.model.SkillResult;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;

public class BattlePage extends JFrame {

    public static final int FIRST_REWARD_STAGE = 2;
    public static final int SECOND_REWARD_STAGE = 4;
    public static final int STAGES_BETWEEN_REWARD = 3;
    public static final int REWARD_HEAL_PERCENT = 35;
    public static final int STAGE_CLEAR_HEAL_PERCENT = 12;
    public static final int REWARD_BONUS_POINTS = 5;

    private final PlayerStats player;
    private Enemy enemy;

    private final Random random = new Random();
    private final HpBar playerHpBar = new HpBar();
    private final HpBar enemyHpBar = new HpBar();
    private final JTextArea logArea = new JTextArea();
    private final JPanel skillPanel = new JPanel(new GridLayout(1, Skill.MAX_SKILL_SLOTS, 10, 0));
    private final JLabel enemyNameLabel = new JLabel();
    private final JLabel enemyElementLabel = new JLabel();
    private final JLabel playerNameLabel = new JLabel();
    private final JLabel playerElementLabel = new JLabel();
    private final JLabel stageLabel = new JLabel();

    private int stage;
    private boolean playerTurn = true;
    private boolean battleOver = false;

    public BattlePage(PlayerStats player) {
        this(player, 1);
    }

    public BattlePage(PlayerStats player, int startStage) {
        this.player = player;
        this.stage = Math.max(1, startStage);

        setTitle("战斗");
        setSize(760, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 8));

        BackgroundPanel field = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        field.setLayout(new BoxLayout(field, BoxLayout.Y_AXIS));
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        field.add(createStageRow());
        field.add(Box.createVerticalStrut(8));
        field.add(createActorBox(enemyNameLabel, enemyElementLabel, enemyHpBar, true));
        field.add(Box.createVerticalStrut(18));
        field.add(createActorBox(playerNameLabel, playerElementLabel, playerHpBar, false));
        field.add(Box.createVerticalGlue());
        add(field, BorderLayout.CENTER);

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("战斗记录"));
        logScroll.setPreferredSize(new Dimension(0, 120));

        refreshSkillButtons();

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        bottom.add(logScroll, BorderLayout.CENTER);
        bottom.add(skillPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        playerNameLabel.setText("你：" + player.getUsername());
        updateElementLabel(playerElementLabel, player.getElement());

        startStage();
    }

    private JPanel createStageRow() {
        stageLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        stageLabel.setForeground(Color.WHITE);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(true);
        row.setBackground(new Color(40, 30, 54, 200));
        row.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        row.add(stageLabel);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(row);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return wrapper;
    }

    private JPanel createActorBox(JLabel nameLabel, JLabel elementLabel, HpBar hpBar, boolean enemySide) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(true);
        box.setBackground(new Color(248, 248, 244, 214));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60, 220), 2),
                BorderFactory.createEmptyBorder(8, 14, 12, 14)));
        box.setMaximumSize(new Dimension(360, 130));

        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        elementLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        elementLabel.setOpaque(true);
        elementLabel.setForeground(Color.WHITE);
        elementLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        elementLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        hpBar.setPreferredSize(new Dimension(330, 36));
        hpBar.setMaximumSize(new Dimension(330, 36));
        hpBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(nameLabel);
        box.add(Box.createVerticalStrut(4));
        box.add(elementLabel);
        box.add(Box.createVerticalStrut(6));
        box.add(hpBar);

        JPanel wrapper = new JPanel(new FlowLayout(enemySide ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        wrapper.add(box);
        wrapper.setOpaque(false);
        return wrapper;
    }

    private void updateElementLabel(JLabel label, Element element) {
        label.setText(element.getLabel() + " 属性");
        label.setBackground(elementColor(element));
    }

    private Color elementColor(Element element) {
        switch (element) {
            case FIRE -> {
                return new Color(214, 78, 48);
            }
            case GRASS -> {
                return new Color(58, 152, 78);
            }
            case WATER -> {
                return new Color(52, 112, 200);
            }
            default -> {
                return new Color(96, 96, 96);
            }
        }
    }

    private void startStage() {
        enemy = EnemyFactory.createForStage(stage, random, player.getElement());
        enemyNameLabel.setText("敌人：" + enemy.getName());
        updateElementLabel(enemyElementLabel, enemy.getElement());
        updateStageLabel();
        refreshBars();
        logArea.setText("");
        log("第 " + stage + " 关开始！野生「" + enemy.getName() + "」出现了！");
        if (enemy.getDefense() > 0) {
            log("敌方防御值 " + enemy.getDefense() + "，减伤 " + enemy.getDefensePercent() + "%");
        }
        log("敌方技能：");
        log(EnemyFactory.describeSkills(enemy).trim());
        battleOver = false;
        playerTurn = true;
        player.clearShield();
        player.clearCooldowns();
        refreshSkillButtons();
    }

    private boolean isRewardStage() {
        return isRewardStage(stage);
    }

    public static boolean isRewardStage(int stageNumber) {
        if (stageNumber == FIRST_REWARD_STAGE || stageNumber == SECOND_REWARD_STAGE) {
            return true;
        }
        if (stageNumber < SECOND_REWARD_STAGE) {
            return false;
        }
        return (stageNumber - SECOND_REWARD_STAGE) % STAGES_BETWEEN_REWARD == 0;
    }

    private int stagesUntilReward() {
        if (isRewardStage()) {
            return 0;
        }
        for (int ahead = 1; ahead <= STAGES_BETWEEN_REWARD; ahead++) {
            if (isRewardStage(stage + ahead)) {
                return ahead;
            }
        }
        return STAGES_BETWEEN_REWARD;
    }

    private void updateStageLabel() {
        int toReward = stagesUntilReward();
        String rewardHint = toReward == 0 ? "（通关可获得奖励）" : "（再闯 " + toReward + " 关可获得奖励）";
        stageLabel.setText("第 " + stage + " 关" + rewardHint);
    }

    private void onSkillSelected(Skill skill) {
        if (!playerTurn || battleOver || !isBattleOngoing()) {
            return;
        }
        if (!player.canUse(skill)) {
            log("「" + skill.getName() + "」还在冷却中（剩余 " + player.getRemainingCooldown(skill) + " 回合）");
            return;
        }
        playerTurn = false;
        setSkillButtonsEnabled(false);

        SkillResult result = skill.use(player, enemy);
        log("你使用了「" + skill.getName() + "」！" + result.getMessage());
        refreshBars();

        if (!enemy.isAlive()) {
            finishStage();
            return;
        }
        if (!player.isAlive()) {
            finish(false);
            return;
        }

        Timer timer = new Timer(600, e -> enemyTurn());
        timer.setRepeats(false);
        timer.start();
    }

    private void enemyTurn() {
        if (battleOver || !isBattleOngoing()) {
            return;
        }
        player.clearShield();
        EnemySkill skill = enemy.chooseSkill(random);
        SkillResult result = skill.use(enemy, player, random);
        String reduced = player.getDefensePercent() > 0
                ? "（防御减免 " + player.getDefensePercent() + "%）" : "";
        log("「" + enemy.getName() + "」使用了" + result.getMessage() + reduced);
        refreshBars();

        if (!player.isAlive()) {
            finish(false);
            return;
        }
        player.tickCooldowns();
        playerTurn = true;
        refreshSkillButtons();
    }

    private void finishStage() {
        setSkillButtonsEnabled(false);
        log("你击败了「" + enemy.getName() + "」！");

        if (!isRewardStage()) {
            int beforeHp = player.getCurrentHp();
            player.healPercentOfMax(STAGE_CLEAR_HEAL_PERCENT);
            int healed = player.getCurrentHp() - beforeHp;
            log("整备阶段：回复了 " + healed + " 点生命。");
            int result = JOptionPane.showConfirmDialog(this,
                    "第 " + stage + " 关通过！\n回复了 " + healed + " 点生命（当前 "
                            + player.getCurrentHp() + " / " + player.getMaxHp() + "）。\n是否挑战第 "
                            + (stage + 1) + " 关？",
                    "关卡通过", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                dispose();
                return;
            }
            stage++;
            startStage();
            return;
        }

        grantReward();
    }

    private void grantReward() {
        log("通过第 " + stage + " 关，获得通关奖励。");
        int result = JOptionPane.showConfirmDialog(this,
                "恭喜通过第 " + stage + " 关！\n\n通关奖励：\n"
                        + "- 回复 " + REWARD_HEAL_PERCENT + "% 最大生命值\n"
                        + "- 获得 " + REWARD_BONUS_POINTS + " 点可分配属性点\n"
                        + "- 可学习 1 个新技能\n\n"
                        + "接下来依次进行「属性分配」与「技能学习」。\n是否继续？",
                "闯关奖励", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            dispose();
            return;
        }

        player.healPercentOfMax(REWARD_HEAL_PERCENT);
        player.addBonusPoints(REWARD_BONUS_POINTS);
        player.clearCooldowns();
        battleOver = true;
        int nextStage = stage + 1;
        dispose();
        new CharacterCreate(player, () -> openSkillLearning(nextStage), false).start();
    }

    private void openSkillLearning(int nextStage) {
        new SkillLearnPage(player, () -> new BattlePage(player, nextStage).start()).start();
    }

    private void finish(boolean win) {
        battleOver = true;
        setSkillButtonsEnabled(false);
        String message = win ? "胜利！" : "你在第 " + stage + " 关被击败了……";
        log(message);
        if (win) {
            JOptionPane.showMessageDialog(this, message, "战斗结算", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }
        Object[] options = {"重新开始", "退出游戏"};
        int choice = JOptionPane.showOptionDialog(this,
                message + "\n\n坚持了 " + stage + " 关，获得 " + player.getBonusPoints()
                        + " 点奖励属性。\n是否重新开始一段新的冒险？",
                "战败结算", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);
        dispose();
        if (choice == JOptionPane.YES_OPTION) {
            new CharacterCreate(player.getUsername()).start();
        } else {
            new Game().start();
        }
    }

    private boolean isBattleOngoing() {
        return player.isAlive() && enemy.isAlive();
    }

    private void refreshBars() {
        playerHpBar.setValues(player.getCurrentHp(), player.getMaxHp());
        enemyHpBar.setValues(enemy.getCurrentHp(), enemy.getMaxHp());
    }

    private void refreshSkillButtons() {
        skillPanel.removeAll();
        for (Skill skill : player.getSkills()) {
            JButton button = new JButton(buttonTextFor(skill));
            button.setFont(new Font("微软雅黑", Font.BOLD, 14));
            button.setToolTipText(skill.getDescription());
            button.setPreferredSize(new Dimension(0, 46));
            button.setEnabled(playerTurn && !battleOver && player.canUse(skill));
            button.addActionListener(e -> onSkillSelected(skill));
            skillPanel.add(button);
        }
        for (int i = player.getSkills().size(); i < Skill.MAX_SKILL_SLOTS; i++) {
            JButton empty = new JButton("空技能槽");
            empty.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            empty.setEnabled(false);
            skillPanel.add(empty);
        }
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    private String buttonTextFor(Skill skill) {
        if (player.canUse(skill)) {
            return skill.getName();
        }
        return skill.getName() + "（" + player.getRemainingCooldown(skill) + "）";
    }

    private void setSkillButtonsEnabled(boolean enabled) {
        Component[] components = skillPanel.getComponents();
        List<Skill> skills = player.getSkills();
        for (int i = 0; i < components.length; i++) {
            if (!(components[i] instanceof JButton button)) {
                continue;
            }
            boolean hasSkill = i < skills.size();
            button.setEnabled(enabled && hasSkill && player.canUse(skills.get(i)));
            if (hasSkill) {
                button.setText(buttonTextFor(skills.get(i)));
            }
        }
    }

    private void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void start() {
        setVisible(true);
    }
}