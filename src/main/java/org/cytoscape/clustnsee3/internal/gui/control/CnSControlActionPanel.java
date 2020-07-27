package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.write.ExportVizmapTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
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
				CnSEvent ev = new CnSEvent(CyActivator.GET_EXPORT_STYLE_FACTORY, CnSEventManager.CY_ACTIVATOR);
				ExportVizmapTaskFactory evtf = (ExportVizmapTaskFactory)CnSEventManager.handleMessage(ev);
				TaskIterator ti = evtf.createTaskIterator(new File("/home/fafa/Documents/cns.style"));
				ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
				TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev);
				tm.execute(ti);
				
				ev = new CnSEvent(CyActivator.GET_VISUAL_STYLE_FACTORY, CnSEventManager.CY_ACTIVATOR);
				VisualStyle vs = ((VisualStyleFactory)CnSEventManager.handleMessage(ev)).createVisualStyle("Clust'n'see");
				
				//Use pass-through mapping
				String ctrAttrName1 = "SUID";
				ev = new CnSEvent(CyActivator.GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY, CnSEventManager.CY_ACTIVATOR);
				
				PassthroughMapping<String, ?> pMapping = (PassthroughMapping<String, ?>) ((VisualMappingFunctionFactory)CnSEventManager.handleMessage(ev)).createVisualMappingFunction(ctrAttrName1, String.class, BasicVisualLexicon.NODE_LABEL);
				vs.addVisualMappingFunction(pMapping);                        
				
				// Add the new style to the VisualMappingManager
				ev = new CnSEvent(CyActivator.GET_VISUAL_MAPPING_MANAGER, CnSEventManager.CY_ACTIVATOR);
				((VisualMappingManager)CnSEventManager.handleMessage(ev)).addVisualStyle(vs);
				  
				// Apply the visual style to a NetwokView
				ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
				CyNetworkView v = ((CnSView)CnSEventManager.handleMessage(ev)).getView();
				vs.apply(v);
				v.updateView();
			}
		});
	}
}