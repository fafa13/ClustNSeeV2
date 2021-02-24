/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 13 janv. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSSearchAnnotationPopupWindow extends JDialog implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -7159351837305533272L;

	private JList<CnSNodeAnnotation> keywordList;
	private JScrollPane scrollPane;
	private int xm, ym;
	private CnSPanel mainPanel;
	
	public CnSSearchAnnotationPopupWindow(CnSSearchAnnotationComponent parent) {
		super();
		initGraphics();
		initListeners(parent);
	}
	
	public CnSNodeAnnotation getSelectedAnnotation() {
	    return keywordList.getSelectedValue();
	}
	public void clearSelectedAnnotation() {
		keywordList.clearSelection();
	}
	public String getSelectedWord() {
		if (getSelectedAnnotation() != null)
			return getSelectedAnnotation().getValue();
		return null;
	}
	
	private void initGraphics() {
		mainPanel = new CnSPanel();
	    mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(Color.black), BorderFactory.createEmptyBorder(0, 0, 0, 0)), mainPanel.getBorder()));
	    keywordList = new JList<CnSNodeAnnotation>();
	    keywordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
	    scrollPane = new JScrollPane(keywordList);
	    mainPanel.addComponent(scrollPane, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.BOTH, 0, 0, 0, 10, 0, 0);
	    
	    getContentPane().setLayout(new BorderLayout());
	    getContentPane().add(mainPanel, BorderLayout.CENTER);
	    setUndecorated(true);
	    setResizable(true);
	    
	}
	
	private void initListeners(CnSSearchAnnotationComponent parent) {
		addMouseListener(this);
	    addMouseMotionListener(this);
	    keywordList.addMouseListener(parent);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		xm = arg0.getX();
	    ym = arg0.getY();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		//searchComponent.storePopupWindowLocation();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - xm;
	    int dy = e.getY() - ym;
	    int w = mainPanel.getSize().width;
	    int h = mainPanel.getSize().height;

	    if (getCursor().getType() == Cursor.DEFAULT_CURSOR) {
	    	if ((xm != -1) && (ym != -1)) {
	    		Point p = this.getLocation();
	    		p.setLocation(p.getX() + dx, p.getY() + dy);
	    		this.setLocation(p);
	    	}
	    }
	    else if (getCursor().getType() == Cursor.E_RESIZE_CURSOR) {
	    	this.setSize(w + dx, h);
	    	xm = e.getX();
	    }
	    else if (getCursor().getType() == Cursor.S_RESIZE_CURSOR) {
	    	this.setSize(w, h + dy);
	    	ym = e.getY();
	    }
	    if (getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
	    	this.setSize(w + dx, h + dy);
	    	xm = e.getX();
	    	ym = e.getY();
	    }
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		int dx = mainPanel.getSize().width - e.getX();
	    int dy = mainPanel.getSize().height - e.getY();
	    if ((dx < 5) && (dy < 5))
	    	setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
	    else if (dx < 5)
	    	setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	    else if (dy < 5)
	    	setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
	    else
	    	setCursor(Cursor.getDefaultCursor());
	}
	
	public void init(Vector<CnSNodeAnnotation> v) {
		keywordList.setListData(v);
	}
	
	public void down() {
		keywordList.setSelectedIndex(keywordList.getSelectedIndex() + 1);
		keywordList.ensureIndexIsVisible(keywordList.getSelectedIndex());
	}
	public void up() {
		int index = keywordList.getSelectedIndex() - 1;
		if (index >= 0) {
			keywordList.setSelectedIndex(index);
			keywordList.ensureIndexIsVisible(index);
		}
	}
}
