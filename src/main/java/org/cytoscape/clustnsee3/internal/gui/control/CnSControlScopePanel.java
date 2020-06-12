package org.cytoscape.clustnsee3.internal.gui.control;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlScopePanel extends CnSPanel {
	private static final long serialVersionUID = -904118259961128672L;
	
	private JRadioButton networkButton, selectionButton;
	private ButtonGroup bg;
	
	public CnSControlScopePanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	private void initListeners() {
		networkButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				CnSEvent ev = new CnSEvent(CnSAlgorithmEngine.SET_SCOPE, CnSEventManager.ALGORITHM_ENGINE);
				if (networkButton.isSelected())
					ev.addParameter(CnSAlgorithmEngine.SCOPE, "Network");
				else
					ev.addParameter(CnSAlgorithmEngine.SCOPE, "Selection");
				CnSEventManager.handleMessage(ev);
			}
		});
	}

	protected void initGraphics() {
		super.initGraphics();
		networkButton = new JRadioButton("Network");
		addComponent(networkButton, 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 5, 5, 5, 5, 0, 0);
		selectionButton = new JRadioButton("Selection");
		addComponent(selectionButton, 0, 1, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 5, 5, 5, 0, 0);		
		bg = new ButtonGroup();
		bg.add(networkButton);
		bg.add(selectionButton);
		
		networkButton.setSelected(true);
	}
}
