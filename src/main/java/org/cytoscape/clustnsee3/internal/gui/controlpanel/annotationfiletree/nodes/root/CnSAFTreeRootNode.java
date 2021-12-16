/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.CnSAFTreeModel;
import org.cytoscape.clustnsee3.internal.gui.dialog.CnSLoadAnnotationFileDialog;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

public class CnSAFTreeRootNode extends CnSPanelTreeNode {
	public static final int TITLE = 1;
	
	private CnSAFTreeModel treeModel;
	
	public CnSAFTreeRootNode(Hashtable<Integer, Object> v) {
		super(null, v);
		panel = new CnSAFTreeRootNodePanel(getData(TITLE).toString());
		((CnSAFTreeRootNodePanel)panel).getAddButton().addActionListener(this);
	}
	
	public void setTreeModel(CnSAFTreeModel tm) {
		treeModel = tm;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof CnSButton) {
			if (((CnSButton)e.getSource()).getActionCommand().equals("add_file")) {
				System.out.println("Pressed: add annotation file");
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				CnSLoadAnnotationFileDialog dialog = CnSLoadAnnotationFileDialog.getInstance();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				dialog.setLocation((screenSize.width - dialog.getWidth()) / 2, (screenSize.height - dialog.getHeight()) / 2);
				dialog.setVisible(true);
				ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
					
				if (dialog.getExitOption() == CnSLoadAnnotationFileDialog.OK_OPTION) {
					if (! treeModel.contains(dialog.getSelectedFile())) {
						ev = new CnSEvent(CnSNodeAnnotationManager.LOAD_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
						ev.addParameter(CnSNodeAnnotationManager.FILE, dialog.getSelectedFile());
						ev.addParameter(CnSNodeAnnotationManager.FROM_LINE, dialog.getFromLine());
						CnSNodeAnnotationFile annotationFile = (CnSNodeAnnotationFile)CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL);
						if (partition != null) ev.addParameter(CnSPartitionPanel.PARTITION, partition);
						CnSEventManager.handleMessage(ev);
						treeModel.addAnnotationFile(this, annotationFile, annotationFile.getAllAnnotations().size(), annotationFile.getAllTargets().size());
						
						ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL);
						CnSEventManager.handleMessage(ev);
					}
					else {
						JOptionPane.showMessageDialog(null, "Annotation file " + dialog.getSelectedFile().getName() + " is already loaded.");
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(TITLE).toString();
	}
}
