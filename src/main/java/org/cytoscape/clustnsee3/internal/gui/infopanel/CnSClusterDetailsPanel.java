/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 14 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.infopanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

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
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSClusterDetailsPanel extends CnSPanelSplitCommand {
	private static final long serialVersionUID = -8732351304210969069L;
	
	private JTabbedPane tabbedPane;
	private CnSPanel clusterinfoPanel;
	private CnSTable annotationTable;
	private Vector<Vector<String>> data;
	private JLabel clusterNameLabel, nodesInClusterLabel, monoClusteredNodesLabel, multiClusteredNodesLabel, intraClusterEdgesLabel, extraClusterEdgesLabel;
	private CnSButton exportDataButton;
	private CnSTable nodeTable;
	private CnSNodeListTableCellRenderer nodeListTableCellRenderer;
	
	public CnSClusterDetailsPanel() {
		super();
		nodeTable = new CnSTable();
		nodeTable.setAutoCreateRowSorter(true);
		nodeListTableCellRenderer = new CnSNodeListTableCellRenderer();
		nodeTable.setDefaultRenderer(Vector.class, nodeListTableCellRenderer);
		nodeTable.setDefaultRenderer(String.class, nodeListTableCellRenderer);
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Annotation");
		data = new Vector<Vector<String>>();
		initGraphics();
		initListeners();
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	private void initListeners() {
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
					try {
						BufferedWriter br= new BufferedWriter(new FileWriter(file));
						CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
						CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
						br.write("#ClustnSee cluster nodes list");
						br.newLine();
						br.write("#Algorithm: ");
						if (partition != null) br.write(partition.getAlgorithmName());
						br.newLine();
						br.write("#Network: ");
						if (partition != null) br.write(partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
						br.newLine();
						br.write("#Scope: ");
						if (partition != null) br.write(partition.getScope());
						br.newLine();
						if (partition != null) {
							Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
							while (k.hasNext()) {
								int key = k.next();
								br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
								br.newLine();
							}
						}
						br.write("#Cluster: ");
						br.write(clusterNameLabel.getText());
						br.newLine();
						br.write("#Nodes in cluster: ");
						br.write(nodesInClusterLabel.getText());
						br.newLine();
						br.write("#Mono-clustered nodes: ");
						br.write(monoClusteredNodesLabel.getText());
						br.newLine();
						br.write("#Multi-clustered nodes: ");
						br.write(multiClusteredNodesLabel.getText());
						br.newLine();
						br.write("#Intra-cluster edges: ");
						br.write(intraClusterEdgesLabel.getText());
						br.newLine();
						br.write("#Extra-cluster edges: ");
						br.write(extraClusterEdgesLabel.getText());
						br.newLine();
						br.write("#Annotation list: ");
						for (Vector<String> vw : data) br.write(vw.firstElement() + ";");
						br.newLine();
						br.write("#Selected annotation: ");
						ev = new CnSEvent(CnSPartitionPanel.GET_SELECTED_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
						CnSNodeAnnotation ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev, true).getValue();
						if (ann != null) br.write(ann.getValue());
						br.newLine();
						br.write("#Nodes annotated with selected annotation: ");
						StringBuilder sb = new StringBuilder();
						for (int row = 0; row < nodeTable.getRowSorter().getViewRowCount(); row++) {
							Object value = nodeTable.getModel().getValueAt(nodeTable.getRowSorter().convertRowIndexToModel(row), 0);
							if (!clusterNameLabel.equals("")) {
								ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODE_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
								ev.addParameter(CnSNodeAnnotationManager.NODE, nodeListTableCellRenderer.getCluster().getNodes().get(row).getCyNode());
								Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, false).getValue();
								if (clusterAnnotations == null) clusterAnnotations = new Vector<CnSNodeAnnotation>();
								CnSEvent ev2 = new CnSEvent(CnSPartitionPanel.GET_SELECTED_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
								ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev2, true).getValue();
								if (ann != null)
									if (clusterAnnotations.contains(ann)) {
										sb.append(value.toString());
										sb.append(";");
									}
							}
						}
						if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
						br.write(sb.toString());
						br.newLine();
						write(br);
						br.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void write(BufferedWriter br) throws IOException {
		Object value;
		if (nodeTable.getModel() != null) {
			for (int col = 0; col < nodeTable.getModel().getColumnCount(); col++) {
				br.write(nodeTable.getModel().getColumnName(col));
				br.write("\t");
			}
			br.newLine();
			for (int row = 0; row < nodeTable.getRowSorter().getViewRowCount(); row++) {
				for (int col = 0; col < nodeTable.getModel().getColumnCount(); col++) {
					value = nodeTable.getModel().getValueAt(nodeTable.getRowSorter().convertRowIndexToModel(row), col);
					if (value instanceof JLabel)
						br.write(((JLabel)value).getText());
					else if (value instanceof Vector) {
						StringBuilder sb = new StringBuilder();
						for (CnSNodeAnnotation na : (Vector<CnSNodeAnnotation>)value) {
							sb.append(na.getValue());
							sb.append(";");
						}
						if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
						br.write(sb.toString());
					}
					else
						br.write(value.toString());
					br.write("\t");
				}
				br.newLine();
			}
		}
	}

	public void initGraphics() {
		super.initGraphics();
		nodeTable = new CnSTable() {
			private static final long serialVersionUID = -3262262817917972446L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				int r = rowAtPoint(me.getPoint());
				if (getValueAt(r, c) instanceof JLabel)
					return ((JLabel)getValueAt(r, c)).getText();
				return getValueAt(r, c).toString();
			}
			
		};
		nodeTable.setRowHeight(26);
		nodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nodeTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		nodeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nodeTable.setDefaultRenderer(Vector.class, nodeListTableCellRenderer);
		nodeTable.setDefaultRenderer(String.class, nodeListTableCellRenderer);
		nodeTable.setModel(new CnSNodeListTableModel(null));
		nodeTable.setTableHeader(new JTableHeader(nodeTable.getColumnModel()) {
			private static final
			long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				//CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
				//ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, false).getValue();
				//rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					
				}
				return null;
			}
		});
		nodeTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Node list", new JScrollPane(nodeTable));
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Annotation");
		
		annotationTable = new CnSTable(data, columnNames);
		annotationTable.setRowHeight(26);
		annotationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		annotationTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		annotationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		annotationTable.setTableHeader(new JTableHeader(annotationTable.getColumnModel()) {
			private static final
			long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				switch(c) {
					
				}
				return null;
			}
		});
		annotationTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		JScrollPane scrollPane = new JScrollPane(annotationTable);
		scrollPane.setPreferredSize(new Dimension(300, 150));
		tabbedPane.addTab("Annotation list", scrollPane);
		clusterinfoPanel = new CnSPanel();
		clusterinfoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		clusterNameLabel = new JLabel();
		clusterNameLabel.setFont(clusterNameLabel.getFont().deriveFont(clusterNameLabel.getFont().getStyle() | Font.BOLD));
		clusterinfoPanel.addComponent(clusterNameLabel, 0, 0, 2, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 10, 10, 5, 10, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Nodes in cluster :"), 0, 1, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		nodesInClusterLabel = new JLabel();
		clusterinfoPanel.addComponent(nodesInClusterLabel, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 10, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Mono-clustered nodes :"), 0, 2, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		monoClusteredNodesLabel = new JLabel();
		clusterinfoPanel.addComponent(monoClusteredNodesLabel, 1, 2, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 10, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Multi-clustered nodes :"), 0, 3, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		multiClusteredNodesLabel = new JLabel();
		clusterinfoPanel.addComponent(multiClusteredNodesLabel, 1, 3, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 10, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Intra-cluster edges :"), 0, 4, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		intraClusterEdgesLabel = new JLabel();
		clusterinfoPanel.addComponent(intraClusterEdgesLabel, 1, 4, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 0, 10, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Extra-cluster edges :"), 0, 5, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 10, 5, 0, 0, 0);
		extraClusterEdgesLabel = new JLabel();
		clusterinfoPanel.addComponent(extraClusterEdgesLabel, 1, 5, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 0, 10, 10, 0, 0);
		
		commandPanel = new CnSPanel();
		commandPanel.addComponent(clusterinfoPanel, 0, 0, 1, 1, 0.0, 0.0, CENTER, BOTH, 5, 5, 5, 5, 0, 0);
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		
		CnSPanel tabbedPanePanel = new CnSPanel();
		tabbedPanePanel.addComponent(tabbedPane, 0, 0, 1, 1, 1.0, 1.0, CENTER, BOTH, 0, 10, 10, 10, 0, 0);
		initGraphics(tabbedPanePanel, commandPanel);
		setResizeWeight(0.0D);
	}
	
	public void init(CnSCluster cluster) {
		
		clusterNameLabel.setText("Cluster #" + cluster.getName());
		
		nodesInClusterLabel.setText(String.valueOf(cluster.getNbNodes()));
		
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
		ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
		
		ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER, this.getClass());
		ev.addParameter(CnSPartitionManager.PARTITION, partition);
		ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
		Integer nb = (Integer)CnSEventManager.handleMessage(ev, true).getValue();
		
		multiClusteredNodesLabel.setText(nb.toString());
		monoClusteredNodesLabel.setText(String.valueOf(cluster.getNbNodes() - nb.intValue()));
		
		intraClusterEdgesLabel.setText(String.valueOf(cluster.getEdges().size()));
		extraClusterEdgesLabel.setText(String.valueOf(cluster.getExtEdges().size()));
		
		nodeTable.setModel(new CnSNodeListTableModel(cluster));
		nodeListTableCellRenderer.setCluster(cluster);
		nodeTable.setRowSorter(new TableRowSorter<CnSNodeListTableModel>((CnSNodeListTableModel)nodeTable.getModel()));
		
		data.clear();
		Vector<String> v;
		for (int i = 0 ; i < cluster.getAnnotations().size(); i++) {
			v = new Vector<String>();
			v.addElement(cluster.getAnnotations().get(i).getAnnotation());
			data.addElement(v);
		}
		annotationTable.fireTableDataChanged();
		setColumnsWidth();
		repaint();
	}

	public void clear() {
		clusterNameLabel.setText("");
		nodesInClusterLabel.setText("");
		multiClusteredNodesLabel.setText("");
		monoClusteredNodesLabel.setText("");
		intraClusterEdgesLabel.setText("");
		extraClusterEdgesLabel.setText("");
		nodeTable.setModel(new CnSNodeListTableModel(null));
		nodeTable.setRowSorter(null);
		data.clear();
		annotationTable.fireTableDataChanged();
		nodeTable.fireTableDataChanged();
		setColumnsWidth();
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		FontMetrics headerFontMetrics = nodeTable.getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)nodeTable.getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < nodeTable.getColumnModel().getColumnCount(); col++) {
			pWidth = headerFontMetrics.stringWidth(nodeTable.getModel().getColumnName(col)) + nodeTable.getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = nodeTable.getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < nodeTable.getRowCount(); row++) {
				Component comp = nodeTable.prepareRenderer(nodeTable.getDefaultRenderer(nodeTable.getColumnClass(col)), row, col);
				if (comp != null)
					pWidth = Math.max(pWidth, comp.getPreferredSize().width + 10 + nodeTable.getIntercellSpacing().width);
			}
	        pWidth = Math.min(pWidth, maxWidth);
	        tc.setPreferredWidth(0);
			tc.setPreferredWidth(pWidth);
		}
		for (int col = 0; col < annotationTable.getColumnModel().getColumnCount(); col++) {
			pWidth = headerFontMetrics.stringWidth(annotationTable.getModel().getColumnName(col)) + annotationTable.getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = annotationTable.getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < annotationTable.getRowCount(); row++) {
				Component comp = annotationTable.prepareRenderer(annotationTable.getDefaultRenderer(annotationTable.getColumnClass(col)), row, col);
				if (comp != null)
					pWidth = Math.max(pWidth, comp.getPreferredSize().width + 10 + annotationTable.getIntercellSpacing().width);
			}
	        pWidth = Math.min(pWidth, maxWidth);
	        tc.setPreferredWidth(0);
			tc.setPreferredWidth(pWidth);
		}
	}
}
