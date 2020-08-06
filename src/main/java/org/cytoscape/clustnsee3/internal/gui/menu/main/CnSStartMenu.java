package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.CnSClustnseePlugin;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class CnSStartMenu extends AbstractCyAction {
	private static final long serialVersionUID = 4674370847552144972L;
	private BundleContext context;
	private CyActivator cyActivator;
	private ServiceRegistration ref;
	private static CnSStartMenu instance;
	
	private CnSStartMenu(BundleContext context, CyActivator ca) {
		super("Start"); 						// name est le menu item
		setPreferredMenu("Apps.Clust&see"); 	// definit le menu
		
		this.context = context;
		cyActivator = ca;
	}

	public void actionPerformed(ActionEvent e) {
		ref = context.registerService(CnSClustnseePlugin.class.getName(), CnSClustnseePlugin.getInstance(context, cyActivator), new Properties());
	}
	public ServiceRegistration getRef() {
		return ref;
	}
	public static CnSStartMenu getInstance(BundleContext context, CyActivator ca) {
		if (instance == null) instance = new CnSStartMenu(context, ca);
		return instance;
	}
}
