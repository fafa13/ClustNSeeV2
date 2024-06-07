/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 29 mai 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * 
 */
public class CnSThresholdTextField extends JTextField {
	private static final long serialVersionUID = -6310452098935180329L;
	/**
	 * @param
	 * @return
	 */
	public CnSThresholdTextField(String string) {
		super(string);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_ENTER) setBackground(Color.white);
			}
		});
	}
	
	public boolean isANumber() {
		String value = getText();
		try {
			Double.parseDouble(value);
			setBackground(Color.green);
			return true;
		}
		catch (NumberFormatException ex) {
			setBackground(Color.red);
			return false;
		}
	}
}
