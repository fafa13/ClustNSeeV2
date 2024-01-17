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

package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.dialog.CnSComparePartitionsDialog;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSComparePartitionsMenu extends AbstractCyAction {
	private static final long serialVersionUID = 600642538506055931L;
	private static CnSComparePartitionsMenu instance;
	private boolean en;
	
	private CnSComparePartitionsMenu() {
		super("Compare partitions"); 
		setPreferredMenu("Apps.Clust&see");
		en = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITIONS, CnSEventManager.PARTITION_MANAGER, this.getClass());
		@SuppressWarnings("unchecked")
		Vector<CnSPartition> allData = (Vector<CnSPartition>)CnSEventManager.handleMessage(ev, true);
		if (allData != null) 
			if (allData.size() >= 2) {
				CnSComparePartitionsDialog dialog = new CnSComparePartitionsDialog(allData);
				dialog.pack();
				dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2, 
						(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
				dialog.setVisible(true);
			}
	}
	
	public static CnSComparePartitionsMenu getInstance() {
		if (instance == null) instance = new CnSComparePartitionsMenu();
		return instance;
	}
	
	public void setEnabled_(boolean b) {
		super.setEnabled(b);
		en = b;
	}
	public boolean isEnabled() {
		return en;
	}
}
