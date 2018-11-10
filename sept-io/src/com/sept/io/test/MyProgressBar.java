package com.sept.io.test;

import java.awt.BorderLayout;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class MyProgressBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private JLabel lblNewLabel;
	private long allSize;
	private BigDecimal bigAll;
	private BigDecimal nowFinshStep;
	private boolean isFinsh = false;
	private JLabel title;
	private Object key = new Object();
	private int allNumber = 0;
	private int finshNumber = 0;

	/**
	 * Create the panel.
	 */
	public MyProgressBar(long allSize) {
		this();
		this.setAllSize(allSize);
	}

	public MyProgressBar() {
		setLayout(new BorderLayout(0, 0));
		lblNewLabel = new JLabel();
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblNewLabel, BorderLayout.SOUTH);
		progressBar = new JProgressBar();
		add(progressBar, BorderLayout.CENTER);
		progressBar.setStringPainted(true);//

		title = new JLabel();
		add(title, BorderLayout.NORTH);
		nowFinshStep = new BigDecimal(0);
	}

	public void setAllNumber(int allNumber) {
		synchronized (key) {
			this.allNumber = allNumber;
		}
	}

	public void finshOneNumber() {
		synchronized (key) {
			this.finshNumber++;
			this.setShowText("已完成[" + finshNumber + "/" + allNumber + "]["
					+ UnitConversionUtil.formatBitToASUnit(nowFinshStep.longValue()) + "/"
					+ UnitConversionUtil.formatBitToASUnit(allSize) + "]");
		}
	}

	public void setShowText(String text) {
		synchronized (key) {
			this.lblNewLabel.setText(text);
		}
	}

	public void setTitle(String text) {
		synchronized (key) {
			this.title.setText(text);
		}
	}

	public String goStep(long step) {
		synchronized (key) {
			if (isFinsh) {
				return "100.00%";
			}
			BigDecimal bigAdd = new BigDecimal(Long.toString(step));
			nowFinshStep = nowFinshStep.add(bigAdd);
			this.setShowText("已完成[" + finshNumber + "/" + allNumber + "]["
					+ UnitConversionUtil.formatBitToASUnit(nowFinshStep.longValue()) + "/"
					+ UnitConversionUtil.formatBitToASUnit(allSize) + "]");

			return setNowSize(nowFinshStep);
		}
	}

	public String setNowSize(BigDecimal nowFinshStep) {
		synchronized (key) {

			this.nowFinshStep = nowFinshStep;

			BigDecimal bigDecimalDivide = nowFinshStep.divide(bigAll, 4, BigDecimal.ROUND_HALF_UP);
			double divide = bigDecimalDivide.doubleValue() * 100;
			String bfb = Double.toString(divide);
			String[] bfbs = bfb.split("\\.");
			bfb = bfbs[0];
			String xs = bfbs[1].substring(0, bfbs[1].length() > 3 ? 3 : bfbs[1].length());
			if (xs.length() == 3) {
				int k = Integer.parseInt(xs.charAt(2) + "");
				// System.out.println(k+" :"+ xs.charAt(2));
				xs = xs.substring(0, 2);
				int xsInt = Integer.parseInt(xs);
				if (k >= 5) {
					xsInt++;
				}
				xs = xsInt + "";
			}
			// System.out.println(xs);
			while (xs.length() < 2) {
				xs += "0";
			}
			while (xs.length() > 2) {
				xs = xs.substring(0, xs.length() - 1);
			}
			bfb += "." + xs;

			if (divide >= 100.00) {
				isFinsh = true;
				bfb = "100.00";
			}

			this.progressBar.setValue((int) Double.parseDouble(bfb));
			this.progressBar.setString(bfb + "%");
			return bfb + "%";
		}

	}

	public long getAllSize() {
		synchronized (key) {
			return allSize;
		}
	}

	public void setAllSize(long allSize) {
		synchronized (key) {
			this.allSize = allSize;
			this.bigAll = new BigDecimal(Long.toString(allSize));
		}

	}

	public boolean isFinsh() {
		synchronized (key) {
			return this.isFinsh;
		}
	}

	public void reset() {
		synchronized (key) {
			this.setTitle("");
			this.setShowText("");
			this.progressBar.setValue(0);
			this.progressBar.setString("0.00%");
			this.setAllNumber(0);
			this.finshNumber = 0;
			this.isFinsh = false;
			this.nowFinshStep = new BigDecimal(0);
			this.allSize = 0l;
			this.bigAll = null;

		}
	}
}
