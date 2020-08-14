package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.task.CnSAnalyzeTask;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
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
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				CyNetwork network = cam.getCurrentNetwork();
				if (network == null)
					JOptionPane.showMessageDialog(null, "You must select a network first !");
				else {
					ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev);
					TaskIterator ti = new TaskIterator();
					CnSAnalyzeTask task = new CnSAnalyzeTask(network);
					ti.append(task);
					dialogTaskManager.execute(ti);
				}
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

	/**
	 * 
	 * @param
	 * @return
	 */
	public void setAnalysisEnabled(Boolean enable) {
		analyzeButton.setEnabled(enable);
	}
}