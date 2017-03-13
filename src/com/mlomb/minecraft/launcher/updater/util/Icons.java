package com.mlomb.minecraft.launcher.updater.util;

import javax.swing.*;

public class Icons {
	public static ImageIcon blue, blue_hover;

	public Icons() {
		blue = load("/buttons/blue_445x23.png");
		blue_hover = load("/buttons/blue_hover_445x23.png");
	}

	private ImageIcon load(String iconName) {
		return new ImageIcon(this.getClass().getResource(iconName));
	}

	private ImageIcon load(String iconName, int w, int h) {
		return resize(load(iconName), w, h);
	}

	public static ImageIcon resize(ImageIcon imgIcon, int w, int h) {
		java.awt.Image image = imgIcon.getImage();
		java.awt.Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(newimg);
	}
}