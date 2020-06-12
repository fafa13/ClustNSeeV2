package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.clustnsee3.internal.CnSClustnseePlugin;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;

public class CnSControlActionPanel extends CnSPanel {
	private static final long serialVersionUID = -7770743668639958943L;
	
	private CnSButton analyzeButton, closeButton;
	
	public CnSControlActionPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	protected void initGraphics() {
		super.initGraphics();
		analyzeButton = new CnSButton("Analyze");
		addComponent(analyzeButton, 0, 0, 1, 1, 0.0, 0.0, NORTHEAST, NONE, -10, 5, 5, 5, 0, 0);
		closeButton = new CnSButton("Close");
		addComponent(closeButton, 1, 0, 1, 1, 0.0, 0.0, NORTHEAST, NONE, -10, 0, 5, 5, 0, 0);
	}
	
	private void initListeners() {
		analyzeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev;
				
				/**
				 * Get the name of the selected algorithm
				 */
				ev = new CnSEvent(CnSAlgorithmManager.GET_SELECTED_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
				String algoName = (String)CnSEventManager.handleMessage(ev);
				
				/**
				 * Get the selected algorithm
				 */
				ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
				ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algoName);
				CnSAlgorithm algo = (CnSAlgorithm)CnSEventManager.handleMessage(ev);
				
				/**
				 * Run the algorithm
				 */
				ev = new CnSEvent(CnSAlgorithmEngine.START, CnSEventManager.ALGORITHM_ENGINE);
				ev.addParameter(CnSAlgorithmEngine.ALGORITHM, algo);
	    		CnSAlgorithmResult result = (CnSAlgorithmResult)CnSEventManager.handleMessage(ev);
	    		
	    		/**
	    		 * initialize result panel
	    		 */
	    		ev = new CnSEvent(CnSClustnseePlugin.GET_ADAPTER, CnSEventManager.CLUSTNSEE_PLUGIN);
	    		CyAppAdapter ad = (CyAppAdapter)CnSEventManager.handleMessage(ev);
	    		CyNetwork network = ad.getCyApplicationManager().getCurrentNetwork();
	    		ev = new CnSEvent(CnSResultsPanel.INIT, CnSEventManager.RESULTS_PANEL);
				ev.addParameter(CnSResultsPanel.RESULT, result);
				ev.addParameter(CnSResultsPanel.ALGO, algo);
				ev.addParameter(CnSResultsPanel.NETWORK, network);
	    		CnSEventManager.handleMessage(ev);
			}
		});
	}
}