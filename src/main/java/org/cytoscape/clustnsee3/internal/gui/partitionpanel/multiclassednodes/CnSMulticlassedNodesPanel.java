/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 31 oct. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.multiclassednodes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.util.ResourceBundle;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanelSplitCommand;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSMulticlassedNodesPanel extends CnSPanelSplitCommand {
	private static final long serialVersionUID = 9005702179972911401L;
	private CnSButton exportDataButton;
	private CnSMulticlassedNodesTable multiclassedNodesTable;
	
	public CnSMulticlassedNodesPanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void initGraphics( ) {
		exportDataButton = new CnSButton("Export data");
		//addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 5, 0, 0);
		
		multiclassedNodesTable = new CnSMulticlassedNodesTable();
		multiclassedNodesTable.setRowHeight(26);
		multiclassedNodesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		multiclassedNodesTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		multiclassedNodesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		multiclassedNodesTable.setTableHeader(new JTableHeader(multiclassedNodesTable.getColumnModel()) {
			private static final // TODO Auto-generated method stub
			long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
				ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, false);
				rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					case 1 : return rBundle.getString("CnSMulticlassedNodesTableModel.degree_MO");
					case 2 : return rBundle.getString("CnSMulticlassedNodesTableModel.betweeness_MO");
					case 3 : return rBundle.getString("CnSMulticlassedNodesTableModel.clusters_MO");
					case 4 : return rBundle.getString("CnSMulticlassedNodesTableModel.enrichedAnnotationPairs_MO");
				}
				return null;
			}
		});
		
		multiclassedNodesTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		commandPanel = new CnSPanel();
		commandPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		initGraphics(commandPanel, new JScrollPane(multiclassedNodesTable));
		//addComponent(new JScrollPane(multiclassedNodesTable), 0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, 0, 5, 0, 5, 0, 0);
	}
	public void initListeners() {
		
	}
	
	public void init(CnSPartition partition) {
		CnSMulticlassedNodesTableModel model = new CnSMulticlassedNodesTableModel(partition);
		multiclassedNodesTable.setModel(model);
		setColumnsWidth();
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		FontMetrics headerFontMetrics = multiclassedNodesTable.getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)multiclassedNodesTable.getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < multiclassedNodesTable.getModel().getColumnCount(); col++) {
			pWidth = headerFontMetrics.stringWidth(multiclassedNodesTable.getModel().getColumnName(col)) + multiclassedNodesTable.getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = multiclassedNodesTable.getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < multiclassedNodesTable.getModel().getRowCount(); row++)
				pWidth = Math.max(pWidth, multiclassedNodesTable.prepareRenderer(multiclassedNodesTable.getDefaultRenderer(multiclassedNodesTable.getModel().getColumnClass(col)), row, col).getPreferredSize().width + 10 + multiclassedNodesTable.getIntercellSpacing().width);
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		}
	}
}
