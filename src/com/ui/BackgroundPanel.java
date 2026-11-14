package com.ui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

public class BackgroundPanel extends JPanel {

    private static final Color FALLBACK_COLOR = new Color(238, 232, 245);

    private Image background;

    public BackgroundPanel(String imagePath) {
        setOpaque(false);
        load(imagePath);
    }

    private void load(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            System.out.println("背景图片不存在：" + file.getAbsolutePath());
            return;
        }
        try {
            background = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("读取背景图片失败：" + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (background == null) {
            g.setColor(FALLBACK_COLOR);
            g.fillRect(0, 0, panelWidth, panelHeight);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int imageWidth = background.getWidth(this);
        int imageHeight = background.getHeight(this);

        double scale = Math.max((double) panelWidth / imageWidth, (double) panelHeight / imageHeight);
        int drawWidth = (int) Math.ceil(imageWidth * scale);
        int drawHeight = (int) Math.ceil(imageHeight * scale);
        int x = (panelWidth - drawWidth) / 2;
        int y = (panelHeight - drawHeight) / 2;

        g2.drawImage(background, x, y, drawWidth, drawHeight, this);
        g2.dispose();
    }
}