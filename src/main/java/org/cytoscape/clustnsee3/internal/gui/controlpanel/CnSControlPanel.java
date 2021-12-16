package org.cytoscape.clustnsee3.internal.gui.controlpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.CnSNetworksTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root.CnSAFTreeNetworksRootNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.CnSAFTreeModel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTree;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellEditor;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.task.CnSAnalyzeTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class CnSControlPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener  {
	private static final long serialVersionUID = -5798886682673421450L;
	
	public static final int ADD_MAPPED_NETWORK = 1;
	public static final int REMOVE_MAPPED_NETWORK = 2;
	public static final int REFRESH = 3;

	public static final int ANNOTATION_FILE = 1001;
	public static final int NETWORK = 1002;
	public static final int TREE_FILE_NODE = 1003;
	public static final int MAPPED_NODES = 1004;
	public static final int MAPPED_ANNOTATIONS = 1005;
	public static final int NETWORK_NODES = 1006;
	public static final int FILE_ANNOTATIONS = 1007;

	
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
		initGraphics();
		initListeners();
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
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				CyNetwork network = cam.getCurrentNetwork();
				if (network == null)
					JOptionPane.showMessageDialog(null, "You must select a network first !");
				else {
					ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev);
					TaskIterator ti = new TaskIterator();
					CnSAnalyzeTask task = new CnSAnalyzeTask(network);
					ti.append(task);
					dialogTaskManager.execute(ti);
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		CyNetwork network = (CyNetwork)event.getParameter(NETWORK);
		CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);
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
				//tree.scrollPathToVisible(new TreePath(nnn.getPath()));
				//networksTreeModel.nodeStructureChanged(nnn);
				//tree.revalidate();
				//tree.repaint();
				//tree.treeDidChange();
				//detailsNodePanel.initGraphics();
				detailsNodePanel.getNetworksTree().updateUI();
				detailsNodePanel.revalidate();
				detailsNodePanel.repaint();
				break;

			case REMOVE_MAPPED_NETWORK:
				CnSAFTreeNetworkNetnameNode tnn = (CnSAFTreeNetworkNetnameNode)event.getParameter(TREE_FILE_NODE);
				networksTreeModel = ((CnSAFTreeNetworksRootNode)tnn.getParent()).getDetailsNodePanel().getNetworksTreeModel();
				networksTreeModel.removeNetwork(tnn, network, af);
				((CnSAFTreeNetworksRootNode)tnn.getParent()).getDetailsNodePanel().repaint();
				break;

			case REFRESH :
				tree.updateUI();
				treeImportAnnotationPanel.invalidate();
				treeImportAnnotationPanel.repaint();
				invalidate();
				repaint();

				break;
		}
		return null;
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
}