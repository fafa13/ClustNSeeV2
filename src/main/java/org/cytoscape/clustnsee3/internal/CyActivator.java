package org.cytoscape.clustnsee3.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory.CnSAnnotateClusterMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory.CnSExpandCompressClusterNodeMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory.CnSShowClusterlinksMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSStartMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSStopMenu;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.task.write.ExportVizmapTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskObserver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
	public static final int GET_EXPORT_STYLE_FACTORY = 14;
	public static final int GET_VISUAL_MAPPING_MANAGER = 15;
	public static final int GET_VISUAL_STYLE_FACTORY = 16;
	public static final int GET_CONTINUOUS_VISUAL_MAPPING_FUNCTION_FACTORY = 17; 
	public static final int GET_DISCRETE_VISUAL_MAPPING_FUNCTION_FACTORY = 18;
	public static final int GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY = 19;
	public static final int GET_LOAD_VIZMAP_FILE_TASK_FACTORY = 20;
	public static final int GET_VIZMAP_MANAGER = 21;
	public static final int STOP = 22;
	public static final int GET_CYTO_PANEL = 23;
	public static final int REGISTER_CLUSTNSEE = 24;
	
	public static final int NAME = 1000;

	private BundleContext bc = null;
	private CnSStartMenu clustnseeStart;
	private CnSStopMenu clustnseeStop;
	private ServiceRegistration clustnseeService;
	
	@Override
	public void start(BundleContext context) throws Exception {
		bc = context;
		
		// Definit le menu item
		clustnseeStart = CnSStartMenu.getInstance(context, this);
		registerAllServices(context, clustnseeStart, new Properties());
		clustnseeService = CnSStartMenu.getInstance(context, this).getRef();
		
		clustnseeStop = CnSStopMenu.getInstance(context, this);
		registerAllServices(context, clustnseeStop, new Properties());
		
		CnSExpandCompressClusterNodeMenuFactory expandCompressClusterNodeMenuFactory  = new CnSExpandCompressClusterNodeMenuFactory();
		Properties expandCompressClusterNodeMenuFactoryProps = new Properties();
		expandCompressClusterNodeMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, expandCompressClusterNodeMenuFactory, expandCompressClusterNodeMenuFactoryProps);
		
		CnSShowClusterlinksMenuFactory showClusterlinksMenuFactory = new CnSShowClusterlinksMenuFactory();
		Properties showClusterlinksMenuFactoryProps = new Properties();
		showClusterlinksMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, showClusterlinksMenuFactory, showClusterlinksMenuFactoryProps);
		
		CnSAnnotateClusterMenuFactory annotateClusterMenuFactory = new CnSAnnotateClusterMenuFactory();
		Properties annotateClusterMenuFactoryProps = new Properties();
		annotateClusterMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, annotateClusterMenuFactory, annotateClusterMenuFactoryProps);
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
			case GET_EXPORT_STYLE_FACTORY :
				ret = getService(bc, ExportVizmapTaskFactory.class);
				break;
			
			case GET_VISUAL_MAPPING_MANAGER :
				ret = getService(bc,VisualMappingManager.class);
                break;
			case GET_VISUAL_STYLE_FACTORY :
				ret = getService(bc,VisualStyleFactory.class);
				break;
			case GET_CONTINUOUS_VISUAL_MAPPING_FUNCTION_FACTORY :	
				ret = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
				break;
			case GET_DISCRETE_VISUAL_MAPPING_FUNCTION_FACTORY :
				ret = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
				break;
			case GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY :
				ret = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
				break;
			case GET_LOAD_VIZMAP_FILE_TASK_FACTORY :
				ret = getService(bc,LoadVizmapFileTaskFactory.class);
				break;
			case GET_VIZMAP_MANAGER :
				ret = getService(bc, VisualMappingManager.class);
				break;
			case STOP:
				CnSClustnseePlugin.getInstance(bc, this).stop();
				if (clustnseeService != null) {
					clustnseeService.unregister();
					clustnseeService = null;
				}
				//CnSStartMenu.getInstance(bc, this).stop();
				break;
			case GET_CYTO_PANEL :
				CySwingApplication app = getService(bc,CySwingApplication.class);
				ret = app.getCytoPanel((CytoPanelName)event.getParameter(NAME));
				break;
			case REGISTER_CLUSTNSEE :
				break;
		}
		return ret;
	}
}
