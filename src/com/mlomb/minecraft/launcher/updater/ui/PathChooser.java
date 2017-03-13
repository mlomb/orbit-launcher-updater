package com.mlomb.minecraft.launcher.updater.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import com.mlomb.minecraft.launcher.updater.*;
import com.mlomb.minecraft.launcher.updater.lang.*;
import com.mlomb.minecraft.launcher.updater.util.Icons;

public class PathChooser extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private JButton finish;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	private JRadioButton radioButton3;
	private JRadioButton radioButton4;
	private JRadioButton radioButton5;
	private boolean canOther = false;
	private Dimension size = new Dimension(465, 292);

	int posX;
	int posY;

	public PathChooser() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
		}
		setTitle(Lang.getText("chooserWindowTitle"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(size);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		setResizable(false);
		setLayout(null);
		setLocationRelativeTo(null);
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				posX = e.getX();
				posY = e.getY();
			}
		});
		MouseAdapter ma = new MouseAdapter()
		{
			public void mouseDragged(MouseEvent evt)
			{
				setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		};
		addMouseMotionListener(ma);
		// Icon
		try {
			BufferedImage imgs = ImageIO.read(Updater.class.getResource("/icon.png"));
			ImageIcon icon = new ImageIcon(imgs);

			BufferedImage imgs2 = ImageIO.read(Updater.class.getResource("/background_2.png"));
			ImageIcon icon2 = new ImageIcon(imgs2);

			setIconImage(icon.getImage());
			setContentPane(new JLabel(icon2));
		} catch (Exception e) {
			System.out.println("Can't load icon: " + e.getMessage());
		}
		// -Icon

		ButtonGroup g = new ButtonGroup();

		final JTextArea lbl1 = new JTextArea(Lang.getText("chooserTitle"));
		lbl1.setFont(Updater.font);
		lbl1.setBounds(10, 10, size.width - 52, 60);
		lbl1.setLineWrap(true);
		lbl1.setWrapStyleWord(true);
		lbl1.setForeground(Color.white);
		lbl1.setBackground(new Color(0, 0, 0, 0));
		lbl1.setEditable(false);
		lbl1.setHighlighter(null);
		lbl1.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				posX = e.getX() + lbl1.getX();
				posY = e.getY() + lbl1.getY();
			}
		});

		lbl1.addMouseMotionListener(ma);
		add(lbl1);

		int m = 40;

		radioButton1 = new JRadioButton("/" + System.getProperty("user.name") + "/.minecraft");
		radioButton1.setOpaque(false);
		radioButton1.setBounds(6, 32 + m, size.width - 12, 23);
		add(radioButton1);
		g.add(radioButton1);

		radioButton2 = new JRadioButton("/" + System.getProperty("user.name") + "/minecraft");
		radioButton2.setSelected(true);
		radioButton2.setBounds(6, 58 + m, size.width - 12, 23);
		radioButton2.setOpaque(false);
		add(radioButton2);
		g.add(radioButton2);

		radioButton3 = new JRadioButton("%appdata%/.minecraft");
		radioButton3.setBounds(6, 84 + m, size.width - 12, 23);
		radioButton3.setOpaque(false);
		add(radioButton3);
		g.add(radioButton3);

		radioButton4 = new JRadioButton("%appdata%/minecraft");
		radioButton4.setOpaque(false);
		radioButton4.setBounds(6, 110 + m, size.width - 12, 23);
		add(radioButton4);
		g.add(radioButton4);

		textField = new JTextField();
		textField.setEditable(false);
		if(Updater.font != null)
			textField.setFont(Updater.font.deriveFont(12f));
		textField.setBounds(10, size.height - 70, size.width - 120 - 10 - 20, 23);
		add(textField);
		textField.setText(Lang.getText("select"));
		textField.setColumns(10);

		final JButton btnNewButton = new JButton(Lang.getText("examine"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				chooser.setDialogTitle(Lang.getText("selectTitle"));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				int response = chooser.showOpenDialog(PathChooser.this);

				if (response == JFileChooser.APPROVE_OPTION) {
					textField.setText(chooser.getSelectedFile().getAbsolutePath());
					canOther = true;
					finish.setEnabled(true);
				}
			}
		});
		btnNewButton.setBackground(new Color(0, 0, 0, 0));
		btnNewButton.setEnabled(false);
		btnNewButton.setBounds(size.width - 120 - 10, size.height - 70, 120, 23);
		if(Updater.font != null)
			btnNewButton.setFont(Updater.font.deriveFont(10f));
		add(btnNewButton);

		radioButton5 = new JRadioButton(Lang.getText("other"));
		radioButton5.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					btnNewButton.setEnabled(true);
					if (!canOther) finish.setEnabled(false);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					btnNewButton.setEnabled(false);
					finish.setEnabled(true);
				}
			}
		});
		radioButton5.setOpaque(false);
		radioButton5.setBounds(6, size.height - 95, size.width - 12, 23);
		add(radioButton5);
		g.add(radioButton5);

		finish = new JButton(Lang.getText("install"));
		finish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path = "";
				String sep = System.getProperty("file.separator");
				if (radioButton1.isSelected()) path = System.getProperty("user.home") + sep + ".minecraft";
				else if (radioButton2.isSelected()) path = System.getProperty("user.home") + sep + "minecraft";
				else if (radioButton3.isSelected()) path = System.getenv("APPDATA") + sep + ".minecraft";
				else if (radioButton4.isSelected()) path = System.getenv("APPDATA") + sep + "minecraft";
				else if (radioButton5.isSelected()) path = textField.getText();
				finish.setEnabled(false);
				Updater.setInstall(true);
				Updater.use(path);
			}
		});
		finish.setBounds(10, size.height - 35, size.width - 20, 25);
		if(Updater.font != null)
			finish.setFont(Updater.font);
		finish.setHorizontalTextPosition(SwingConstants.CENTER);
		finish.setForeground(Color.white);
		finish.setContentAreaFilled(false);
		finish.setBorder(BorderFactory.createEmptyBorder());
		finish.setIcon(Icons.blue);
		finish.setRolloverIcon(Icons.blue_hover);
		finish.setPressedIcon(Icons.blue_hover);
		add(finish);

		JButton button = new JButton();
		try {
			Image img = ImageIO.read(getClass().getResource("/close.png"));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		button.setBounds(size.width - 32, 16, 18, 18);
		button.setBorder(null);
		button.setBorderPainted(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setContentAreaFilled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		add(button);

		radioButton1.setForeground(Color.WHITE);
		radioButton2.setForeground(Color.WHITE);
		radioButton3.setForeground(Color.WHITE);
		radioButton4.setForeground(Color.WHITE);
		radioButton5.setForeground(Color.WHITE);

		radioButton1.setFont(Updater.font);
		radioButton2.setFont(Updater.font);
		radioButton3.setFont(Updater.font);
		radioButton4.setFont(Updater.font);
		radioButton5.setFont(Updater.font);
	}
}
