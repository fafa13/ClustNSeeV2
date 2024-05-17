/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 mai 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusterannotationmatrix;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

import org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusteranalysis.CnSClusterTableModel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanelSplitCommand;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;

/**
 * 
 */
public class CnSClusterAnnotationMatrixPanel extends CnSPanelSplitCommand {
	private static final long serialVersionUID = 9190239075586246538L;
	
	private CnSClusterAnnotationMatrix matrix;
	private CnSClusterAnnotationMatrixModel matrixModel;
	private CnSButton exportDataButton;
	private JComboBox<String> statList;
	private JSpinner thresholdSpinner;
	private int currentHypergeometricThreshold = 5;
	private int currentMajorityThreshold = 50;
	
	public CnSClusterAnnotationMatrixPanel() {
		super();
		matrix = new CnSClusterAnnotationMatrix();
		matrixModel = new CnSClusterAnnotationMatrixModel();
		initGraphics();
		initListeners();
	}
	
	public void init(CnSPartition partition) {
		matrixModel = new CnSClusterAnnotationMatrixModel();
		matrixModel.init(partition);
		matrix.getTable().setDefaultRenderer(Double.class, new CnSAnnotationMatrixCellRenderer((Integer)thresholdSpinner.getValue()));
		matrix.getTable().setModel(matrixModel);
		matrix.getTable().fireTableDataChanged();
	}

	private void initListeners() {
		statList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				matrix.getTable().setDefaultRenderer(Double.class, new CnSAnnotationMatrixCellRenderer((Integer)thresholdSpinner.getValue()));
				matrix.getTable().fireTableDataChanged();
				if (statList.getSelectedIndex() == 0)
					thresholdSpinner.setValue(currentHypergeometricThreshold);
				else
					thresholdSpinner.setValue(currentMajorityThreshold);
				((CnSClusterAnnotationMatrixModel)matrix.getTable().getModel()).setStat(statList.getSelectedIndex());
			}
		});
		thresholdSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.err.println("thSpinner : " + thresholdSpinner.getValue());
				matrix.getTable().setDefaultRenderer(Double.class, new CnSAnnotationMatrixCellRenderer((Integer)thresholdSpinner.getValue()));
				matrix.getTable().fireTableDataChanged();
				if (statList.getSelectedIndex() == 0)
					currentHypergeometricThreshold = ((Integer)thresholdSpinner.getValue());
				else
					currentMajorityThreshold = ((Integer)thresholdSpinner.getValue());
			}
		});
	}
	
	public void initGraphics() {
		CnSPanel showPanel1 = new CnSPanel();
		showPanel1.addComponent(new JLabel("Show"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		statList = new JComboBox<String>();
		statList.addItem("Hypergeometric");
		statList.addItem("Majority");
		showPanel1.addComponent(statList, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		showPanel1.addComponent(new JLabel("enriched terms"), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		
		CnSPanel showPanel2 = new CnSPanel();
		showPanel2.addComponent(new JLabel("with a threshold of"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		thresholdSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
		showPanel2.addComponent(thresholdSpinner, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		showPanel2.addComponent(new JLabel("%."), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		
		CnSPanel showPanel = new CnSPanel();
		showPanel.addComponent(showPanel1, 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 0, 5, 0, 0);
		showPanel.addComponent(showPanel2, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		showPanel.setBorder(BorderFactory.createEtchedBorder());
		
		exportDataButton = new CnSButton("Export data");
		
		commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		commandPanel.addComponent(showPanel, 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 5, 5, 0, 5, 0, 0);
		commandPanel.addComponent(exportDataButton, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		
		matrix = new CnSClusterAnnotationMatrix();
		initGraphics(commandPanel, new JScrollPane(matrix.getTable()));
		
		//matrix.getTable().setTableHeader(new JTableHeader(matrix.getTable().getColumnModel()));
		matrix.getTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
	}
	
	public int getSelectedStat() {
		return statList.getSelectedIndex();
	}
	public String getSelectedStatName() {
		return statList.getSelectedItem().toString();
	}
	public int getCurrentThreshold() {
		return (Integer)thresholdSpinner.getValue();
	}
}
