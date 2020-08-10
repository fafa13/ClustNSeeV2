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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmParameter;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * 
 */
public class CnSImportPartitionMenu extends AbstractCyAction {
	private static final long serialVersionUID = -5381959952948728885L;
	private static CnSImportPartitionMenu instance;
	
	private CnSImportPartitionMenu() {
		super("Import partition"); 						
		setPreferredMenu("Apps.Clust&see");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new FileNameExtensionFilter("Clust&See file", "cns"));
		int ret = jfc.showOpenDialog(null);
		boolean toload =false;
		String s;
		File file = null;
		CnSAlgorithm algo = null;
		CyNetwork network = null;
		
		if (ret == JFileChooser.APPROVE_OPTION) {
			toload = true;
			file = jfc.getSelectedFile();
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "The file you have selected doest not exist !", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
				toload = false;
			}	
		}
		if (toload) {
			try {
				System.err.println("Importing partition from " + file.getName());
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
    			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
    			CyNetwork currentNetwork = cam.getCurrentNetwork();
    			String algoName, networkName, scope;
    			BufferedReader br= new BufferedReader(new FileReader(file));
    			Set<CyNode> n;
    			s = br.readLine();
    			boolean ok = true;
    			if (s != null) {
    				if (s.equals("#ClustnSee analysis export")) {
    					while ((s = br.readLine()) != null) {
    						if (s.startsWith("#")) {
    							if (s.startsWith("#Algorithm:")) {
    								algoName = s.substring(11);
    								ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
    								ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algoName);
    								algo = (CnSAlgorithm)CnSEventManager.handleMessage(ev);
    								if (algo == null) {
    									JOptionPane.showMessageDialog(null, "Unknown algorithm : " + s, "Unknown algorithm", JOptionPane.ERROR_MESSAGE, null);
        								ok = false;
        								break;
    								}
    								System.err.println("  Algorithm name = " + algo.getName());
    							}
    							else if (s.startsWith("#Network:")) {
    								networkName = s.substring(9);
    								if (!networkName.equals(currentNetwork.getRow(currentNetwork).get("name", String.class))) {
    									JOptionPane.showMessageDialog(null, "The current network is not the one used to generate the partition", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
    									ok = false;
    									break;
    								}
    								network = currentNetwork;
    								System.err.println("  Network name = " + networkName);
    							}
    							else if (s.startsWith("#Scope:")) {
    								scope = s.substring(7);
    								if (!scope.equals("Network") && !scope.equals("Selection")) {
    									JOptionPane.showMessageDialog(null, "Unknown scope : " + s, "Unknown scope", JOptionPane.ERROR_MESSAGE, null);
    									ok = false;
    									break;
    								}
    								System.err.println("  Scope = " + scope);
    							}
    							else if (s.startsWith("#Parameter:")) {
    								String[] item = s.substring(11).split("=");
    								if (item.length == 2) {
    									ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER);
    									ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algo.getName());
    									ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, item[0]);
    									ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, item[1]);
    									ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, algo.getParameters().getParameterKey(item[0]));
    									CnSEventManager.handleMessage(ev);
    								}
    								else {
    									JOptionPane.showMessageDialog(null, "Unknown parameter : " + s, "Unknown parameter", JOptionPane.ERROR_MESSAGE, null);
    									ok = false;
    									break;
    								}
    							}
    						}
    						else if (s.startsWith(">")) {
    							System.err.println("  " + s);
    						}
    						else if (!s.equals("")) {
    							n = getNodesWithValue(network, network.getDefaultNodeTable(), "name", s);
    							if (n.size() == 1)
    								System.err.println("  " + n.iterator().next().getSUID() + " => " + s);
    							else {
    								JOptionPane.showMessageDialog(null, "Unknown node : " + s, "Unknown node", JOptionPane.ERROR_MESSAGE, null);
    								ok = false;
    								break;
    							}
    						}
    						else
    							System.err.println();
    					}
    					br.close();
    					for (Integer paramKey : algo.getParameters()) {
    						System.err.println("**Parameter : " + algo.getParameters().getParameter(paramKey).getName() + " = " + algo.getParameters().getParameter(paramKey).getValue());
    					}
    				}
    				else {
    					JOptionPane.showMessageDialog(null, "The file you have selected is not a Clustnsee export file", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
    					ok = false;
    				}
    			}
    			else {
					JOptionPane.showMessageDialog(null, "The file you have selected is not a Clustnsee export file", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
					ok = false;
    			}
				/*ev = new CnSEvent(CnSResultsPanel.ADD_PARTITION, CnSEventManager.RESULTS_PANEL);
    			ev.addParameter(CnSResultsPanel.RESULT, result);
    			ev.addParameter(CnSResultsPanel.ALGO, algo);
    			ev.addParameter(CnSResultsPanel.NETWORK, network);
    			CnSEventManager.handleMessage(ev);*/
			}
			catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public boolean insertSeparatorAfter() {
		return true;
	}
	
	public static CnSImportPartitionMenu getInstance() {
		if (instance == null) instance = new CnSImportPartitionMenu();
		return instance;
	}
	
	/**
     * Get all the nodes with a given attribute value.
     *
     * This method is effectively a wrapper around {@link CyTable#getMatchingRows}.
     * It converts the table's primary keys (assuming they are node SUIDs) back to
     * nodes in the network.
     *
     * Here is an example of using this method to find all nodes with a given name:
     *
     * {@code
     *   CyNetwork net = ...;
     *   String nodeNameToSearchFor = ...;
     *   Set<CyNode> nodes = getNodesWithValue(net, net.getDefaultNodeTable(), "name", nodeNameToSearchFor);
     *   // nodes now contains all CyNodes with the name specified by nodeNameToSearchFor
     * }
     * @param net The network that contains the nodes you are looking for.
     * @param table The node table that has the attribute value you are looking for;
     * the primary keys of this table <i>must</i> be SUIDs of nodes in {@code net}.
     * @param colname The name of the column with the attribute value
     * @param value The attribute value
     * @return A set of {@code CyNode}s with a matching value, or an empty set if no nodes match.
     */
    private static Set<CyNode> getNodesWithValue(final CyNetwork net, final CyTable table, final String colname, final Object value) {
        final Collection<CyRow> matchingRows = table.getMatchingRows(colname, value);
        final Set<CyNode> nodes = new HashSet<CyNode>();
        final String primaryKeyColname = table.getPrimaryKey().getName();
        for (final CyRow row : matchingRows) {
            final Long nodeId = row.get(primaryKeyColname, Long.class);
            if (nodeId == null) continue;
            final CyNode node = net.getNode(nodeId);
            if (node == null) continue;
            nodes.add(node);
        }
        return nodes;
    }
}
