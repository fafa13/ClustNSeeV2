/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSComparePartitionsDialog extends JDialog implements ActionListener, ItemListener {
	private static final long serialVersionUID = -6497149083805785043L;
	private JComboBox<CnSPartition> firstPartitionComboBox, secondPartitionComboBox;
	private CnSButton okButton, closeButton;
	private Vector<CnSPartition> allData = null, firstComboBoxData = null, secondComboBoxData = null;
	private boolean firstComboBoxListen, secondComboBoxListen;
	
	public CnSComparePartitionsDialog(Vector<CnSPartition> allData) {
		super();
		this.allData = allData;
		firstComboBoxListen = secondComboBoxListen = true;
		firstComboBoxData = new Vector<CnSPartition>();
		secondComboBoxData = new Vector<CnSPartition>();
		for (int i = 0; i < allData.size(); i++) {
			firstComboBoxData.addElement(allData.get(i));
			if (i > 0) secondComboBoxData.addElement(allData.get(i));
		}
		setModal(true);
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		setTitle("Compare partitions");
		CnSPanel mainPanel = new CnSPanel();
		CnSPanel firstPartitionPanel = new CnSPanel();
		CnSPanel secondPartitionPanel = new CnSPanel();
		JPanel buttonsPanel = new JPanel();
		firstPartitionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Select the first partition to compare :"));
		mainPanel.addComponent(firstPartitionPanel, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 5, 5, 0, 5, 30, 30);
		secondPartitionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Select the second partition to compare :"));
		mainPanel.addComponent(secondPartitionPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 10, 5, 0, 5, 30, 30);
		mainPanel.addComponent(buttonsPanel, 0, 2, 1, 1, 1.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
	
		firstPartitionComboBox = new JComboBox<CnSPartition>(firstComboBoxData);
		firstPartitionPanel.addComponent(firstPartitionComboBox, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		secondPartitionComboBox = new JComboBox<CnSPartition>(secondComboBoxData);
		secondPartitionPanel.addComponent(secondPartitionComboBox, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		
		okButton = new CnSButton("OK");
		closeButton = new CnSButton("Close");
		buttonsPanel.add(okButton);
		buttonsPanel.add(closeButton);
		getContentPane().add(mainPanel);
	}
	
	public void initListeners() {
		firstPartitionComboBox.addItemListener(this);
		secondPartitionComboBox.addItemListener(this);
		closeButton.addActionListener(this);
		okButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent ae) {
				CnSPartition p1 = firstPartitionComboBox.getItemAt(firstPartitionComboBox.getSelectedIndex());
				CnSPartition p2 = secondPartitionComboBox.getItemAt(secondPartitionComboBox.getSelectedIndex());
				Vector<CnSNode> v;
				Vector<Vector<Integer>> data = new Vector<Vector<Integer>>();
				for (CnSCluster c1 : p2.getClusters()) {
					data.addElement(new Vector<Integer>());
					for (CnSCluster c2 : p1.getClusters()) {
						v = (Vector<CnSNode>)c1.getNodes().clone();
						v.retainAll(c2.getNodes());
						data.get(data.size() - 1).addElement(v.size());
					}
				}
				dispose();
				CnSComparePartitionsResultDialog dialog = new CnSComparePartitionsResultDialog(p1, p2, data);
				dialog.pack();
				dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2, 
						(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
				dialog.setVisible(true);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource() == firstPartitionComboBox && firstComboBoxListen) {
				secondComboBoxListen = false;
				CnSPartition spart = secondPartitionComboBox.getItemAt(secondPartitionComboBox.getSelectedIndex());
				secondComboBoxData.clear();
				for (int i = 0; i < allData.size(); i++) 
					if (allData.get(i) != firstPartitionComboBox.getItemAt(firstPartitionComboBox.getSelectedIndex())) 
						secondComboBoxData.addElement(allData.get(i));
				if (secondComboBoxData.contains(spart)) 
					secondPartitionComboBox.setSelectedItem(spart);
				else
					secondPartitionComboBox.setSelectedIndex(0);
				secondPartitionComboBox.updateUI();
				secondPartitionComboBox.repaint();
			}
			else if (secondComboBoxListen) {
				firstComboBoxListen = false;
				CnSPartition spart = firstPartitionComboBox.getItemAt(firstPartitionComboBox.getSelectedIndex());
				firstComboBoxData.clear();
				for (int i = 0; i < allData.size(); i++) 
					if (allData.get(i) != secondPartitionComboBox.getItemAt(secondPartitionComboBox.getSelectedIndex())) 
						firstComboBoxData.addElement(allData.get(i));
				if (firstComboBoxData.contains(spart)) 
					firstPartitionComboBox.setSelectedItem(spart);
				else
					firstPartitionComboBox.setSelectedIndex(0);
				firstPartitionComboBox.updateUI();
				firstPartitionComboBox.repaint();
			}
			secondComboBoxListen = firstComboBoxListen = true;
		}
	}

}
