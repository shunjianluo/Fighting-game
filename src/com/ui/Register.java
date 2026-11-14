package com.ui;

import com.model.User;
import com.model.UserStore;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.Random;
import java.util.regex.Pattern;

public class Register extends JFrame {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{6,20}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[^\\s]{3,20}$");
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();
    private final JTextField codeField = new JTextField();
    private final JLabel codeLabel = new JLabel();
    private String code;

    public Register() {
        setTitle("注册");
        setSize(480, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel content = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        JLabel title = new JLabel("用户注册", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(46, 26, 58));
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 8, 0));
        content.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(true);
        form.setBackground(new Color(255, 255, 255, 210));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 235), 2),
                BorderFactory.createEmptyBorder(16, 26, 16, 26)));
        form.add(createRow("用户名：", usernameField));
        form.add(Box.createVerticalStrut(10));
        form.add(createRow("密码：", passwordField));
        form.add(Box.createVerticalStrut(10));
        form.add(createRow("确认密码：", confirmField));
        form.add(Box.createVerticalStrut(10));
        form.add(createRow("验证码：", codeField));
        form.add(Box.createVerticalStrut(10));
        form.add(createCodeRow());

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);
        content.add(center, BorderLayout.CENTER);

        JLabel hint = new JLabel("密码需为字母与数字混合，长度 6-20 位");
        hint.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        hint.setForeground(new Color(46, 26, 58));
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hintPanel.setOpaque(false);
        hintPanel.add(hint);

        JButton registerButton = new JButton("注册");
        JButton backButton = new JButton("返回");
        registerButton.addActionListener(e -> doRegister());
        backButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        buttons.setOpaque(false);
        buttons.add(registerButton);
        buttons.add(backButton);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.add(hintPanel);
        bottom.add(buttons);
        content.add(bottom, BorderLayout.SOUTH);

        refreshCode();
    }

    private JPanel createCodeRow() {
        JLabel label = new JLabel("验证码：");
        label.setPreferredSize(new Dimension(80, 28));

        codeLabel.setPreferredSize(new Dimension(100, 28));
        codeLabel.setOpaque(true);
        codeLabel.setHorizontalAlignment(JLabel.CENTER);
        codeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));

        JButton refresh = new JButton("换一个");
        refresh.addActionListener(e -> refreshCode());

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(codeLabel);
        row.add(Box.createHorizontalStrut(8));
        row.add(refresh);
        row.add(Box.createHorizontalGlue());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return row;
    }

    private void refreshCode() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            builder.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        code = builder.toString();
        codeLabel.setText(code);
        codeField.setText("");
    }

    private JPanel createRow(String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 28));
        field.setPreferredSize(new Dimension(220, 28));
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(field);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return row;
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showError("用户名不能为空、不能含空格，长度需为 3-20 位");
            return;
        }
        if (UserStore.getInstance().usernameExists(username)) {
            showError("该用户名已被注册，请换一个");
            usernameField.requestFocusInWindow();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showError("密码必须由字母和数字混合组成，长度 6-20 位");
            return;
        }
        if (!password.equals(confirm)) {
            showError("两次输入的密码不一致，请重新输入");
            confirmField.setText("");
            confirmField.requestFocusInWindow();
            return;
        }
        String inputCode = codeField.getText().trim();
        if (!inputCode.equalsIgnoreCase(code)) {
            showError("验证码错误，请重新输入");
            refreshCode();
            codeField.requestFocusInWindow();
            return;
        }

        String userId = generateUserId();
        UserStore.getInstance().add(new User(userId, username, password));
        JOptionPane.showMessageDialog(this,
                "注册成功！\n用户名：" + username + "\n用户 ID：" + userId,
                "注册成功", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private String generateUserId() {
        Random random = new Random();
        String id;
        do {
            id = "U" + (10000000 + random.nextInt(90000000));
        } while (UserStore.getInstance().userIdExists(id));
        return id;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE);
    }

    public void start() {
        setVisible(true);
    }
}