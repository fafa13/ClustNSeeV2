package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;

public class CnSControlScopePanel extends CnSPanel {
	private static final long serialVersionUID = -904118259961128672L;
	
	private JRadioButton networkButton, selectionButton;
	private ButtonGroup bg;
	
	public CnSControlScopePanel(String title) {
		super(title);
		setTitleLocation(TitledBorder.LEFT, TitledBorder.BELOW_TOP);
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

	public void initGraphics() {
		super.initGraphics();
		networkButton = new JRadioButton("Network");
		addComponent(networkButton, 0, 0, 1, 1, 1.0, 0.0, WEST, BOTH, 5, 20, 5, 5, 0, 0);
		selectionButton = new JRadioButton("Selection");
		addComponent(selectionButton, 1, 0, 1, 1, 1.0, 0.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);		
		bg = new ButtonGroup();
		bg.add(networkButton);
		bg.add(selectionButton);
		
		networkButton.setSelected(true);
	}
}
