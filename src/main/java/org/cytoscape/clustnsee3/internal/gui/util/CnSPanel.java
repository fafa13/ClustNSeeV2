package org.cytoscape.clustnsee3.internal.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class CnSPanel extends JPanel {
	private static final long serialVersionUID = -5064107269789069026L;
	public static Color shCol = UIManager.getColor("TextField.darkShadow");
	public static final int WEST = GridBagConstraints.WEST;
	public static final int NORTHWEST = GridBagConstraints.NORTHWEST;
	public static final int NORTH = GridBagConstraints.NORTH;
	public static final int CENTER = GridBagConstraints.CENTER;
	public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;
	public static final int NORTHEAST = GridBagConstraints.NORTHEAST;
	public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;
	public static final int NONE = GridBagConstraints.NONE;
	public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
	public static final int VERTICAL = GridBagConstraints.VERTICAL;
	public static final int BOTH = GridBagConstraints.BOTH;
	public static final int EAST = GridBagConstraints.EAST;
	public static final int SOUTH = GridBagConstraints.SOUTH;

	private String title;
	private int top, bottom, left, right;
	private int justification, position;

	public CnSPanel(String tit, int t, int l, int b, int r) {
		super();
		justification = TitledBorder.LEFT;
		position = TitledBorder.ABOVE_TOP;
		setLayout(new GridBagLayout());
		title = tit;
		top = t;
		left = l;
		bottom = b;
		right = r;
	}
	
	public CnSPanel() {
		this("", 0, 0, 0, 0);
	}
	public CnSPanel(String tit, int justification, int position) {
		this(tit, 20, 5, 5, 5);
		setTitleLocation(justification, position);
	}
	public CnSPanel(String tit) {
		this(tit, 20, 5, 5, 5);
	}
	public CnSPanel(int t, int l, int b, int r) {
		this("", t, l, b, r);
	}
	public Insets getInsets() {
		return new Insets(top, left, bottom, right);
	}
	public void setInsets(int t, int l, int b, int r) {
		top = t;
		left = l;
		bottom = b;
		right = r;  	
	}
	public void setTitleLocation(int justification, int position) {
		this.justification = justification;
		this.position = position;
	}
	public void addComponent(Component comp, int gridx, int gridy, int gridwidth, int gridheight,
                           double weightx, double weighty, int anchor, int fill, int insetstop, int insetsleft,
                           int insetsbottom, int insetsright, int ipadx, int ipady) {
		add(comp, new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill,
				new Insets(insetstop, insetsleft, insetsbottom, insetsright), ipadx, ipady));
	}
	public void initGraphics() {
		if (getTitle() != null)
			if (!getTitle().equals("") && !getTitle().equals("Clust&see")) 
				setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " " + getTitle() + " ", justification, position));
			else
				setBorder(BorderFactory.createEtchedBorder());
	}
	public String getTitle() {
		return title;
	}
}
