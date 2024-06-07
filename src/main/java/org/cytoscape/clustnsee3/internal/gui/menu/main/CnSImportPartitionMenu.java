/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 6 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.task.CnSImportPartitionTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * 
 */
public class CnSImportPartitionMenu extends AbstractCyAction {
	private static final long serialVersionUID = -5381959952948728885L;
	private static CnSImportPartitionMenu instance;
	private boolean en;
	
	private CnSImportPartitionMenu() {
		super("Import partition"); 						
		setPreferredMenu("Apps.Clust&See");
		en = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new FileNameExtensionFilter("Clust&See file", "cns"));
		int ret = jfc.showOpenDialog(null);
		boolean toload = false;
		File file = null;
		String algoName, networkName;
		CnSAlgorithm algo = null;
		CyNetwork network = null;
		String scope = null;
		Vector<Vector<Long>> imported_partition = null;
		Vector<Vector<String>> imported_annotation = null;
		
		if (ret == JFileChooser.APPROVE_OPTION) {
			toload = true;
			file = jfc.getSelectedFile();
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "The file you have selected doest not exist !", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
				toload = false;
			}	
		}
		if (toload) {
			CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
			CyNetwork currentNetwork = cam.getCurrentNetwork();
			
			if (currentNetwork == null) {
				JOptionPane.showMessageDialog(null, "No network selected", "No network", JOptionPane.ERROR_MESSAGE, null);
				return;
			}
			try {
				BufferedReader br= new BufferedReader(new FileReader(file));
				Set<CyNode> n;
				String s = br.readLine();
				if (s != null) {
					if (s.equals("#ClustnSee analysis export") || s.equals("#ModClust analysis export")) {
						imported_partition = new Vector<Vector<Long>>();
						imported_annotation = new Vector<Vector<String>>();
						
						while ((s = br.readLine()) != null) {
							if (s.startsWith("#")) {
								if (s.startsWith("#Algorithm:")) {
									algoName = s.substring(11);
									ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER, this.getClass());
									ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algoName);
									algo = (CnSAlgorithm)CnSEventManager.handleMessage(ev, true).getValue();
									if (algo == null) {
										JOptionPane.showMessageDialog(null, "Unknown algorithm : " + s, "Unknown algorithm", JOptionPane.ERROR_MESSAGE, null);
										break;
									}
								}
								else if (s.startsWith("#Network:")) {
									networkName = s.substring(9);
									if (!networkName.equals(currentNetwork.getRow(currentNetwork).get("name", String.class))) {
										JOptionPane.showMessageDialog(null, "The current network is not the one used to generate the partition", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
										break;
									}
									network = currentNetwork;
								}
								else if (s.startsWith("#Scope:")) {
									scope = s.substring(7);
									if (!scope.equals("Network") && !scope.equals("Selection")) {
										JOptionPane.showMessageDialog(null, "Unknown scope : " + s, "Unknown scope", JOptionPane.ERROR_MESSAGE, null);
										break;
									}
								}
								else if (s.startsWith("#Parameter:")) {
									String[] item = s.substring(11).split("=");
									if (item.length == 2) {
										ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER, this.getClass());
										ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algo.getName());
										ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, item[0]);
										ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, item[1]);
										ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, algo.getParameters().getParameterKey(item[0]));
										CnSEventManager.handleMessage(ev, true);
									}
									else {
										JOptionPane.showMessageDialog(null, "Unknown parameter : " + s, "Unknown parameter", JOptionPane.ERROR_MESSAGE, null);
										break;
									}
								}
							}
							else if (s.startsWith(">")) {
								imported_partition.addElement(new Vector<Long>());
								imported_annotation.addElement(new Vector<String>());
								String[] annot = s.split("\\|\\|");
								
								for (int i = 1; i < annot.length; i++) imported_annotation.lastElement().addElement(annot[i]);
							}
							else if (!s.equals("")) {
								ev = new CnSEvent(CnSNetworkManager.GET_NODES_WITH_VALUE, CnSEventManager.NETWORK_MANAGER, this.getClass());
								ev.addParameter(CnSNetworkManager.NETWORK, network);
								ev.addParameter(CnSNetworkManager.COLNAME, "shared name");
								ev.addParameter(CnSNetworkManager.VALUE, s);
								n = (Set<CyNode>)CnSEventManager.handleMessage(ev, true).getValue();
								
								if (n.size() == 1) {
									imported_partition.lastElement().addElement(n.iterator().next().getSUID());
								}
								else {
									JOptionPane.showMessageDialog(null, "Unknown node : " + s, "Unknown node", JOptionPane.ERROR_MESSAGE, null);
									break;
								}
							}
						}
						br.close();
					}
					else
						JOptionPane.showMessageDialog(null, "The file you have selected is not a Clustnsee export file", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
				}
				else
					JOptionPane.showMessageDialog(null, "The file you have selected is not a Clustnsee export file", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
				
				CnSImportPartitionTask task = new CnSImportPartitionTask(imported_partition, imported_annotation, algo, network, scope);
				ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
				DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev, true).getValue();
				TaskIterator ti = new TaskIterator();
				ti.append(task);
				dialogTaskManager.execute(ti);
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void setEnabled_(boolean b) {
		super.setEnabled(b);
		en = b;
	}
	
	public boolean isEnabled() {
		return en;
	}
	
	public boolean insertSeparatorAfter() {
		return true;
	}
	
	public static CnSImportPartitionMenu getInstance() {
		if (instance == null) instance = new CnSImportPartitionMenu();
		return instance;
	}
}
