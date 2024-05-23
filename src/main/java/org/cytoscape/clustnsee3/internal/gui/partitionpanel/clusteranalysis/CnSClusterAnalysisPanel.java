/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusteranalysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanelSplitCommand;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTable;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSClusterAnalysisPanel extends CnSPanelSplitCommand  {
	private static final long serialVersionUID = 9174731642049743947L;
	
	private CnSTable clusterTable;
	private CnSButton exportDataButton;
	private JComboBox<CnSCluster> clusterList;
	
	public CnSClusterAnalysisPanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void initGraphics( ) {
		System.err.println("CnSClusterAnalysisPanel.initGraphics()");
		CnSPanel showAnnotationsPanel = new CnSPanel();
		showAnnotationsPanel.setBorder(BorderFactory.createEtchedBorder());
		showAnnotationsPanel.addComponent(new JLabel("Show"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		clusterList = new JComboBox<CnSCluster>();
		clusterList.setMinimumSize(new Dimension(100,16));
		showAnnotationsPanel.addComponent(clusterList, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		showAnnotationsPanel.addComponent(new JLabel("annotations"), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 10, 0, 0);
		//addComponent(showAnnotationsPanel,0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
		
		exportDataButton = new CnSButton("Export data");
		//addComponent(exportDataButton, 1, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 5, 0, 0);
		
		clusterTable = new CnSTable();
		clusterTable.setRowHeight(26);
		clusterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		clusterTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		clusterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		clusterTable.setTableHeader(new JTableHeader(clusterTable.getColumnModel()) {
			private static final
			long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
				ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, false).getValue();
				rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					case 1 : return rBundle.getString("CnSClusterTableModel.nodes_MO");
					case 2 : return rBundle.getString("CnSClusterTableModel.frequency_MO");
					case 3 : return rBundle.getString("CnSClusterTableModel.enrichedClustersPhyper_MO");
					case 4 : return rBundle.getString("CnSClusterTableModel.enrichedClustersMajorityPercent_MO");
				}
				return null;
			}
		});
		clusterTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		commandPanel = new CnSPanel();
		
		initGraphics(commandPanel, new JScrollPane(clusterTable));
		commandPanel.addComponent(showAnnotationsPanel,0, 0, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		commandPanel.addComponent(exportDataButton, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		//addComponent(new JScrollPane(clusterTable), 0, 1, 2, 1, 1.0, 1.0, CENTER, BOTH, 0, 5, 0, 5, 0, 0);
	}
	
	public CnSNodeAnnotation getSelectedAnnotation() {
		System.err.println("CnSClusterAnalysisPanel.getSelectedAnnotation()");
		if (clusterTable.getSelectedRow() != -1)
			return (CnSNodeAnnotation)clusterTable.getValueAt(clusterTable.getSelectedRow(), 0);
		else
			return null;
	}
	
	public void selectCluster(int id) {
		int i;
		System.err.println("CnSClusterAnalysisPanel.selectCluster(" + id + ")");
		//clusterList.setSelectedIndex(id);
		if (id == 0) 
			i = 0;
		else
			for (i = 0; i < clusterList.getItemCount(); i++) 
				if (clusterList.getItemAt(i).toString().equals("Cluster " + id)) break;
		if (i >= clusterList.getItemCount()) i = 0;
		clusterList.setSelectedIndex(i);
	}
	private void initListeners() {
		System.err.println("CnSClusterAnalysisPanel.initListeners()");
		clusterTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = clusterTable.getSelectedRow();
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.SET_SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
					ev.addParameter(CnSPartitionPanel.ANNOTATION, clusterTable.getValueAt(clusterTable.getSelectedRow(), 0));
					CnSEventManager.handleMessage(ev, true);
					
					clusterTable.getSelectionModel().setSelectionInterval(index, index);
				}
			}
		});
		
		clusterList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL, this.getClass());
					String cn = clusterList.getItemAt(clusterList.getSelectedIndex()).getName();
					int ci;
					if (cn.equals("all")) 
						ci = 0;
					else
						ci = Integer.valueOf(cn);
					ev.addParameter(CnSResultsPanel.CLUSTER_NAME, ci);
					CnSEventManager.handleMessage(ev, true);
				}
			}
		});
		exportDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("CSV file (separator: tabulation)", "csv"));
				int ret = jfc.showSaveDialog(null);
				boolean tosave =false;
				File file = null;
				if (ret == JFileChooser.APPROVE_OPTION) {
					tosave =true;
					file = jfc.getSelectedFile();
					if (file.exists()) {
						ret = JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists. Are you sure you want to owerwrite it ?");
						tosave =  (ret == JOptionPane.YES_OPTION);
					}	
				}
				if (tosave) {
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.EXPORT_CLUSTER_ANALYSIS_DATA, CnSEventManager.PARTITION_PANEL, this.getClass());
					ev.addParameter(CnSPartitionPanel.OUTPUT_FILE, file);
					CnSEventManager.handleMessage(ev, true);
				}
			}
		});
	}
	
	public void selectAnnotation(CnSNodeAnnotation ann) {
		System.err.println("CnSClusterAnalysisPanel.selectAnnotation(" + ann + ")");
		if (ann != null ) {
			int index = clusterTable.getRowSorter().convertRowIndexToView(((CnSClusterTableModel)clusterTable.getModel()).getIndex(ann));
			clusterTable.getSelectionModel().setSelectionInterval(index, index);
			clusterTable.scrollRectToVisible(clusterTable.getCellRect(index, 0, true));
		}
		else {
			clusterTable.getSelectionModel().clearSelection();
			clusterTable.scrollRectToVisible(clusterTable.getCellRect(0, 0, true));
		}
	}
	
	public void init(CnSPartition partition) {
		System.err.println("CnSClusterAnalysisPanel.init(" + partition + ")");
		CnSClusterTableModel model = new CnSClusterTableModel(partition);
		clusterTable.setModel(model);
		
		RowFilter<CnSClusterTableModel,Integer> annotationFilter = new RowFilter<CnSClusterTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSClusterTableModel, ? extends Integer> entry) {
				int i = clusterList.getSelectedIndex();
				if (i == 0) 
					return true;
				else {
					CnSClusterTableModel model = entry.getModel();
					CnSCluster cluster = model.getSelectedCluster();
					if (cluster == null) return false;
					CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, false).getValue();
					TreeSet<CnSNodeAnnotation> ts = new TreeSet<CnSNodeAnnotation>(clusterAnnotations);
					return ts.contains(model.getAnnotation(entry.getIdentifier()));
				}
			}
		};
		TableRowSorter<CnSClusterTableModel> sorter = new TableRowSorter<CnSClusterTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		clusterTable.setRowSorter(sorter);
		clusterList.removeAllItems();
		clusterList.addItem(new CnSCluster());
		if (partition != null) {
			//for (int i = 1; i <= partition.getClusters().size(); i++) clusterList.addItem("cluster " + i);
			for (CnSCluster cl : partition.getClusters()) clusterList.addItem(cl);
		}
		setColumnsWidth();
		//clusterTable.fireTableDataChanged();
		model.fireTableDataChanged();
	}

	public void refresh(CnSPartition partition) {
		System.err.println("CnSClusterAnalysisPanel.refresh");
		clusterList.removeAllItems();
		clusterList.addItem(new CnSCluster());
		if (partition != null) {
			for (int i = 1; i <= partition.getClusters().size(); i++) {
				CnSCluster cluster = partition.getCluster(i);
				CnSEvent ev = new CnSEvent(CnSPartitionPanel.GET_HIDE_SMALL_CLUSTERS, CnSEventManager.PARTITION_PANEL, this.getClass());
				boolean b = (Boolean)CnSEventManager.handleMessage(ev, true).getValue();
				if (b) {
					if (cluster.getNbNodes() > 4 ) {
						clusterList.addItem(cluster);
					}
				}
				else
					clusterList.addItem(cluster);
			}
		}
		clusterList.setSelectedIndex(0);
		((CnSClusterTableModel)clusterTable.getModel()).setSelectedCluster(null);
		
		//clusterTable.fireTableDataChanged();
		((CnSClusterTableModel)clusterTable.getModel()).fireTableDataChanged();
	}
	public void init(CnSCluster cluster) {
		if (cluster != ((CnSClusterTableModel)clusterTable.getModel()).getSelectedCluster()) {
			System.err.println("CnSClusterAnalysisPanel.init(" + cluster + ")");
			((CnSClusterTableModel)clusterTable.getModel()).setSelectedCluster(cluster);
			CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, true).getValue();
			final TreeSet<CnSNodeAnnotation> ts = new TreeSet<CnSNodeAnnotation>(clusterAnnotations);
			
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			final Vector<?> annotations = (Vector<?>)CnSEventManager.handleMessage(ev, true).getValue();
			
			RowFilter<CnSClusterTableModel,Integer> annotationFilter = new RowFilter<CnSClusterTableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends CnSClusterTableModel, ? extends Integer> entry) {
					int i = clusterList.getSelectedIndex();
					if (i == 0) 
						return true;
					else {
						CnSClusterTableModel model = entry.getModel();
						CnSCluster cluster = model.getSelectedCluster();
						if (cluster == null) return false;
						return ts.contains(annotations.elementAt(entry.getIdentifier()));
					}
				}
			};
			TableRowSorter<CnSClusterTableModel> sorter = new TableRowSorter<CnSClusterTableModel>((CnSClusterTableModel)(clusterTable.getModel()));
			sorter.setRowFilter(annotationFilter);
			sorter.setMaxSortKeys(1);
			setColumnsWidth();
			clusterTable.setRowSorter(sorter);
			//clusterList.setSelectedIndex(cluster.getID());
			selectCluster(cluster.getID());
			//clusterTable.fireTableDataChanged();
			((CnSClusterTableModel)clusterTable.getModel()).fireTableDataChanged();
		}
	}
	public void init() {
		System.err.println("CnSClusterAnalysisPanel.init()");
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
		CyNetwork network = cam.getCurrentNetwork();
		CnSClusterTableModel model = new CnSClusterTableModel(network);
		clusterTable.setModel(model);
		setColumnsWidth();
		RowFilter<CnSClusterTableModel,Integer> annotationFilter = new RowFilter<CnSClusterTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSClusterTableModel, ? extends Integer> entry) {
				int i = clusterList.getSelectedIndex();
				if (i == 0) 
					return true;
				else {
					CnSClusterTableModel model = entry.getModel();
					CnSCluster cluster = model.getSelectedCluster();
					if (cluster == null) return true;
					CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					Vector<?> clusterAnnotations = (Vector<?>)CnSEventManager.handleMessage(ev, false).getValue();
					return clusterAnnotations.contains(model.getAnnotation(entry.getIdentifier()));
				}
			}
		};
		TableRowSorter<CnSClusterTableModel> sorter = new TableRowSorter<CnSClusterTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		clusterTable.setRowSorter(sorter);
		model.fireTableDataChanged();
		//clusterTable.fireTableDataChanged();
		clusterList.removeAllItems();
	}
	
	public void clear() {
		System.err.println("CnSClusterAnalysisPanel.clear()");
		((CnSClusterTableModel)clusterTable.getModel()).setSelectedCluster(null);
		clusterTable.clear();
		//clusterTable.fireTableDataChanged();
		((CnSClusterTableModel)clusterTable.getModel()).fireTableDataChanged();
	}
	
	public void refresh() {
		System.err.println("CnSClusterAnalysisPanel.refresh()");
		//clusterTable.fireTableDataChanged();
		((CnSClusterTableModel)clusterTable.getModel()).fireTableDataChanged();
	}
	
	public CnSCluster getSelectedCluster() {
		System.err.println("CnSClusterAnalysisPanel.getSelectedCluster()");
		CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL, this.getClass());
		CnSCluster cl = (CnSCluster)(CnSEventManager.handleMessage(ev, true)).getValue();
		return cl;
	}
	
	private void setColumnsWidth() {
		System.err.println("CnSClusterAnalysisPanel.setColumnsWidth()");
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		FontMetrics headerFontMetrics = clusterTable.getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)clusterTable.getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < clusterTable.getColumnModel().getColumnCount(); col++) {
			pWidth = headerFontMetrics.stringWidth(clusterTable.getModel().getColumnName(col)) + clusterTable.getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = clusterTable.getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < clusterTable.getRowCount(); row++) {
				Component comp = clusterTable.prepareRenderer(clusterTable.getDefaultRenderer(clusterTable.getColumnClass(col)), row, col);
				if (comp != null)
					pWidth = Math.max(pWidth, comp.getPreferredSize().width + 10 + clusterTable.getIntercellSpacing().width);
			}
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		    //System.err.println("colmun = " + clusterTable.getModel().getColumnName(col) + " ; id = " + col + " ; pwidth = " + pWidth);
		}
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @throws IOException 
	 */
	public void write(BufferedWriter br) throws IOException {
		if (clusterTable.getModel() != null) {
			for (int col = 0; col < clusterTable.getModel().getColumnCount(); col++) {
				br.write(clusterTable.getModel().getColumnName(col));
				br.write("\t");
			}
			br.newLine();
			for (int row = 0; row < clusterTable.getRowSorter().getViewRowCount(); row++) {
				for (int col = 0; col < clusterTable.getModel().getColumnCount(); col++) {
					br.write(clusterTable.getModel().getValueAt(clusterTable.getRowSorter().convertRowIndexToModel(row), col).toString());
					br.write("\t");
				}
				br.newLine();
			}
		}
	}
}
