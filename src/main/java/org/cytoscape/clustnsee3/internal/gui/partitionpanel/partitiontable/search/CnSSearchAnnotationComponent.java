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

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;

/**
 * 
 */
public class CnSSearchAnnotationComponent implements KeyListener, ActionListener, MouseListener {
	private CnSSearchAnnotationPopupWindow popupWindow;
	private JTextField textField;
	private Point popupPos = null;
	private Font font;
	  
	public CnSSearchAnnotationComponent() {
		super();
		font = new Font("monospaced", Font.PLAIN, 10);
		textField = new JTextField(20);
		textField.setFont(font);
		popupWindow = new CnSSearchAnnotationPopupWindow(this);
		
		initListeners();
	}
	
	private void initListeners() {
		textField.addKeyListener(this);
		textField.addActionListener(this);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent ev) {
			}
			@Override
			public void insertUpdate(DocumentEvent ev) {
				textField.setBackground(Color.WHITE);
			}
			@Override
			public void removeUpdate(DocumentEvent ev) {
				textField.setBackground(Color.WHITE);
			}
		});
	}
	
	public void storePopupWindowLocation() {
	    popupPos = popupWindow.getLocation();
	}
	
	public CnSSearchAnnotationPopupWindow getPopupWindow() {
		return popupWindow;
	}
	
	public Point getPopupPos() {
	    return popupPos;
	}
	
	public void initPopupWindowLocation() {
	    popupPos = new Point(textField.getLocationOnScreen().x, textField.getLocationOnScreen().y + textField.getSize().height + 2);
	}
	
	public String getText() {
	    return textField.getText();
	}
	
	public JTextField getTextField() {
	    return textField;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_DOWN) {
			popupWindow.down();
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP) {
			popupWindow.up();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_ESCAPE) 
			popupWindow.setVisible(false);
		else if (key.getKeyCode() != KeyEvent.VK_DOWN && key.getKeyCode() != KeyEvent.VK_UP && key.getKeyCode() != KeyEvent.VK_ENTER) {  
			String prefix = textField.getText();
			if (prefix.length() >= 3) {
				if (popupPos == null) {
					initPopupWindowLocation();
					popupWindow.setLocation(popupPos);
					popupWindow.pack();
				}
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.LOOK_FOR_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.PREFIX, prefix);
				Vector<CnSNodeAnnotation> data = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
				if (data.size() > 0) {
					popupWindow.init(data);
					popupWindow.setVisible(true);
					popupWindow.setFocusableWindowState(false);
					popupWindow.setAlwaysOnTop(true);
				}
				else
					popupWindow.setVisible(false);
			}
			else {
				popupWindow.setVisible(false);
				popupWindow.clearSelectedAnnotation();
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getComponent() != textField) {
			textField.setText(textField.getText() + e.getKeyChar());
			textField.dispatchEvent(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		CnSEvent ev;
		String word = popupWindow.getSelectedWord();
		if (word != null) {
			textField.setText(popupWindow.getSelectedAnnotation().getValue());
			popupWindow.setVisible(false);
			ev = new CnSEvent(CnSPartitionPanel.SEARCH, CnSEventManager.ANNOTATION_PANEL);
			ev.addParameter(CnSPartitionPanel.ANNOTATION, popupWindow.getSelectedAnnotation());
			CnSEventManager.handleMessage(ev);
			textField.setBackground(Color.GREEN);
		}
		else {
			word = textField.getText();
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATION, CnSEventManager.ANNOTATION_MANAGER);
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, word);
			CnSNodeAnnotation ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev);
			if (ann != null)
				textField.setBackground(Color.GREEN);
			else if (!word.equals(""))
				textField.setBackground(Color.RED);
			else
				textField.setBackground(Color.WHITE);
			ev = new CnSEvent(CnSPartitionPanel.SEARCH, CnSEventManager.ANNOTATION_PANEL);
			if (ann != null) ev.addParameter(CnSPartitionPanel.ANNOTATION, ann);
			CnSEventManager.handleMessage(ev);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			String word = popupWindow.getSelectedWord();
			if (word != null) {
				textField.setText(word);
				popupWindow.setVisible(false);
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATION, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, word);
				CnSNodeAnnotation ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev);
				if (ann != null)
					textField.setBackground(Color.GREEN);
				else if (!word.equals(""))
					textField.setBackground(Color.RED);
				else
					textField.setBackground(Color.WHITE);
				ev = new CnSEvent(CnSPartitionPanel.SEARCH, CnSEventManager.ANNOTATION_PANEL);
				if (ann != null) ev.addParameter(CnSPartitionPanel.ANNOTATION, ann);
				CnSEventManager.handleMessage(ev);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
