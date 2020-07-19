/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 18 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.annotation.CnSClusterAnnotation;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSAnnotateClusterDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 4964423878036460743L;
	private JTextField annotationTextField;
	private CnSButton addButton;
	private JTable annotationTable;
	private Vector<Vector<String>> data;
	private CnSButton upButton, downButton, removeButton, closeButton;
	
	public CnSAnnotateClusterDialog(CnSCluster cluster) {
		super();
		setModal(true);
		data = new Vector<Vector<String>>();
		initGraphics(cluster);
		initListeners(cluster);
	}
	
	private void initGraphics(CnSCluster cluster) {
		setTitle("Annotate cluster " + cluster.getName());
		CnSPanel panel = new CnSPanel();
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		CnSPanel newAnnotationPanel = new CnSPanel();
		newAnnotationPanel.addComponent(new JLabel("New annotation :"), 0, 0, 2, 1, 1.0, 1.0, CnSPanel.NORTHWEST, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		annotationTextField = new JTextField(30);
		newAnnotationPanel.addComponent(annotationTextField, 0, 1, 1, 1, 1.0, 0.0, CnSPanel.NORTHWEST, CnSPanel.HORIZONTAL, 5, 5, 0, 0, 0, 0);
		addButton = new CnSButton("Add");
		newAnnotationPanel.addComponent(addButton, 1, 1, 1, 1, 0.0, 1.0, CnSPanel.NORTHEAST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		panel.addComponent(newAnnotationPanel, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTHWEST, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		panel.addComponent(Box.createVerticalStrut(16), 0, 1, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 0, 0, 0, 0, 0, 0);
		
		CnSPanel annotationListPanel = new CnSPanel();
		annotationListPanel.addComponent(new JLabel("Cluster annotation list :"), 0, 0, 2, 1, 1.0, 0.0, CnSPanel.NORTHWEST, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Annotation");
		Vector<String> v;
		for (int i = 0 ; i < cluster.getAnnotations().size(); i++) {
			v = new Vector<String>();
			v.addElement(cluster.getAnnotations().get(i).getAnnotation());
			data.addElement(v);
		}
		annotationTable = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(annotationTable);
		scrollPane.setPreferredSize(new Dimension(300, 150));
		annotationListPanel.addComponent(scrollPane, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTHWEST, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		
		CnSPanel annotationListButtonsPanel = new CnSPanel();
		upButton = new CnSButton("Up");
		annotationListButtonsPanel.addComponent(upButton, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.NONE, 0, 0, 0, 0, 0, 0);
		downButton = new CnSButton("Down");
		annotationListButtonsPanel.addComponent(downButton, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.NONE, 0, 0, 0, 0, 0, 0);
		removeButton = new CnSButton("Remove");
		annotationListButtonsPanel.addComponent(removeButton, 0, 2, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.NONE, 0, 0, 0, 0, 0, 0);
		
		annotationListPanel.addComponent(annotationListButtonsPanel, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		panel.addComponent(annotationListPanel, 0, 2, 1, 1, 1.0, 1.0, CnSPanel.NORTHEAST, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		
		closeButton = new CnSButton("Close");
		
		panel.addComponent(closeButton, 0, 3, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		getContentPane().add(panel);
	}
	
	private void initListeners(CnSCluster cluster) {
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!annotationTextField.getText().equals("")) {
					cluster.addAnnotation(new CnSClusterAnnotation(annotationTextField.getText()));
					Vector<String> v  = new Vector<String>();
					v.addElement(annotationTextField.getText());
					data.addElement(v);
					annotationTextField.setText("");
					annotationTable.updateUI();
					annotationTable.repaint();
				}
			}
		});
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		closeButton.addActionListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}
}
