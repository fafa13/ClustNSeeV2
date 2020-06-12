package org.cytoscape.clustnsee3.internal;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.app.CyAppAdapter;
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
	private CyAppAdapter adapter;
	private CyActivator cyActivator;
	
	public MenuActionClustnsee(String name, BundleContext context, CyAppAdapter adapter, CyActivator ca) {
		super(name); 				// name est le menu item
		setPreferredMenu("Apps"); 	// definit le menu
		
		this.context = context;
		this.adapter = adapter;
		cyActivator = ca;
	}

	public void actionPerformed(ActionEvent e) {
		//CnSControlPanel cnsPanel= new CnSControlPanel("Clust&see");
        //context.registerService( CytoPanelComponent.class.getName(), main_panel, new Properties());
       context.registerService( CytoPanelComponent.class.getName(), CnSClustnseePlugin.getInstance(context, adapter, cyActivator), new Properties());
        
	}
}
