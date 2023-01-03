package org.cytoscape.clustnsee3.internal.gui.util;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class CnSButton extends JButton {
	private static final long serialVersionUID = 4800992668517797309L;
	private Insets insets;
	
	public CnSButton() {
		super();
		//insets = new Insets(0, 0, 0, 0);
	}
	public CnSButton(Icon icon) {
		super(icon);
		//insets = new Insets(0, 0, 0, 0);
	}
	public CnSButton(String text) {
		super(text);
		//insets = new Insets(0, 0, 0, 0);
	}
	public CnSButton(Action a) {
		super(a);
		//insets = new Insets(0, 0, 0, 0);
	}
	public CnSButton(String text, Icon icon) {
		super(text, icon);
		//insets = new Insets(0, 0, 0, 0);
	}
	public Insets getInsets() {
		return super.getInsets();
	}
	public void setInsets(int top, int left, int bottom, int right) {
		insets = new Insets(top, left, bottom, right);
	}
}
