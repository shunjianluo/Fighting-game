package com.ui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class HpBar extends JComponent {

    private static final int BAR_HEIGHT = 14;
    private static final Color HP_GREEN = new Color(72, 200, 96);
    private static final Color HP_YELLOW = new Color(238, 190, 60);
    private static final Color HP_RED = new Color(222, 68, 56);

    private int current;
    private int max = 1;

    public void setValues(int current, int max) {
        this.current = Math.max(0, current);
        this.max = Math.max(1, max);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int y = getHeight() - BAR_HEIGHT - 2;

        g2.setColor(new Color(64, 64, 64));
        g2.fillRoundRect(0, y, width, BAR_HEIGHT, 8, 8);

        double ratio = (double) current / max;
        int filled = (int) (width * ratio);
        g2.setColor(colorFor(ratio));
        if (filled > 0) {
            g2.fillRoundRect(1, y + 1, Math.max(0, filled - 2), BAR_HEIGHT - 2, 7, 7);
        }

        g2.setFont(new Font("微软雅黑", Font.BOLD, 12));
        g2.setColor(new Color(50, 50, 50));
        String text = "HP " + current + " / " + max;
        g2.drawString(text, (width - g2.getFontMetrics().stringWidth(text)) / 2, y - 4);

        g2.dispose();
    }

    private Color colorFor(double ratio) {
        if (ratio > 0.5) {
            return HP_GREEN;
        }
        if (ratio > 0.2) {
            return HP_YELLOW;
        }
        return HP_RED;
    }
}