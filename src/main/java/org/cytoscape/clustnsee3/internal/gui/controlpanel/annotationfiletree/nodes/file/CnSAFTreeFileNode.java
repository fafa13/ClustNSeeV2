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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.dialog.CnSAnnotationFileStatsDialog;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.task.CnSAnnotateGraphTask;
import org.cytoscape.clustnsee3.internal.task.CnSDeleteAnnotationFileTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class CnSAFTreeFileNode extends CnSPanelTreeNode {
	public final static int ANNOTATION_FILE = 1;
	public final static int NB_ANNOTATIONS = 2;
	public final static int NB_NODES = 3;
	
	public CnSAFTreeFileNode(CnSAFTreeRootNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeFileNodePanel((CnSNodeAnnotationFile)getData(ANNOTATION_FILE));
		((CnSAFTreeFileNodePanel)panel).getDeleteButton().addActionListener(this);
		((CnSAFTreeFileNodePanel)panel).getAnnotateButton().addActionListener(this);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		CnSNodeAnnotationFile af = null;
		if (e.getSource() instanceof CnSButton) {
			af = (CnSNodeAnnotationFile)getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			if (((CnSButton)e.getSource()).getActionCommand().equals("delete")) {
				System.out.println("Pressed: delete " + af);
				System.out.println("Child count = " + getChildCount());
				
				CnSEvent ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
				DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev);
				TaskIterator ti = new TaskIterator();
				CnSPanelTreeNode rootNode = (CnSPanelTreeNode)((CnSAFTreeDetailsNodePanel)getChildAt(0).getPanel()).getNetworksTree().getModel().getRoot();
				
				CnSDeleteAnnotationFileTask task = new CnSDeleteAnnotationFileTask(af, rootNode, this);
				ti.append(task);
				dialogTaskManager.execute(ti);
				
				/*for (int i = 0; i < getChildCount(); i++) {
					CnSPanelTreeNode rootNode = (CnSPanelTreeNode)((CnSAFTreeDetailsNodePanel)getChildAt(i).getPanel()).getNetworksTree().getModel().getRoot();
					for (int k = 0;  k < rootNode.getChildCount(); k++) {
						CyNetwork nw = (CyNetwork)rootNode.getChildAt(k).getData(CnSAFTreeNetworkNetnameNode.NETWORK);
						CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
						ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, af);
						ev.addParameter(CnSNodeAnnotationManager.NETWORK, nw);
						CnSEventManager.handleMessage(ev);
						
						System.err.println("------ " + nw);
					}
				}
				System.err.println("OK1");
				
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.UNLOAD_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
				CnSEventManager.handleMessage(ev);
				System.err.println("OK2");
				
				ev = new CnSEvent(CnSNodeAnnotationManager.REFRESH_CLUSTER_HASMAP, CnSEventManager.ANNOTATION_MANAGER);
				CnSEventManager.handleMessage(ev);
				System.err.println("OK3");
				
				ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL);
				CnSEventManager.handleMessage(ev);
				System.err.println("OK4");
				
				ev = new CnSEvent(CnSControlPanel.REMOVE_ANNOTATION_FILE, CnSEventManager.CONTROL_PANEL);
				ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
				CnSEventManager.handleMessage(ev);
				System.err.println("OK5");
				
				
				
				ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL);
				CnSEventManager.handleMessage(ev);
				System.err.println("OK6");*/
				
			}
			else if (((CnSButton)e.getSource()).getActionCommand().equals("annotate")) {
				System.out.println("Pressed: annotate " + af);
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				CyNetwork network = cam.getCurrentNetwork();
				
				if (network != null) {
					ev = new CnSEvent(CnSNetworkManager.GET_NETWORK, CnSEventManager.NETWORK_MANAGER);
					ev.addParameter(CnSNetworkManager.NETWORK, network);
					CnSNetwork cn = (CnSNetwork)CnSEventManager.handleMessage(ev);
					if (cn != null) network = cn.getBaseNetwork();
					
					ev = new CnSEvent(CnSNodeAnnotationManager.IS_NETWORK_ANNOTATED, CnSEventManager.ANNOTATION_MANAGER);
					ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
					ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
					if (!(Boolean)CnSEventManager.handleMessage(ev)) {
						ev = new CnSEvent(CnSNodeAnnotationManager.PARSE_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
						ev.addParameter(CnSNodeAnnotationManager.FILE, af.getFile());
						ev.addParameter(CnSNodeAnnotationManager.FROM_LINE, af.getFromLine());
						ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
						int[] results = (int[])CnSEventManager.handleMessage(ev);
						
						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				
						CnSAnnotationFileStatsDialog statsDialog = new CnSAnnotationFileStatsDialog(results[0], results[1], results[2]);
						statsDialog.setLocation((screenSize.width - statsDialog.getWidth()) / 2, (screenSize.height - statsDialog.getHeight()) / 2);
						statsDialog.setVisible(true);
						if (statsDialog.getExitOption() == CnSAnnotationFileStatsDialog.OK_OPTION) {
							ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
							DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev);
							TaskIterator ti = new TaskIterator();
							CnSAnnotateGraphTask task = new CnSAnnotateGraphTask(af, network, results, this);
							ti.append(task);
							dialogTaskManager.execute(ti);
						}
					}
					else
						JOptionPane.showMessageDialog(null, "The network is already annotated with this annotation file !");
				}
				else
					JOptionPane.showMessageDialog(null, "There is no network to map annotations !");
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(ANNOTATION_FILE);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	@Override
	public int getIndex(TreeNode node) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}
}
