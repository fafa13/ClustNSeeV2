package org.cytoscape.clustnsee3.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.gui.menu.factory.CnSNodeContextMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.factory.CyNodeViewShowClusterlinksMenuFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskObserver;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator implements CnSEventListener {
	public static final int GET_NETWORK_FACTORY = 1;
	public static final int GET_NETWORK_MANAGER = 2;
	public static final int GET_NETWORK_VIEW_MANAGER = 3;
	public static final int GET_NETWORK_VIEW_FACTORY = 4;
	public static final int GET_ROOT_NETWORK_MANAGER = 5;
	public static final int GET_EVENT_HELPER = 6;
	public static final int GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY = 7;
	public static final int GET_SYNCHRONOUS_TASK_MANAGER = 8;
	public static final int GET_TASK_OBSERVER = 9;
	public static final int GET_APPLICATION_MANAGER = 10;
	public static final int GET_LAYOUT_ALGORITHM_MANAGER = 11;
	public static final int GET_RENDERING_ENGINE_MANAGER = 12; 
	public static final int GET_CY_EVENT_HELPER = 13;
	
	private BundleContext bc = null;
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		bc = context;
		
		// Definit le menu item
		MenuActionClustnsee clustnsee = new MenuActionClustnsee("Clustnsee", context, this);
		registerAllServices(context, clustnsee, new Properties());
		
		CyNodeViewContextMenuFactory myNodeViewContextMenuFactory  = new CnSNodeContextMenuFactory();
		Properties myNodeViewContextMenuFactoryProps = new Properties();
		myNodeViewContextMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, myNodeViewContextMenuFactory, myNodeViewContextMenuFactoryProps);
		
		CyNodeViewShowClusterlinksMenuFactory myNodeViewShowClusterlinksMenuFactory = new CyNodeViewShowClusterlinksMenuFactory();
		Properties myNodeViewShowClusterlinksMenuFactoryProps = new Properties();
		myNodeViewShowClusterlinksMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, myNodeViewShowClusterlinksMenuFactory, myNodeViewShowClusterlinksMenuFactoryProps);
		//context.ungetService(context.getServiceReference(myNodeViewContextMenuFactory.getClass().getName()));
		/*CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
		
		MenuAction action = new MenuAction(cyApplicationManager, "Hello World App");
		
		Properties properties = new Properties();
		
		registerAllServices(context, action, properties);*/
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		
		switch(event.getAction()) {
			case GET_NETWORK_FACTORY :
				ret = getService(bc, CyNetworkFactory.class);
				break;
			case GET_NETWORK_MANAGER :
				ret = getService(bc, CyNetworkManager.class);
				break;
			case GET_NETWORK_VIEW_MANAGER :
				ret = getService(bc, CyNetworkViewManager.class);
				break;
			case GET_NETWORK_VIEW_FACTORY :
				ret = getService(bc, CyNetworkViewFactory.class);
				break;
			case GET_ROOT_NETWORK_MANAGER :
				ret = getService(bc, CyRootNetworkManager.class);
				break;
			case GET_EVENT_HELPER :
				ret = getService(bc, CyEventHelper.class);
				break;
			case GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY :
				ret = getService(bc, ApplyPreferredLayoutTaskFactory.class);
				break;
			case GET_SYNCHRONOUS_TASK_MANAGER :
				ret = getService(bc, SynchronousTaskManager.class);
				break;
			case GET_TASK_OBSERVER :
				ret = getService(bc, TaskObserver.class);
				break;
			case GET_APPLICATION_MANAGER :
				ret = getService(bc, CyApplicationManager.class);
				break;
			case GET_LAYOUT_ALGORITHM_MANAGER :
				ret = getService(bc, CyLayoutAlgorithmManager.class);
				break;
			case GET_RENDERING_ENGINE_MANAGER :
				ret = getService(bc, RenderingEngineManager.class);
				break;
			case GET_CY_EVENT_HELPER :
				ret = getService(bc, CyEventHelper.class);
				break;
		}
		return ret;
	}
}
