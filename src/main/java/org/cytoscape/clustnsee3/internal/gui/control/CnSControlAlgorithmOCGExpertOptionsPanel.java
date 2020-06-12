package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSOCGAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlAlgorithmOCGExpertOptionsPanel extends CnSPanel {
	private static final long serialVersionUID = -8268590516364506764L;
	private JComboBox<String> initialClustersComboBox, classSystemComboBox;
	private Vector<String> initialClusters, classSystem;
	private JTextField maxCardinalTextField, minNumberTextField;
	
	public CnSControlAlgorithmOCGExpertOptionsPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}

	protected void initGraphics() {
		super.initGraphics();
		addComponent(new JLabel("Initial clusters"), 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, -10, 5, 5, 5, 0, 0);
		initialClusters = new Vector<String>();
		initialClusters.addElement("Centered cliques");
		initialClusters.addElement("Maximal cliques");
		initialClusters.addElement("Edges");
		initialClustersComboBox = new JComboBox<String>(initialClusters);
		addComponent(initialClustersComboBox, 0, 1, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		addComponent(new JLabel("Class system"), 0, 2, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 10, 5, 5, 5, 0, 0);
		classSystem = new Vector<String>();
		classSystem.addElement("Maximize modularity");
		classSystem.addElement("Final class");
		classSystemComboBox = new JComboBox<String>(classSystem);
		addComponent(classSystemComboBox, 0, 3, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		addComponent(new JLabel("Cluster max. cardinal"), 0, 4, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 10, 5, 5, 5, 0, 0);
		maxCardinalTextField = new JTextField("0");
		maxCardinalTextField.setColumns(4);
		addComponent(maxCardinalTextField, 0, 5, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 5, 5, 5, 0, 0);
		addComponent(new JLabel("Cluster min. number"), 0, 6, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 10, 5, 5, 5, 0, 0);
		minNumberTextField = new JTextField("2");
		minNumberTextField.setColumns(4);
		addComponent(minNumberTextField, 0, 7, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 5, -10, 5, 0, 0);
	}
	
	private void initListeners( ) {
		initialClustersComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER);
				
					ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "OCG");
					ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, "Initial clusters");
					ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, CnSOCGAlgorithm.INITIAL_CLUSTERS);
					ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, initialClustersComboBox.getSelectedItem());
					CnSEventManager.handleMessage(ev);
				}
			}
		});
	}
}
