package com.mlomb.minecraft.launcher.updater.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import com.mlomb.minecraft.launcher.updater.*;
import com.mlomb.minecraft.launcher.updater.lang.*;

public class Update extends JFrame {
	private static final long serialVersionUID = 1L;

	private JLabel status;
	private JLabel status2;
	private JLabel status3;
	private JLabel status4;
	private Dimension size = new Dimension(300, 169);

	int posX;
	int posY;

	private JProgressBar bar;

	public Update() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		setSize(size);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		setTitle(Lang.getText("updating") + " " + Updater.VERSION_NAME);
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				posX = e.getX();
				posY = e.getY();
			}
		});
		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseDragged(MouseEvent evt)
			{
				setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
		// Icon
		try {
			BufferedImage imgs = ImageIO.read(Updater.class.getResource("/icon.png"));
			ImageIcon icon = new ImageIcon(imgs);

			BufferedImage imgs2 = ImageIO.read(Updater.class.getResource("/background.png"));
			ImageIcon icon2 = new ImageIcon(imgs2);

			setIconImage(icon.getImage());
			setContentPane(new JLabel(icon2));
		} catch (Exception e) {
			System.out.println("Can't load icon: " + e.getMessage());
		}
		// -Icon

		JLabel title = new JLabel(Updater.isInstall() ? Lang.getText("instext") : Lang.getText("updtext"));
		title.setBounds(5, 5, getWidth() - 10, 15);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(Updater.font);
		add(title);

		bar = new JProgressBar();
		bar.setOpaque(false);
		bar.setFont(Updater.font.deriveFont(14f));
		bar.setBackground(new Color(255, 255, 255, 0));
		bar.setUI(new ProgressBarUI());
		bar.setBorder(null);
		bar.setBounds(0, size.height - 25, size.width, 15);
		add(bar);

		status = new JLabel("?/?");
		status.setBounds(10, size.height - 45, size.width - 10, 15);
		status.setHorizontalAlignment(JLabel.LEFT);
		status.setForeground(Color.WHITE);
		status.setFont(Updater.font.deriveFont(11f));
		add(status);

		status2 = new JLabel(Lang.getText("downloading") + " -");
		status2.setBounds(10, size.height - 80, 300, 15);
		status2.setForeground(Color.WHITE);
		status2.setFont(Updater.font.deriveFont(11f));
		add(status2);

		status3 = new JLabel("00:00:00");
		status3.setBounds(5, size.height - 45, size.width - 10, 15);
		status3.setHorizontalAlignment(JLabel.RIGHT);
		status3.setForeground(Color.WHITE);
		status3.setFont(Updater.font.deriveFont(12f));
		add(status3);

		status4 = new JLabel(Lang.getText("downloads") + ": ?");
		status4.setBounds(10, size.height - 65, 300, 15);
		status4.setForeground(Color.WHITE);
		status4.setFont(Updater.font.deriveFont(11f));
		add(status4);

		setVisible(true);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public boolean download(String URL, File dest, boolean retry) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);

		BufferedOutputStream bout = null;
		FileOutputStream fos = null;
		BufferedInputStream in = null;
		DecimalFormat f = new DecimalFormat("#,###");
		String[] s = Updater.VERSION_FILE.split("/");
		status2.setText(Lang.getText("downloading") + " " + s[s.length - 1]);
		status4.setText(Lang.getText("downloads") + ": " + f.format(Updater.VERSION_DOWNLOADS));
		try {
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int filesize = connection.getContentLength();
			String sizeTotal = humanReadableByteCount(filesize, true);
			float totalDataRead = 0;
			in = new java.io.BufferedInputStream(connection.getInputStream());
			fos = new java.io.FileOutputStream(dest);
			bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int i = 0;
			long start = System.currentTimeMillis();
			long elapsed;
			int downloaded = 0;
			String time = "00:00:00";
			while ((i = in.read(data, 0, 1024)) >= 0) {
				totalDataRead += i;
				bout.write(data, 0, i);
				float Percent = (totalDataRead * 100) / filesize;
				bar.setValue((int) Percent);
				status.setText(humanReadableByteCount((int) totalDataRead, true) + "/" + sizeTotal);

				elapsed = System.currentTimeMillis() - start;
				downloaded += i;
				if (elapsed >= 1000) {
					int t = (int) ((filesize - totalDataRead) / downloaded) * 1000;
					time = df.format(new Date(t));
					start = System.currentTimeMillis();
					downloaded = 0;
				}
				status3.setText(time);
			}
			bout.close();
			in.close();
			fos.close();
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			status3.setText(Lang.getText("done"));
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}

			return true;
		} catch (IOException ex) {
			System.out.println("Error downloading: " + ex.getMessage());
			if (retry) {
				return false;
			} else {
				status3.setText(Lang.getText("retrying"));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				return download(URL, dest, true);
			}
		} finally {
			try {
				if (bout != null) bout.close();
				if (in != null) in.close();
				if (fos != null) fos.close();
			} catch (Exception ex) {
			}
		}
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
