package org.cytoscape.clustnsee3.internal;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.swing.AbstractCyAction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MenuActionClustnsee extends AbstractCyAction {
	private static final long serialVersionUID = 4674370847552144972L;
	private BundleContext context;
	private CyActivator cyActivator;
	private ServiceRegistration ref;
	private static MenuActionClustnsee instance;
	
	private MenuActionClustnsee(String name, BundleContext context, CyActivator ca) {
		super(name); 				// name est le menu item
		setPreferredMenu("Apps"); 	// definit le menu
		
		this.context = context;
		cyActivator = ca;
	}

	public void actionPerformed(ActionEvent e) {
		ref = context.registerService(CnSClustnseePlugin.class.getName(), CnSClustnseePlugin.getInstance(context, cyActivator), new Properties());
    }
	public void stop() {
		instance = null;
		if (ref != null) {
			ref.unregister();
			ref = null;
		}
	}
	public static MenuActionClustnsee getInstance(String name, BundleContext context, CyActivator ca) {
		if (instance == null) instance = new MenuActionClustnsee(name, context, ca);
		return instance;
	}
}
