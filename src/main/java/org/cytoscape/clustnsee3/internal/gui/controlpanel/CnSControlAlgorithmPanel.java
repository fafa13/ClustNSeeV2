package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlAlgorithmPanel extends CnSPanel implements ChangeListener {
	private static final long serialVersionUID = 6943247192606993605L;
	
	private JRadioButton FTButton, TFitButton, OCGButton;
	private ButtonGroup bg;
	private CnSControlAlgorithmTfitOptionsPanel tfitOptionsPanel;
	private CnSControlAlgorithmOCGPanel ocgOptionsPanel;
	
	public CnSControlAlgorithmPanel(String title) {
		super(title);
		setTitleLocation(TitledBorder.LEFT, TitledBorder.BELOW_TOP);
		initGraphics();
		initListeners();
	}
	public void initGraphics() {
		super.initGraphics();
		FTButton = new JRadioButton("FT");
		addComponent(FTButton, 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 5, 20, 5, 5, 0, 0);
		TFitButton = new JRadioButton("TFit");
		addComponent(TFitButton, 0, 1, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 20, 5, 5, 0, 0);		
		OCGButton = new JRadioButton("OCG");
		addComponent(OCGButton, 0, 3, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 20, 5, 5, 0, 0);		
		
		tfitOptionsPanel = new CnSControlAlgorithmTfitOptionsPanel("");
		ocgOptionsPanel = new CnSControlAlgorithmOCGPanel("");
		bg = new ButtonGroup();
		bg.add(FTButton);
		bg.add(TFitButton);
		bg.add(OCGButton);
		FTButton.setSelected(true);
	}
	
	private void initListeners() {
		FTButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if (((JRadioButton)e.getSource()).isSelected()) {
					CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_SELECTED_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
					ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "FT");
					CnSEventManager.handleMessage(ev);
				}
			}
			
		});
		TFitButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton)e.getSource()).isSelected()) {
					addComponent(tfitOptionsPanel, 0, 2, 1, 1, 1.0, 1.0, NORTHWEST, HORIZONTAL, 0, 20, 10, 5, 0, 0);
					CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_SELECTED_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
					ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "TFit");
					CnSEventManager.handleMessage(ev);
				}
				else {
					remove(tfitOptionsPanel);
				}
			}
		});
		TFitButton.addChangeListener(this);
		OCGButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton)e.getSource()).isSelected()) {
					addComponent(ocgOptionsPanel, 0, 4, 1, 1, 1.0, 1.0, NORTHWEST, HORIZONTAL, 0, 20, 10, 5, 0, 0);
					CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_SELECTED_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
					ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "OCG");
					CnSEventManager.handleMessage(ev);
				}
				else {
					remove(ocgOptionsPanel);
				}
			}
		});
		OCGButton.addChangeListener(this);
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		update(this.getGraphics());
		revalidate();
		repaint();
	}
}
