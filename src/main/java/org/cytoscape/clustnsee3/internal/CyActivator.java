package org.cytoscape.clustnsee3.internal;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory.CnSAnnotateClusterMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory.CnSExpandCompressClusterNodeMenuFactory;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSBuildNeighborhoodNetworkMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSComparePartitionsMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSImportPartitionMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSSearchNodeClustersMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSStartMenu;
import org.cytoscape.clustnsee3.internal.gui.menu.main.CnSStopMenu;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
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
import org.cytoscape.work.swing.DialogTaskManager;
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
	public static final int GET_SWING_APPLICATION = 25;
	public static final int GET_TASK_MANAGER = 26;
	public static final int START = 27;
	public static final int GET_RESOURCES_BUNDLE = 28;
	
	public static final int NAME = 1000;

	private BundleContext bc = null;
	private CnSStartMenu clustnseeStart;
	private CnSStopMenu clustnseeStop;
	private CnSImportPartitionMenu clustnseeImportPartition;
	private CnSSearchNodeClustersMenu clustnseeSearchNodeClusters;
	private CnSComparePartitionsMenu clustnseeComparePartitions;
	private CnSBuildNeighborhoodNetworkMenu clustnseeBuildNeighborhoodNetwork;
	private ServiceRegistration<?> plugin;
	private static ResourceBundle resourceBundle;
	
	public String getActionName(int k) {
		switch(k) {
			case GET_NETWORK_FACTORY : return "GET_NETWORK_FACTORY";
			case GET_NETWORK_MANAGER : return "GET_NETWORK_MANAGER";
			case GET_NETWORK_VIEW_MANAGER : return "GET_NETWORK_VIEW_MANAGER";
			case GET_NETWORK_VIEW_FACTORY : return "GET_NETWORK_VIEW_FACTORY";
			case GET_ROOT_NETWORK_MANAGER : return "GET_ROOT_NETWORK_MANAGER";
			case GET_EVENT_HELPER : return "GET_EVENT_HELPER";
			case GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY : return "GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY";
			case GET_SYNCHRONOUS_TASK_MANAGER : return "GET_SYNCHRONOUS_TASK_MANAGER";
			case GET_TASK_OBSERVER : return "GET_TASK_OBSERVER";
			case GET_APPLICATION_MANAGER : return "GET_APPLICATION_MANAGER";
			case GET_LAYOUT_ALGORITHM_MANAGER : return "GET_LAYOUT_ALGORITHM_MANAGER";
			case GET_RENDERING_ENGINE_MANAGER : return "GET_RENDERING_ENGINE_MANAGER";
			case GET_CY_EVENT_HELPER : return "GET_CY_EVENT_HELPER";
			case GET_EXPORT_STYLE_FACTORY : return "GET_EXPORT_STYLE_FACTORY";
			case GET_VISUAL_MAPPING_MANAGER : return "GET_VISUAL_MAPPING_MANAGER";
			case GET_VISUAL_STYLE_FACTORY : return "GET_VISUAL_STYLE_FACTORY";
			case GET_CONTINUOUS_VISUAL_MAPPING_FUNCTION_FACTORY : return "GET_CONTINUOUS_VISUAL_MAPPING_FUNCTION_FACTORY";
			case GET_DISCRETE_VISUAL_MAPPING_FUNCTION_FACTORY : return "GET_DISCRETE_VISUAL_MAPPING_FUNCTION_FACTORY";
			case GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY : return "GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY";
			case GET_LOAD_VIZMAP_FILE_TASK_FACTORY : return "GET_LOAD_VIZMAP_FILE_TASK_FACTORY";
			case GET_VIZMAP_MANAGER : return "GET_VIZMAP_MANAGER";
			case STOP : return "STOP";
			case GET_CYTO_PANEL : return "GET_CYTO_PANEL";
			case REGISTER_CLUSTNSEE : return "REGISTER_CLUSTNSEE";
			case GET_SWING_APPLICATION : return "GET_SWING_APPLICATION";
			case GET_TASK_MANAGER : return "GET_TASK_MANAGER";
			case START : return "START";
			case GET_RESOURCES_BUNDLE : return "GET_RESOURCES_BUNDLE";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case NAME : return "NAME";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		bc = context;
				
		resourceBundle = ResourceBundle.getBundle("org.cytoscape.clustnsee3.internal.resources.cns", Locale.getDefault());
		
		CnSClustnseePlugin.getInstance(bc, this);
		
		// Definit le menu item
		clustnseeStart = CnSStartMenu.getInstance(context, this);
		clustnseeStart.setMenuGravity(0.0f);
		registerAllServices(context, clustnseeStart, new Properties());
		
		clustnseeStop = CnSStopMenu.getInstance();
		clustnseeStop.setEnabled_(false);
		clustnseeStop.setMenuGravity(1.0f);
		registerAllServices(context, clustnseeStop, new Properties());
		
		clustnseeImportPartition = CnSImportPartitionMenu.getInstance();
		clustnseeImportPartition.setMenuGravity(2.0f);
		clustnseeImportPartition.setEnabled_(false);
		registerAllServices(context, clustnseeImportPartition, new Properties());
		
		clustnseeSearchNodeClusters = CnSSearchNodeClustersMenu.getInstance();
		clustnseeSearchNodeClusters.setMenuGravity(3.0f);
		clustnseeSearchNodeClusters.setEnabled_(false);
		registerAllServices(context, clustnseeSearchNodeClusters, new Properties());
		
		clustnseeComparePartitions = CnSComparePartitionsMenu.getInstance();
		clustnseeComparePartitions.setMenuGravity(4.0f);
		clustnseeComparePartitions.setEnabled_(false);
		registerAllServices(context, clustnseeComparePartitions, new Properties());
		
		clustnseeBuildNeighborhoodNetwork = CnSBuildNeighborhoodNetworkMenu.getInstance();
		clustnseeBuildNeighborhoodNetwork.setMenuGravity(5.0f);
		clustnseeBuildNeighborhoodNetwork.setEnabled_(false);
		registerAllServices(context, clustnseeBuildNeighborhoodNetwork, new Properties());
		
		CnSExpandCompressClusterNodeMenuFactory expandCompressClusterNodeMenuFactory  = new CnSExpandCompressClusterNodeMenuFactory();
		Properties expandCompressClusterNodeMenuFactoryProps = new Properties();
		expandCompressClusterNodeMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, expandCompressClusterNodeMenuFactory, expandCompressClusterNodeMenuFactoryProps);
		
		CnSAnnotateClusterMenuFactory annotateClusterMenuFactory = new CnSAnnotateClusterMenuFactory();
		Properties annotateClusterMenuFactoryProps = new Properties();
		annotateClusterMenuFactoryProps.put("preferredMenu", "ClustnSee");
		registerAllServices(context, annotateClusterMenuFactory, annotateClusterMenuFactoryProps);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
		switch(event.getAction()) {
			case GET_NETWORK_FACTORY :
				ret = new CnSEventResult<CyNetworkFactory>(getService(bc, CyNetworkFactory.class));
				break;
			case GET_NETWORK_MANAGER :
				ret = new CnSEventResult<CyNetworkManager>(getService(bc, CyNetworkManager.class));
				break;
			case GET_NETWORK_VIEW_MANAGER :
				ret = new CnSEventResult<CyNetworkViewManager>(getService(bc, CyNetworkViewManager.class));
				break;
			case GET_NETWORK_VIEW_FACTORY :
				ret = new CnSEventResult<CyNetworkViewFactory>(getService(bc, CyNetworkViewFactory.class));
				break;
			case GET_ROOT_NETWORK_MANAGER :
				ret = new CnSEventResult<CyRootNetworkManager>(getService(bc, CyRootNetworkManager.class));
				break;
			case GET_EVENT_HELPER :
				ret = new CnSEventResult<CyEventHelper>(getService(bc, CyEventHelper.class));
				break;
			case GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY :
				ret = new CnSEventResult<ApplyPreferredLayoutTaskFactory>(getService(bc, ApplyPreferredLayoutTaskFactory.class));
				break;
			case GET_SYNCHRONOUS_TASK_MANAGER :
				ret = new CnSEventResult<SynchronousTaskManager<?>>(getService(bc, SynchronousTaskManager.class));
				break;
			case GET_TASK_OBSERVER :
				ret = new CnSEventResult<TaskObserver>(getService(bc, TaskObserver.class));
				break;
			case GET_APPLICATION_MANAGER :
				ret = new CnSEventResult<CyApplicationManager>(getService(bc, CyApplicationManager.class));
				break;
			case GET_LAYOUT_ALGORITHM_MANAGER :
				ret = new CnSEventResult<CyLayoutAlgorithmManager>(getService(bc, CyLayoutAlgorithmManager.class));
				break;
			case GET_RENDERING_ENGINE_MANAGER :
				ret = new CnSEventResult<RenderingEngineManager>(getService(bc, RenderingEngineManager.class));
				break;
			case GET_CY_EVENT_HELPER :
				ret = new CnSEventResult<CyEventHelper>(getService(bc, CyEventHelper.class));
				break;
			case GET_EXPORT_STYLE_FACTORY :
				ret = new CnSEventResult<ExportVizmapTaskFactory>(getService(bc, ExportVizmapTaskFactory.class));
				break;
			
			case GET_VISUAL_MAPPING_MANAGER :
				ret = new CnSEventResult<VisualMappingManager>(getService(bc, VisualMappingManager.class));
                break;
			case GET_VISUAL_STYLE_FACTORY :
				ret = new CnSEventResult<VisualStyleFactory>(getService(bc, VisualStyleFactory.class));
				break;
			case GET_CONTINUOUS_VISUAL_MAPPING_FUNCTION_FACTORY :	
				ret = new CnSEventResult<VisualMappingFunctionFactory>(getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=continuous)"));
				break;
			case GET_DISCRETE_VISUAL_MAPPING_FUNCTION_FACTORY :
				ret = new CnSEventResult<VisualMappingFunctionFactory>(getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=discrete)"));
				break;
			case GET_PASSTHROUGH_VISUAL_MAPPING_FUNCTION_FACTORY :
				ret = new CnSEventResult<VisualMappingFunctionFactory>(getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)"));
				break;
			case GET_LOAD_VIZMAP_FILE_TASK_FACTORY :
				ret = new CnSEventResult<LoadVizmapFileTaskFactory>(getService(bc, LoadVizmapFileTaskFactory.class));
				break;
			case GET_VIZMAP_MANAGER :
				ret = new CnSEventResult<VisualMappingManager>(getService(bc, VisualMappingManager.class));
				break;
			case START :
				CnSClustnseePlugin.getInstance(bc, this).registerServices();
				Hashtable<String, ?> dict = new Hashtable<String, Object>();
				plugin = bc.registerService(CnSClustnseePlugin.class.getName(), CnSClustnseePlugin.getInstance(bc, this), dict);
				clustnseeStop.setEnabled_(true);
				clustnseeStart.setEnabled_(false);
				clustnseeImportPartition.setEnabled_(true);
				clustnseeSearchNodeClusters.setEnabled_(true);
				clustnseeComparePartitions.setEnabled_(true);
				clustnseeBuildNeighborhoodNetwork.setEnabled_(true);
				break;
			case STOP:
				if (plugin != null) {
					plugin.unregister();
					clustnseeStop.setEnabled_(false);
					clustnseeStart.setEnabled_(true);
					clustnseeImportPartition.setEnabled_(false);
					clustnseeSearchNodeClusters.setEnabled_(false);
					clustnseeComparePartitions.setEnabled_(false);
					clustnseeBuildNeighborhoodNetwork.setEnabled_(false);
				}
				CnSClustnseePlugin.getInstance(bc, this).stop();
				break;
			case GET_CYTO_PANEL :
				CySwingApplication app = getService(bc, CySwingApplication.class);
				ret = new CnSEventResult<CytoPanel>(app.getCytoPanel((CytoPanelName)event.getParameter(NAME)));
				break;
			case REGISTER_CLUSTNSEE :
				break;
			case GET_SWING_APPLICATION :
				ret = new CnSEventResult<CySwingApplication>(getService(bc, CySwingApplication.class));
				break;
			case GET_TASK_MANAGER :
				ret = new CnSEventResult<DialogTaskManager>(getService(bc, DialogTaskManager.class));
				break;
			case GET_RESOURCES_BUNDLE :
				ret = new CnSEventResult<ResourceBundle>(resourceBundle);
				break;
		}
		return ret;
	}
	public static ResourceBundle getResourcesBundle() {
		return resourceBundle;
	}
}
