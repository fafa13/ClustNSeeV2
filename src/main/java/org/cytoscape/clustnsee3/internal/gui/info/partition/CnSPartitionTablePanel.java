/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionProperty;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSPartitionTablePanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -3877080938361953871L;
	private static CnSPartitionTablePanel instance;
	private CnSPartitionTable table;
	private CnSButton importAnnotationButton, addAnnotationColumnButton, exportDataButton;
	
	public static final int INIT = 1;
	public static final int CLEAR = 2;
	
	public static final int PARTITION = 1001;
	
	public static CnSPartitionTablePanel getInstance() {
		if (instance == null)
			instance = new CnSPartitionTablePanel("Partition table");
		return instance;
	}
	
	private CnSPartitionTablePanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	protected void initGraphics() {
		table = new CnSPartitionTable();
		addComponent(table.getScrollPane(), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 0, 0, 0, 0, 0, 0);
		table.getTable().getTableHeader().setDefaultRenderer(new CnSPartitionTableHeaderRenderer());
		CnSPanel commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		importAnnotationButton = new CnSButton("Import annotation");
		commandPanel.addComponent(importAnnotationButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		addAnnotationColumnButton = new CnSButton("Add annotation column");
		commandPanel.addComponent(addAnnotationColumnButton, 1, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 2, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(commandPanel, 0, 1, 1, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 0, 0, 0, 0, 0);
	}
	
	private void initListeners() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		switch (event.getAction()) {
			case INIT :
				CnSPartition partition = (CnSPartition)event.getParameter(PARTITION);
				CnSPartitionTableModel model = new CnSPartitionTableModel(partition);
				table.setModel(model);
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Nb. nodes"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Intra cluster edges"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Extra cluster edges"));
				model.addAnnotation(new CnSPartitionProperty<Double>(partition, "Intra/extra edges ratio"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Mono-clustered nodes"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Multi-clustered nodes"));
				
				TableColumnModel columnModel = table.getTable().getColumnModel();
			    TableColumn column = columnModel.getColumn(0);
		    	columnModel.removeColumn( column );
		    	table.getFixedTable().getColumnModel().addColumn( column );
		    	
		    	table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
				table.getScrollPane().setRowHeaderView(table.getFixedTable());
				table.getScrollPane().setCorner(JScrollPane.UPPER_LEFT_CORNER, table.getFixedTable().getTableHeader());
				table.getScrollPane().getRowHeader().addChangeListener(table);

				break;
			
			case CLEAR :
				table.getModel().clear();
				break;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getIcon()
	 */
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
