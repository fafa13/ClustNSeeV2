package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSTFitAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlAlgorithmTfitOptionsPanel extends CnSPanel {
	private static final long serialVersionUID = -5755723765170248942L;
	
	private JTextField alphaTextField;
	private float alpha = 1.0f;
	
	public CnSControlAlgorithmTfitOptionsPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		super.initGraphics();
		alphaTextField = new JTextField(String.valueOf(alpha));
		alphaTextField.setColumns(10);
		addComponent(new JLabel("Alpha"), 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, -10, 5, 5, 5, 0, 0);
		addComponent(alphaTextField, 1, 0, 1, 1, 1.0, 0.0, NORTHEAST, NONE, -10, 0, 5, 5, 0, 0);
	}
	private void initListeners() {
		/*alphaTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER);
				try {
					ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "TFit");
					ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, "Alpha");
					ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, CnSTFitAlgorithm.ALPHA);
					ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, Float.valueOf(alphaTextField.getText()));
					CnSEventManager.handleMessage(ev);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});*/
		alphaTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!e.isActionKey()) {
					CnSEvent ev = new CnSEvent(CnSAlgorithmManager.SET_ALGORITHM_PARAMETER, CnSEventManager.ALGORITHM_MANAGER);
					try {
						ev.addParameter(CnSAlgorithmManager.ALGO_NAME, "TFit");
						ev.addParameter(CnSAlgorithmManager.PARAMETER_NAME, "Alpha");
						ev.addParameter(CnSAlgorithmManager.PARAMETER_KEY, CnSTFitAlgorithm.ALPHA);
						ev.addParameter(CnSAlgorithmManager.PARAMETER_VALUE, Float.valueOf(alphaTextField.getText()));
						CnSEventManager.handleMessage(ev);
					}
					catch (Exception ex) {
						
					}
				}
			}
			
		});
	}
}
