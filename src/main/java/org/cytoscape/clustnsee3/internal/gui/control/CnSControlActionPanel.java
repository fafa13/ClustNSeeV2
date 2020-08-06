package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
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
	    		if (result != null) {
	    			ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
	    			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
	    			CyNetwork network = cam.getCurrentNetwork();
	    		
	    			ev = new CnSEvent(CnSResultsPanel.ADD_PARTITION, CnSEventManager.RESULTS_PANEL);
	    			ev.addParameter(CnSResultsPanel.RESULT, result);
	    			ev.addParameter(CnSResultsPanel.ALGO, algo);
	    			ev.addParameter(CnSResultsPanel.NETWORK, network);
	    			CnSEventManager.handleMessage(ev);
	    		}
	    		else
	    			JOptionPane.showMessageDialog(null, "You must select a network !");
			}
		});
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSStyleManager.REMOVE_CNS_STYLES, CnSEventManager.STYLE_MANAGER);
				CnSEventManager.handleMessage(ev);
				ev = new CnSEvent(CyActivator.STOP, CnSEventManager.CY_ACTIVATOR);
				CnSEventManager.handleMessage(ev);
				
			}
		});
	}
}