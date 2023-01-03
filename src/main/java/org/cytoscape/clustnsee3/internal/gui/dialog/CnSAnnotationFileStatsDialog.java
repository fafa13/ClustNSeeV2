/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 5 janv. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;

/**
 * 
 */
public class CnSAnnotationFileStatsDialog extends JDialog {
	private static final long serialVersionUID = 8151633888723560613L;

	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	
	private CnSButton cancelButton, importButton;
	private int nodesInFile, annotationsInFile, foundNodes, exit_option;
	
	public CnSAnnotationFileStatsDialog(int nodesInFile, int annotationsInFile, int foundNodes) {
		super();
		setModal(true);
		this.nodesInFile = nodesInFile;
		this.annotationsInFile = annotationsInFile;
		this.foundNodes = foundNodes;
		exit_option = CANCEL_OPTION;
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		CnSPanel mainPanel = new CnSPanel();
		setContentPane(mainPanel);
		setTitle("Annotate network");
		
		CnSPanel p1 = new CnSPanel();
		p1.addComponent(new JLabel("The annotation file contains"), 0, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		JLabel nodesInFileLabel = new JLabel(String.valueOf(nodesInFile));
		nodesInFileLabel.setFont(nodesInFileLabel.getFont().deriveFont(Font.BOLD));
		p1.addComponent(nodesInFileLabel, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		p1.addComponent(new JLabel("nodes and"), 2, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		JLabel annotationsInFileLabel = new JLabel(String.valueOf(annotationsInFile));
		annotationsInFileLabel.setFont(annotationsInFileLabel.getFont().deriveFont(Font.BOLD));
		p1.addComponent(annotationsInFileLabel, 3, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		p1.addComponent(new JLabel("annotations."), 4, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		mainPanel.addComponent(p1, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		CnSPanel p2 = new CnSPanel();
		JLabel foundNodesLabel = new JLabel(String.valueOf(foundNodes));
		foundNodesLabel.setFont(foundNodesLabel.getFont().deriveFont(Font.BOLD));
		p2.addComponent(foundNodesLabel, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		p2.addComponent(new JLabel("nodes ("), 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		double foundNodesPercent = (double)((int)((double)foundNodes / (double)nodesInFile * 10000.0D)) / 100.0D;
		JLabel foundNodesPercentLabel = new JLabel(String.valueOf(foundNodesPercent) + "%");
		foundNodesPercentLabel.setFont(foundNodesPercentLabel.getFont().deriveFont(Font.BOLD));
		p2.addComponent(foundNodesPercentLabel, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 0, 0, 0);
		p2.addComponent(new JLabel(") were found in graph"), 3, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 5, 0, 0);
		mainPanel.addComponent(p2, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		CnSPanel p3 = new CnSPanel();
		JLabel notFoundNodesLabel = new JLabel(String.valueOf(nodesInFile - foundNodes));
		notFoundNodesLabel.setFont(notFoundNodesLabel.getFont().deriveFont(Font.BOLD));
		p3.addComponent(notFoundNodesLabel, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		p3.addComponent(new JLabel("nodes ("), 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		double notFoundNodesPercent = 100.0D - foundNodesPercent;
		JLabel notFoundNodesPercentLabel = new JLabel(String.valueOf(notFoundNodesPercent) + "%");
		notFoundNodesPercentLabel.setFont(notFoundNodesPercentLabel.getFont().deriveFont(Font.BOLD));
		p3.addComponent(notFoundNodesPercentLabel, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 0, 0, 0);
		p3.addComponent(new JLabel(") were not found in graph"), 3, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 5, 0, 0);
		mainPanel.addComponent(p3, 0, 2, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 0, 5, 0, 5, 0, 0);
		
		CnSPanel commandPanel = new CnSPanel();
		importButton = new CnSButton("Annotate");
		commandPanel.addComponent(importButton, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		cancelButton = new CnSButton("Cancel");
		commandPanel.addComponent(cancelButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		mainPanel.addComponent(commandPanel, 0, 3, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 10, 5, 5, 10, 0, 0);
		pack();
	}
	
	public void initListeners() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit_option = CANCEL_OPTION;
				dispose();
			}
		});
		
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit_option = OK_OPTION;
				dispose();
			}
		});
	}
	
	public int getExitOption() {
		return exit_option;
	}
	
	public void setExitOption(int eo) {
		exit_option = eo;
	}
}
