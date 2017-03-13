package com.mlomb.minecraft.launcher.updater.lang;

import java.util.*;

public class Lang {

	private static ResourceBundle lang;

	private Lang() {
	}

	static {
		lang = ResourceBundle.getBundle("lang");
	}

	public static String getText(String key) {
		if (lang.containsKey(key)) return lang.getString(key);
		return "?";
	}
}