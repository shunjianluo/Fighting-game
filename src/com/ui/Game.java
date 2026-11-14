package com.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class Game extends JFrame {

    public Game() {
        setTitle("Fighting Game");
        setSize(960, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel content = new BackgroundPanel(Resources.BACKGROUND_IMAGE);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        JLabel title = new JLabel("Fighting Game", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 40));
        title.setForeground(new Color(46, 26, 58));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        title.setPreferredSize(new Dimension(0, 90));
        content.add(title, BorderLayout.NORTH);

        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        JButton exitButton = new JButton("退出");

        for (JButton button : new JButton[]{loginButton, registerButton, exitButton}) {
            button.setFont(new Font("微软雅黑", Font.BOLD, 20));
            button.setPreferredSize(new Dimension(220, 50));
            button.setFocusPainted(false);
        }

        loginButton.addActionListener(e -> new Login().start());
        registerButton.addActionListener(e -> new Register().start());
        exitButton.addActionListener(e -> System.exit(0));

        JPanel menu = new JPanel(new GridLayout(3, 1, 0, 18));
        menu.setOpaque(true);
        menu.setBackground(new Color(255, 255, 255, 205));
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 235), 2),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));
        menu.setPreferredSize(new Dimension(280, 234));
        menu.setMinimumSize(new Dimension(280, 234));
        menu.add(loginButton);
        menu.add(registerButton);
        menu.add(exitButton);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        center.add(menu);
        content.add(center, BorderLayout.CENTER);
    }

    public void start() {
        setVisible(true);
    }
}