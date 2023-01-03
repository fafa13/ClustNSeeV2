package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;

public class CnSControlActionPanel extends CnSPanel {
	private static final long serialVersionUID = -7770743668639958943L;
	
	private CnSButton closeButton;
	
	public CnSControlActionPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		super.initGraphics();
		closeButton = new CnSButton("Close");
		addComponent(closeButton, 1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, -10, 0, 5, 5, 0, 0);
	}
	
	private void initListeners() {
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