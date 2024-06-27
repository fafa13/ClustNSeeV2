package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.osgi.framework.BundleContext;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class CnSStartMenu extends AbstractCyAction {
	private static final long serialVersionUID = 4674370847552144972L;
	private static CnSStartMenu instance;
	private boolean en;
	
	private CnSStartMenu(BundleContext context, CyActivator ca) {
		super("Start"); 						// name est le menu item
		setPreferredMenu("Apps.Clust&See"); 	// definit le menu
		en = true;
	}

	public void actionPerformed(ActionEvent e) {
		CnSEvent ev = new CnSEvent(CyActivator.START, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CnSEventManager.handleMessage(ev, true);
		//ev = new CnSEvent(CnSStyleManager.INIT, CnSEventManager.STYLE_MANAGER, this.getClass());
		//CnSEventManager.handleMessage(ev, true);
		
	}
	
	public static CnSStartMenu getInstance(BundleContext context, CyActivator ca) {
		if (instance == null) instance = new CnSStartMenu(context, ca);
		return instance;
	}
	public boolean insertSeparatorAfter() {
		return false;
	}
	public void setEnabled_(boolean b) {
		super.setEnabled(b);
		en = b;
	}
	public boolean isEnabled() {
		return en;
	}
}
