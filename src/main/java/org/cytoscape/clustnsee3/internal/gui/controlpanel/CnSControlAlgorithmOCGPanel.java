package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSOCGAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlAlgorithmOCGPanel extends CnSPanel implements ChangeListener {
	private static final long serialVersionUID = -1974071277000782747L;

	private JRadioButton defaultButton, expertButton;
	private ButtonGroup bg;
	private CnSControlAlgorithmOCGExpertOptionsPanel expertOptionsPanel;
	
	public CnSControlAlgorithmOCGPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		super.initGraphics();
		defaultButton = new JRadioButton("Default options");
		addComponent(defaultButton, 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, -10, 5, 5, 5, 0, 0);
		expertButton = new JRadioButton("Expert options");
		addComponent(expertButton, 0, 1, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		bg = new ButtonGroup();
		bg.add(defaultButton);
		bg.add(expertButton);
		
		expertOptionsPanel = new CnSControlAlgorithmOCGExpertOptionsPanel(null);
		defaultButton.setSelected(true);
	}
	
	private void initListeners() {
		expertButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER);
				ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "OCG");
				ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, "Options");
				ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, CnSOCGAlgorithm.OPTIONS);
				if (((JRadioButton)e.getSource()).isSelected()) {
					addComponent(expertOptionsPanel, 0, 2, 1, 1, 1.0, 1.0, NORTHWEST, HORIZONTAL, 0, 20, 10, 5, 0, 0);
					ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, "Expert options");
				}
				else {
					remove(expertOptionsPanel);
					ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, "Default options");
				}
				CnSEventManager.handleMessage(ev);
			}
		});
		expertButton.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update(this.getGraphics());
		revalidate();
		repaint();
	}
}
