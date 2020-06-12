/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.results;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

//import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.data.CnSpartitionDetailsPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSClusterListPanel extends CnSPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table = null;
	private CnSResultsTableModel model = null;
	
	public void init (Vector<CnSCluster> clusters) {
		model = new CnSResultsTableModel(clusters.size());
		Iterator<CnSCluster> itClusters = clusters.iterator();
		while (itClusters.hasNext()) model.addCluster(itClusters.next());

		//JOptionPane.showMessageDialog(null, "nb class = " + clusters.size());
		table = new JTable(model);
		table.setRowHeight(116);
		table.setBackground(Color.white);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new CnSResultsTableRenderer());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					if (table.getSelectionModel().getMaxSelectionIndex() != -1) {
						CnSCluster cluster = (CnSCluster)model.getValueAt(table.getSelectionModel().getMaxSelectionIndex(), 1);
						CnSEvent ev = new CnSEvent(CnSpartitionDetailsPanel.INIT, CnSEventManager.DATA_PANEL);
						ev.addParameter(CnSpartitionDetailsPanel.CLUSTER, cluster);
						CnSEventManager.handleMessage(ev);
					}
			}
		});
		addComponent(new JScrollPane(table), 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 10, 10, 10, 10, 0, 0);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSCluster getSelectedCluster() {
		return model.getCluster(table.getSelectedRow());
	}
}
