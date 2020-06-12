package org.cytoscape.clustnsee3.internal.gui.control;

import java.awt.Component;

import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

public class CnSControlPanel extends CnSPanel implements CytoPanelComponent {
	private static final long serialVersionUID = -5798886682673421450L;

	private CnSControlScopePanel scopePanel;
	private CnSControlAlgorithmPanel algorithmPanel;
	private CnSControlActionPanel actionPanel;
	
	public CnSControlPanel(String title) {
		super(title);
		initGraphics();
	}
	
	protected void initGraphics() {
		super.initGraphics();
		scopePanel = new CnSControlScopePanel("Scope");
		addComponent(scopePanel, 0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 10, 10, 10, 0, 0);
		algorithmPanel = new CnSControlAlgorithmPanel("Algorithm");
		addComponent(algorithmPanel, 0, 1, 1, 1, 1.0, 1.0, NORTH, HORIZONTAL, 0, 10, 10, 10, 0, 0);
		actionPanel = new CnSControlActionPanel("");
		addComponent(actionPanel, 0, 2, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 10, 10, 10, 0, 0);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}