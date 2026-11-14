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
import java.util.Optional;

public class Login extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public Login() {
        setTitle("登录");
        setSize(460, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel content = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        JLabel title = new JLabel("用户登录", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(46, 26, 58));
        title.setBorder(BorderFactory.createEmptyBorder(24, 0, 12, 0));
        content.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(true);
        form.setBackground(new Color(255, 255, 255, 210));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 235), 2),
                BorderFactory.createEmptyBorder(18, 26, 18, 26)));
        form.add(createRow("用户名：", usernameField));
        form.add(Box.createVerticalStrut(12));
        form.add(createRow("密码：", passwordField));

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);
        content.add(center, BorderLayout.CENTER);

        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("去注册");
        JButton backButton = new JButton("返回");
        loginButton.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> new Register().start());
        backButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(loginButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        buttons.add(registerButton);
        buttons.add(backButton);
        content.add(buttons, BorderLayout.SOUTH);
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

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("请输入用户名");
            usernameField.requestFocusInWindow();
            return;
        }
        if (password.isEmpty()) {
            showError("请输入密码");
            passwordField.requestFocusInWindow();
            return;
        }

        Optional<User> user = UserStore.getInstance().login(username, password);
        if (user.isEmpty()) {
            showError("用户名或密码错误");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "欢迎回来，" + user.get().getUsername() + "！\n用户 ID：" + user.get().getUserId(),
                "登录成功", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new CharacterCreate(user.get().getUsername()).start();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE);
    }

    public void start() {
        setVisible(true);
        System.out.println("登录界面打开了");
    }
}