package com.mlomb.minecraft.launcher.updater.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;

class ProgressBarUI extends BasicProgressBarUI {

	@Override
	protected Color getSelectionBackground() {
		return Color.white;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.setColor(new Color(0, 0, 0, 90));
		int w = (int) (progressBar.getPercentComplete() * progressBar.getWidth());
		g.fillRect(0, 0, w, progressBar.getHeight());

		Insets b = progressBar.getInsets();
		int barRectWidth = progressBar.getWidth() - b.right - b.left;
		int barRectHeight = progressBar.getHeight() - b.top - b.bottom;

		paintString(g, b.left, b.top, barRectWidth, barRectHeight, 0, b);
		c.repaint();
	}
}