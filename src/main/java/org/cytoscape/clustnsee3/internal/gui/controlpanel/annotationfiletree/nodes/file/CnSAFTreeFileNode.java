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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAFTreeFileNode extends CnSPanelTreeNode {
	public final static int ANNOTATION_FILE = 1;
	public final static int NB_ANNOTATIONS = 2;
	public final static int NB_NODES = 3;
	public final static int NETWORKS = 4;
	
	public CnSAFTreeFileNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeFileNodePanel((CnSNodeAnnotationFile)v.get(ANNOTATION_FILE));
		panel.initGraphics();
		((CnSAFTreeFileNodePanel)panel).getDeleteButton().addActionListener(this);
		((CnSAFTreeFileNodePanel)panel).getAnnotateButton().addActionListener(this);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String val;
		if (e.getSource() instanceof CnSButton)
			if (((CnSButton)e.getSource()).getActionCommand().equals("delete")) {
				val = getData(CnSAFTreeFileNode.ANNOTATION_FILE).toString();
				System.out.println("Pressed: delete " + val);
			}
			else if (((CnSButton)e.getSource()).getActionCommand().equals("annotate")) {
				val = getData(CnSAFTreeFileNode.ANNOTATION_FILE).toString();
				System.out.println("Pressed: annotate " + val);
			}
	}
}
