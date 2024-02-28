package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.CnSNetworksTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root.CnSAFTreeNetworksRootNode;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTree;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeCellEditor;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.CnSAFTreeModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.task.CnSAnalyzeTask;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

class RefreshTree extends SwingWorker<String, Object> {
	CnSPanelTree tree;
	
	public RefreshTree(CnSPanelTree tree) {
		this.tree = tree;
	}
	@Override
	public String doInBackground() {
		SwingUtilities.invokeLater(new Runnable() { public void run() { tree.updateUI(); } });
		return "";
   }

	@Override
	protected void done() {
		
	}
}
public class CnSControlPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener, SessionLoadedListener {
	private static final long serialVersionUID = -5798886682673421450L;
	
	public static final int ADD_MAPPED_NETWORK = 1;
	public static final int REMOVE_MAPPED_NETWORK = 2;
	public static final int REFRESH = 3;
	public static final int REMOVE_ANNOTATION_FILE = 4;
	public static final int DEANNOTATE_ALL_NETWORKS = 5;

	public static final int ANNOTATION_FILE = 1001;
	public static final int NETWORK = 1002;
	public static final int TREE_FILE_NODE = 1003;
	public static final int MAPPED_NODES = 1004;
	public static final int MAPPED_ANNOTATIONS = 1005;
	public static final int NETWORK_NODES = 1006;
	public static final int FILE_ANNOTATIONS = 1007;
	public static final int FILE_NODE = 1008;
	
	private CnSControlScopePanel scopePanel;
	private CnSControlAlgorithmPanel algorithmPanel;
	private CnSPanel analyzePanel;
	private CnSButton analyzeButton;
	
	private CnSPanel treeImportAnnotationPanel;
	private CnSAFTreeModel treeModel;
	
	private CnSPanel mainPanel;
	
	private CnSPanelTree tree;
	private CnSAFTreeRootNode rootNode;
	
	public CnSControlPanel(String title) {
		super(title);
		rootNode = null;
		initGraphics();
		initListeners();
	}
	
	public String getActionName(int k) {
		switch(k) {
			case ADD_MAPPED_NETWORK : return "ADD_MAPPED_NETWORK";
			case REMOVE_MAPPED_NETWORK : return "REMOVE_MAPPED_NETWORK";
			case REFRESH : return "REFRESH";
			case REMOVE_ANNOTATION_FILE : return "REMOVE_ANNOTATION_FILE";
			case DEANNOTATE_ALL_NETWORKS : return "DEANNOTATE_ALL_NETWORKS";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case ANNOTATION_FILE : return "ANNOTATION_FILE";
			case NETWORK : return "NETWORK";
			case TREE_FILE_NODE : return "TREE_FILE_NODE";
			case MAPPED_NODES : return "MAPPED_NODES";
			case MAPPED_ANNOTATIONS : return "MAPPED_ANNOTATIONS";
			case NETWORK_NODES : return "NETWORK_NODES";
			case FILE_ANNOTATIONS : return "FILE_ANNOTATIONS";
			case FILE_NODE : return "FILE_NODE";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	public void initGraphics() {
		mainPanel = new CnSPanel(); 
		analyzePanel = new CnSPanel("Analyze", TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
		scopePanel = new CnSControlScopePanel("Scope");
		analyzePanel.addComponent(scopePanel, 0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 0, 10, 0, 0, 0);
		algorithmPanel = new CnSControlAlgorithmPanel("Algorithm");
		analyzePanel.addComponent(algorithmPanel, 0, 1, 1, 1, 1.0, 1.0, NORTH, HORIZONTAL, 0, 0, 0, 0, 0, 0);
		analyzeButton = new CnSButton("Run");
		analyzePanel.addComponent(analyzeButton, 0, 2, 1, 1, 1.0, 1.0, EAST, NONE, 0, 0, 5, 0, 0, 0);
		analyzePanel.initGraphics();
		mainPanel.addComponent(analyzePanel, 0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 0, 0, 0, 0, 0);
		
		treeImportAnnotationPanel = new CnSPanel("Annotation files", TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
		
		Hashtable<Integer, Object> v= new Hashtable<Integer, Object>();
		v.put(CnSAFTreeRootNode.TITLE, "Imported annotation files");
		rootNode = new CnSAFTreeRootNode(v);
		rootNode.getPanel().deriveFont(Font.PLAIN, 14);
		treeModel = new CnSAFTreeModel(rootNode);
		rootNode.setTreeModel(treeModel);
		tree = new CnSPanelTree(treeModel);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new CnSPanelTreeCellRenderer());
		tree.setCellEditor(new CnSPanelTreeCellEditor());
		tree.setRowHeight(0);
		
		JScrollPane jsp2 = new JScrollPane(tree);
		jsp2.getViewport().setPreferredSize(new Dimension(0, 10*26));
		treeImportAnnotationPanel.addComponent(jsp2, 0, 0, 2, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
		treeImportAnnotationPanel.initGraphics();
		mainPanel.addComponent(treeImportAnnotationPanel, 0, 2, 1, 1, 1.0, 1.0, NORTH, BOTH, 10, 0, 0, 0, 0, 0);
		
		addComponent(mainPanel, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
		
		super.initGraphics();
	}
	
	private void initListeners() {
		analyzeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
				CyNetwork network = cam.getCurrentNetwork();
				if (network == null)
					JOptionPane.showMessageDialog(null, "You must select a network first !");
				else {
					ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev, true).getValue();
					TaskIterator ti = new TaskIterator();
					CnSAnalyzeTask task = new CnSAnalyzeTask(network);
					ti.append(task);
					dialogTaskManager.execute(ti);
					System.err.println("fini");
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		if (log) CnSLogger.LogCnSEvent(event, this);
		
		CyNetwork network = (CyNetwork)event.getParameter(NETWORK);
		CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);
		CnSPanelTreeNode fileNode = (CnSPanelTreeNode)event.getParameter(TREE_FILE_NODE);
		CnSNetworksTreeModel networksTreeModel;
		
		switch(event.getAction()) {
			case ADD_MAPPED_NETWORK:
				CnSAFTreeFileNode tfn = (CnSAFTreeFileNode)event.getParameter(TREE_FILE_NODE);
				CnSAFTreeDetailsNode detailsNode = (CnSAFTreeDetailsNode)tfn.getChildAt(0);
				CnSAFTreeDetailsNodePanel detailsNodePanel = (CnSAFTreeDetailsNodePanel)detailsNode.getPanel();
				networksTreeModel = detailsNodePanel.getNetworksTreeModel();
				networksTreeModel.addNetwork(network, af, (Integer)event.getParameter(MAPPED_ANNOTATIONS),
						(Integer)event.getParameter(MAPPED_NODES), (Integer)event.getParameter(NETWORK_NODES), 
						(Integer)event.getParameter(FILE_ANNOTATIONS));
				networksTreeModel.printStructure((CnSPanelTreeNode) networksTreeModel.getRoot(), 0);
				detailsNodePanel.getNetworksTree().updateUI();
				break;

			case REMOVE_MAPPED_NETWORK:
				CnSAFTreeNetworkNetnameNode tnn = (CnSAFTreeNetworkNetnameNode)event.getParameter(TREE_FILE_NODE);
				CnSAFTreeNetworksRootNode networksRootNode = (CnSAFTreeNetworksRootNode)fileNode.getParent();
				CnSAFTreeDetailsNodePanel detailsPanel = networksRootNode.getDetailsNodePanel();
				networksTreeModel = detailsPanel.getNetworksTreeModel();
				networksTreeModel.removeNetwork(tnn, network, af);
				detailsPanel.getNetworksTree().updateUI();
				tree.updateUI();
				updateUI();
				break;

			case REFRESH :
				tree.validate();
				tree.expandRow(0);
				(new RefreshTree(tree)).execute();
				//tree.updateUI();
				break;
				
			case REMOVE_ANNOTATION_FILE :
				if (rootNode != null) rootNode.removeChild(fileNode);
				break;
				
			case DEANNOTATE_ALL_NETWORKS :
				deannotateAllNetworks();
				break;
		}
		return new CnSEventResult<Object>(null);
	}
	
	@Override
	public Component getComponent() {
		return this;
	}
	
	public void deannotateAllNetworks() {
		Vector<CnSNodeAnnotationFile> afs = treeModel.getAnnotationFiles();
		
		for (CnSNodeAnnotationFile af : afs) {
			CnSEvent ev;
			Vector<CnSAFTreeNetworkNetnameNode> vtnnn = treeModel.getAnnotatedNetworks(af);
			for (CnSAFTreeNetworkNetnameNode tnnn : vtnnn) {
				ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, af);
				ev.addParameter(CnSNodeAnnotationManager.NETWORK, tnnn.getData(CnSAFTreeNetworkNetnameNode.NETWORK));
				CnSEventManager.handleMessage(ev, true);
						
				ev = new CnSEvent(CnSControlPanel.REMOVE_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL, this.getClass());
				ev.addParameter(CnSControlPanel.TREE_FILE_NODE, tnnn);
				CnSEventManager.handleMessage(ev, true);
			}
		}
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void setAnalysisEnabled(Boolean enable) {
		analyzeButton.setEnabled(enable);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.session.events.SessionLoadedListener#handleEvent(org.cytoscape.session.events.SessionLoadedEvent)
	 */
	@Override
	public void handleEvent(SessionLoadedEvent e) {
		deannotateAllNetworks();
	}
}