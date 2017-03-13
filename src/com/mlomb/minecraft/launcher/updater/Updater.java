package com.mlomb.minecraft.launcher.updater;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.imageio.*;
import javax.swing.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import com.mlomb.minecraft.launcher.updater.lang.*;
import com.mlomb.minecraft.launcher.updater.ui.*;
import com.mlomb.minecraft.launcher.updater.ui.SplashScreen;
import com.mlomb.minecraft.launcher.updater.util.*;

public class Updater {

	public static File CFG;
	public static File PATH;

	public static PathChooser pc;
	private static SplashScreen ss;
	private static JSONParser parser;

	public static String VERSION_NAME, VERSION_FILE, VERSION_FILE_TYPE;
	public static int VERSION_DOWNLOADS;

	public static Font font;
	private static boolean isInstall = false;

	public Updater() {
		// Splash screen
		Image img = null;
		try {
			BufferedImage imgs = ImageIO.read(Updater.class.getResource("/splashscreen.png"));
			img = imgs;
		} catch (Exception e) {
			System.out.println("Can't load splashscreen (ignore): " + e.getMessage());
		}
		ss = new SplashScreen(img);
		ss.showSplashScreen(true);
		// Load font
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Updater.class.getResourceAsStream("/font.ttf"));
			font = font.deriveFont(14f);
		} catch (IOException | FontFormatException e) {
		}
		// Load icons
		new Icons();
		// Start
		CFG = new File(System.getProperty("user.home"), "OrbitLauncher.config");
		parser = new JSONParser();

		if (CFG.exists() && !CFG.isDirectory()) {
			String path = Util.fileAsString(CFG.getAbsolutePath());
			if (path != null && path != "") {
				try {
					JSONObject obj = (JSONObject) parser.parse(path);
					path = (String) obj.get("path");
					new File(path).getCanonicalFile(); // Crash if is invalid
					PATH = new File(path);
					start();
					return;
				} catch (ParseException e) {
				} catch (IOException e) {
				}
			}
		}
		pc = new PathChooser();
		ss.showSplashScreen(false);
		pc.setVisible(true);
	}

	private static void start() {
		final File jf = new File(PATH, "launcher.jar");

		int status = checkStatus();
		switch (status) {
		case 0:
			run(PATH);
			break;
		case 1:
			Thread t = new Thread() {
				public void run() {
					Update upd = new Update();
					ss.showSplashScreen(false);

					boolean success = upd.download("http://download.olc.pvporbit.com/d/" + VERSION_FILE, jf, false);

					if (!success) {
						upd.dispose();
						ss.showSplashScreen(true);
						error();
					} else {
						upd.dispose();
						Updater.run(PATH);
					}
				}
			};
			t.start();
			break;
		case 2:
			error();
			break;
		}
	}

	private static void error() {
		File jf = new File(PATH, "launcher.jar");
		boolean exists = false;
		if (jf.exists() && !jf.isDirectory()) exists = true;

		if (exists) {
			run(jf); // Run anyway
		} else {
			InputStream inputStream = null;
			OutputStream outputStream = null;

			try {
				inputStream = Updater.class.getResourceAsStream("/backup.jar");
				if (inputStream == null) throw new IOException();
				outputStream = new FileOutputStream(jf);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				run(jf);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, Lang.getText("error3"), Lang.getText("title"), 0);
				JOptionPane.showMessageDialog(null, Lang.getText("error1"), Lang.getText("title"), 0);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
			System.exit(0);
		}
	}

	private static void run(File jarFile) {
		try {
			String command = "java -jar \"" + new File(PATH, "launcher.jar").getAbsolutePath() + "\" \"" + PATH.getAbsolutePath() + "\" \"" + new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath() + "\"";
			System.out.println("Executing " + command);
			System.exit(0);
			Runtime.getRuntime().exec(command);
		} catch (IOException | URISyntaxException e) {
			JOptionPane.showMessageDialog(null, Lang.getText("cantstart") + " " + e.getMessage());
		}
		System.exit(0);
	}

	private static int checkStatus() {
		if (!PATH.exists()) PATH.mkdirs();
		File jarfile = new File(PATH, "launcher.jar");
		try {
			URL u = new URL("http://olc.pvporbit.com/latest/");
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			huc.setConnectTimeout(10000);
			huc.connect();
			InputStream r = huc.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(r));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			if (stringBuilder.length() < 5) return 2;
			String md5 = null;
			try {
				JSONObject obj = (JSONObject) parser.parse(stringBuilder.toString());
				boolean error = (boolean) obj.get("error");
				if (!error) {
					VERSION_NAME = (String) obj.get("version");
					md5 = (String) obj.get("md5");
					VERSION_FILE = (String) obj.get("file");
					VERSION_FILE_TYPE = (String) obj.get("fileType");
					VERSION_DOWNLOADS = (int) (long) obj.get("downloads");
				} else
					return 2;
			} catch (ParseException e) {
				return 2;
			}
			if (!jarfile.exists() && !jarfile.isDirectory()) return 1;
			String jarMd5 = Util.getMD5(jarfile);
			if (!md5.equals(jarMd5)) {
				return 1; // Need Update
			} else { // Up to date
				return 1; // TODO CHANGE TO 0
			}
		} catch (SocketTimeoutException e) {
			return 2; // Time out
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}
	}

	public static void use(String path) {
		pc.setVisible(false);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(CFG, "UTF-8");
			writer.print("{\"path\": \"" + path + "\"}");
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Lang.getText("error2") + " " + CFG.getAbsolutePath(), Lang.getText("title"), 0);
			JOptionPane.showMessageDialog(null, Lang.getText("error1"), Lang.getText("title"), 0);
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (writer != null) writer.close();
		}
		PATH = new File(path);
		Updater.start();
	}

	public static void main(String[] args) {
		new Updater();
	}

	public static boolean isInstall() {
		return isInstall;
	}

	public static void setInstall(boolean install) {
		isInstall = install;
	}
}