package com.mlomb.minecraft.launcher.updater.util;

import java.io.*;
import java.math.*;
import java.security.*;

public class Util {
	public static String getMD5(final File file) {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			final byte[] buffer = new byte[65536];
			for (int read = stream.read(buffer); read >= 1; read = stream.read(buffer)) {
			}
		} catch (Exception ignored) {
			return null;
		} finally {
			try {
				if (stream != null) stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return String.format("%1$032x", new BigInteger(1, stream.getMessageDigest().digest()));
	}

	public static String fileAsString(String file) {
		BufferedReader reader = null;
		StringBuilder stringBuilder = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			System.err.println("Error loading: " + file);
			return null;
		} finally {
			if (reader != null) try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
}