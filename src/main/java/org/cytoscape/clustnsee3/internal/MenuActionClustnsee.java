package org.cytoscape.clustnsee3.internal;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.osgi.framework.BundleContext;


/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MenuActionClustnsee extends AbstractCyAction {
	private static final long serialVersionUID = 4674370847552144972L;
	private BundleContext context;
	private CyActivator cyActivator;
	
	public MenuActionClustnsee(String name, BundleContext context, CyActivator ca) {
		super(name); 				// name est le menu item
		setPreferredMenu("Apps"); 	// definit le menu
		
		this.context = context;
		cyActivator = ca;
	}

	public void actionPerformed(ActionEvent e) {
		context.registerService(CytoPanelComponent.class.getName(), CnSClustnseePlugin.getInstance(context, cyActivator), new Properties());
    }
}
