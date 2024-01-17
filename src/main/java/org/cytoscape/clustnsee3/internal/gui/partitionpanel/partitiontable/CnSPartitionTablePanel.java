/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 3 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.annotation.CnSClusterAnnotation;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanelSplitCommand;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSPartitionTablePanel extends CnSPanelSplitCommand {
	private static final long serialVersionUID = 4538284737604540732L;
	
	private CnSPartitionTable table;
	private CnSButton exportDataButton, annotateButton, deannotateButton;
	private CnSCluster selectedCluster;
	private JComboBox<String> statList;
	private JSpinner thresholdSpinner;
	private JCheckBox hideSmallClustersCheckbox;
	private CnSPartition partition;
	private int currentHypergeometricThreshold = 5;
	private int currentMajorityThreshold = 50;
	
	public CnSPartitionTablePanel() {
		super();
		initGraphics();
		initListeners();
		selectedCluster = null;
		partition = null;
	}
	
	public void initGraphics() {
		final ResourceBundle rBundle = CyActivator.getResourcesBundle();
		
		CnSPanel showPanel1 = new CnSPanel();
		showPanel1.addComponent(new JLabel("Show"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		statList = new JComboBox<String>();
		statList.addItem("Hypergeometric");
		statList.addItem("Majority");
		showPanel1.addComponent(statList, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		showPanel1.addComponent(new JLabel("enriched terms"), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		
		CnSPanel showPanel2 = new CnSPanel();
		showPanel2.addComponent(new JLabel("with a threshold of"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		thresholdSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
		showPanel2.addComponent(thresholdSpinner, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		showPanel2.addComponent(new JLabel("%."), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		
		CnSPanel showPanel = new CnSPanel();
		showPanel.addComponent(showPanel1, 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 0, 5, 0, 0);
		showPanel.addComponent(showPanel2, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		showPanel.setBorder(BorderFactory.createEtchedBorder());
		
		CnSPanel buttonsPanel = new CnSPanel();
		annotateButton = new CnSButton("Annotate all clusters");
		annotateButton.setActionCommand("All");
		buttonsPanel.addComponent(annotateButton, 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 0, 0, 0);
		
		deannotateButton = new CnSButton("Remove all clusters annotations");
		deannotateButton.setActionCommand("All");
		buttonsPanel.addComponent(deannotateButton, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		
		buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
		
		CnSPanel hidePanel = new CnSPanel();
		hideSmallClustersCheckbox = new JCheckBox("Hide small clusters");
		hideSmallClustersCheckbox.setToolTipText(rBundle.getString("CnSPartitionTablePanel.HideSmallClustersCheckbox_MO"));
		hidePanel.addComponent(hideSmallClustersCheckbox, 0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 0, 0, 0, 0);
		
		exportDataButton = new CnSButton("Export data");
		hidePanel.addComponent(exportDataButton, 1, 0, 1, 1, 0.0, 0.0, EAST, HORIZONTAL, 5, 5, 0, 5, 0, 0);
	
		commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		commandPanel.addComponent(showPanel, 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 5, 5, 0, 5, 0, 0);
		//commandPanel.addComponent(showPanel2, 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, 5, 5, 0, 5, 0, 0);
		commandPanel.addComponent(buttonsPanel, 0, 1, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 5, 5, 0, 5, 0, 0);
		commandPanel.addComponent(hidePanel, 0, 2, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		
		table = new CnSPartitionTable();
		initGraphics(commandPanel, table.getScrollPane());
		
		table.getTable().setTableHeader(new JTableHeader(table.getTable().getColumnModel()) {
			private static final long serialVersionUID = 4645600937944995274L;

			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				//rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					case 1 : return rBundle.getString("CnSPartitionTableModel.nodes_MO");
					case 2 : return rBundle.getString("CnSPartitionTableModel.edges_MO");
					case 3 : return rBundle.getString("CnSPartitionTableModel.annotationTerms_MO");
					case 4 : return rBundle.getString("CnSPartitionTableModel.enrichmentTerms_MO");
					case 5 : return rBundle.getString("CnSPartitionTableModel.enrichmentStat_MO");
				}
				return null;
			}
		});
		table.getTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		table.getFixedTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
	}
	
	private void initListeners() {
		exportDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("exportDataButton");
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
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.EXPORT_CLUSTER_LIST_DATA, CnSEventManager.PARTITION_PANEL, this.getClass());
					ev.addParameter(CnSPartitionPanel.OUTPUT_FILE, file);
					CnSEventManager.handleMessage(ev, true);
				}
			}
		});
		table.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
		    	CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true);
		    	ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL, this.getClass());
		    	CnSCluster cluster = null;
		    	int selectedRow = -1;
		    	if (!e.getValueIsAdjusting()) {
		    		if (table.getTable().isRowSelected(e.getLastIndex()))
		    			selectedRow = e.getLastIndex();
		    		else if (table.getTable().isRowSelected(e.getFirstIndex()))
		    			selectedRow = e.getFirstIndex();
		    		selectedRow = table.getTable().getSelectedRow();
		    		
		    		if (selectedRow != -1) {
		    			int modelRow = table.getTable().convertRowIndexToModel(selectedRow);
		                cluster = ((CnSPartitionTableModel)table.getTable().getModel()).getCluster(modelRow);
		            }
		    		if (cluster != null) {
		    			ev.addParameter(CnSResultsPanel.CLUSTER_NAME, Integer.parseInt(cluster.getName()));
		    			CnSEventManager.handleMessage(ev, true);
		    			annotateButton.setActionCommand(String.valueOf(cluster.getID()));
		    			annotateButton.setText("Annotate cluster " + cluster.getID());
		    			deannotateButton.setActionCommand(String.valueOf(cluster.getID()));
		    			deannotateButton.setText("Remove cluster " + cluster.getID() + " annotations");
		    		}
		    		else {
		    			CnSEventManager.handleMessage(ev, true);
		    			annotateButton.setActionCommand("All");
		    			annotateButton.setText("Annotate all clusters");
		    			deannotateButton.setActionCommand("All");
		    			deannotateButton.setText("Remove all clusters annotations");
		    		}
		    		ev = new CnSEvent(CnSPartitionPanel.INIT_ANNOTATION_PANEL, CnSEventManager.PARTITION_PANEL, this.getClass());
		    		if (cluster != null) 
		    			ev.addParameter(CnSPartitionPanel.CLUSTER, cluster);
		    		else if (partition != null) {
		    			ev.addParameter(CnSPartitionPanel.PARTITION, partition);
		    		}
		    		CnSEventManager.handleMessage(ev, true);
		    	}
		    }
		});
		statList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("statList : " + statList.getSelectedItem());
				table.fireTableDataChanged();
				if (statList.getSelectedIndex() == 0)
					thresholdSpinner.setValue(currentHypergeometricThreshold);
				else
					thresholdSpinner.setValue(currentMajorityThreshold);
			}
		});
		thresholdSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.err.println("thSpinner : " + thresholdSpinner.getValue());
				table.fireTableDataChanged();
				if (statList.getSelectedIndex() == 0)
					currentHypergeometricThreshold = ((Integer)thresholdSpinner.getValue());
				else
					currentMajorityThreshold = ((Integer)thresholdSpinner.getValue());
			}
		});
		annotateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev;
				CnSCluster cluster;
				Vector<CnSAnnotationClusterPValue> annots;
				CnSClusterAnnotation cca;
				CnSPartition part;
				if (annotateButton.getActionCommand().equals("All")) {
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					part = (CnSPartition)CnSEventManager.handleMessage(ev, true);
					if (part != null) {
						ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER, this.getClass());
						ev.addParameter(CnSPartitionManager.PARTITION, part);
						for (int row = 0; row < table.getTable().getRowSorter().getViewRowCount(); row++) {
							ev.addParameter(CnSPartitionManager.CLUSTER_ID, table.getFixedTable().getValueAt(row, 0));
							cluster = (CnSCluster)CnSEventManager.handleMessage(ev, true);
							annots = (Vector<CnSAnnotationClusterPValue>)table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(row), -5);
							for (CnSAnnotationClusterPValue annot : annots) {
								cca = new CnSClusterAnnotation(annot.getAnnotation().getValue());
								cluster.addAnnotation(cca);
							}
						}
					}
				}
				else {
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					part = (CnSPartition)CnSEventManager.handleMessage(ev, true);
					if (part != null) {
						ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER, this.getClass());
						ev.addParameter(CnSPartitionManager.CLUSTER_ID, Integer.valueOf(annotateButton.getActionCommand()));
						ev.addParameter(CnSPartitionManager.PARTITION, part);
						cluster = (CnSCluster)CnSEventManager.handleMessage(ev, true);
						annots = (Vector<CnSAnnotationClusterPValue>)table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(table.getTable().getSelectedRow()), -5);
						for (CnSAnnotationClusterPValue annot : annots) {
							cca = new CnSClusterAnnotation(annot.getAnnotation().getValue());
							cluster.addAnnotation(cca);
						}
					}
				}
				table.fireTableDataChanged();
			}
		});
		deannotateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev;
				CnSCluster cluster;
				Vector<CnSAnnotationClusterPValue> annots;
				CnSClusterAnnotation cca;
				CnSPartition part;
				if (deannotateButton.getActionCommand().equals("All")) {
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					part = (CnSPartition)CnSEventManager.handleMessage(ev, true);
					if (part != null)
						for (int row = 0; row < table.getTable().getRowSorter().getViewRowCount(); row++) {
							ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER, this.getClass());
							ev.addParameter(CnSPartitionManager.CLUSTER_ID, table.getFixedTable().getValueAt(row, 0));
							ev.addParameter(CnSPartitionManager.PARTITION, part);
							cluster = (CnSCluster)CnSEventManager.handleMessage(ev, true);
							annots = (Vector<CnSAnnotationClusterPValue>)table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(row), -5);
							for (CnSAnnotationClusterPValue annot : annots) {
								cca = new CnSClusterAnnotation(annot.getAnnotation().getValue());
								cluster.removeAnnotation(cca);
							}
						}
				}
				else {
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					part = (CnSPartition)CnSEventManager.handleMessage(ev, true);
					if (part != null) {
						ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER, this.getClass());
						ev.addParameter(CnSPartitionManager.CLUSTER_ID, Integer.valueOf(annotateButton.getActionCommand()));
						ev.addParameter(CnSPartitionManager.PARTITION, part);
						cluster = (CnSCluster)CnSEventManager.handleMessage(ev, true);
						annots = (Vector<CnSAnnotationClusterPValue>)table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(table.getTable().getSelectedRow()), -5);
						for (CnSAnnotationClusterPValue annot : annots) {
							cca = new CnSClusterAnnotation(annot.getAnnotation().getValue());
							cluster.removeAnnotation(cca);
						}
					}
				}
				table.fireTableDataChanged();
			}
		});
		hideSmallClustersCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println(hideSmallClustersCheckbox.isSelected());
				CnSEvent ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL, this.getClass());
				if (partition != null) ev.addParameter(CnSPartitionPanel.PARTITION, partition);
				CnSEventManager.handleMessage(ev, true);
				ev = new CnSEvent(CnSPartitionPanel.FIRE_TABLE_DATA_CHANGED, CnSEventManager.PARTITION_PANEL, this.getClass());
				CnSEventManager.handleMessage(ev, true);
			}
		});
	}
	public int getSelectedStat() {
		return statList.getSelectedIndex();
	}
	public String getSelectedStatName() {
		return statList.getSelectedItem().toString();
	}
	public void fireTableDatachanged() {
		table.fireTableDataChanged();
	}
	public void init(final CnSPartition partition) {
		this.partition = partition;
		CnSPartitionTableModel model = new CnSPartitionTableModel(partition);
		table.setModel(model);
		//((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).setToolTipText("toto");
    	TableRowSorter<CnSPartitionTableModel> sorter = new TableRowSorter<CnSPartitionTableModel>(model);
		sorter.setMaxSortKeys(1);
		table.getTable().setRowSorter(sorter);
		table.getFixedTable().setRowSorter(sorter);
		setColumnsWidth();
		table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
		
		RowFilter<CnSPartitionTableModel,Integer> clusterFilter = new RowFilter<CnSPartitionTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSPartitionTableModel, ? extends Integer> entry) {
				boolean b = hideSmallClustersCheckbox.isSelected();
				if (!b) 
					return true;
				else {
					CnSCluster cluster = partition.getClusters().elementAt(entry.getIdentifier());
					if (cluster == null) return false;
					return cluster.getNbNodes() > 4;
				}
			}
		};
		sorter.setRowFilter(clusterFilter);
		sorter.setMaxSortKeys(1);
		setColumnsWidth();
		refresh();
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		FontMetrics headerFontMetrics = table.getTable().getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < table.getTable().getColumnModel().getColumnCount(); col++) {
			pWidth = headerFontMetrics.stringWidth(table.getTable().getModel().getColumnName(col + 1)) + table.getTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = table.getTable().getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < table.getTable().getRowCount(); row++)
				pWidth = Math.max(pWidth, table.getTable().prepareRenderer(table.getTable().getDefaultRenderer(table.getModel().getColumnClass(col + 1)), row, col).getPreferredSize().width + 10 + table.getTable().getIntercellSpacing().width);
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		}
		
		pWidth = headerFontMetrics.stringWidth(table.getFixedTable().getColumnName(0)) + table.getFixedTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
		pWidth = Math.max(pWidth,  minWidth);
		tc = table.getFixedTable().getColumnModel().getColumn(0);
		tc.setMinWidth(minWidth);
		tc.setMaxWidth(maxWidth);
		for (int row = 0; row < table.getFixedTable().getRowCount(); row++)
			pWidth = Math.max(pWidth, table.getFixedTable().prepareRenderer(table.getFixedTable().getDefaultRenderer(table.getFixedTable().getColumnClass(0)), row, 0).getPreferredSize().width + 10 + table.getFixedTable().getIntercellSpacing().width);
        pWidth = Math.min(pWidth, maxWidth);
        tc.setPreferredWidth(pWidth);
	}
	
	public void selectCluster(CnSCluster cluster) {
		selectedCluster = cluster;
		if (cluster == null) {
			table.getTable().getSelectionModel().clearSelection();
		}
		else {
			int index = 0;
			for (index = 0; index < table.getModel().getRowCount(); index++)
				if (Integer.valueOf(cluster.getID()).toString().equals(table.getModel().getValueAt(index, 0).toString())) {
					break;
				}
			if (index < table.getModel().getRowCount()) {
				int viewRow = table.getTable().convertRowIndexToView(index);
				if (viewRow != -1) {
					table.getTable().setRowSelectionInterval(viewRow, viewRow);
					table.getTable().scrollRectToVisible(table.getTable().getCellRect(viewRow, 0, true));
				}
				else {
					table.getTable().getSelectionModel().clearSelection();
					table.getTable().scrollRectToVisible(table.getTable().getCellRect(0, 0, true));
				}
				repaintTable();
			}
		}
	}
	
	public void setSelectedAnnotation(CnSNodeAnnotation annotation) {
		table.setSelectedAnnotation(annotation);
		table.fireTableDataChanged();
		repaintTable();
	}
	public CnSNodeAnnotation getSelectedAnnoselectedAnnotationtation() {
		return table.getSelectedAnnotation();
	}
	
	public void clear() {
		table.getTable().setModel(new DefaultTableModel());
		table.clear();
		table.getTable().doLayout();
		table.getTable().repaint();
		selectedCluster = null;
	}
	public void repaintTable() {
		table.getTable().repaint();
		table.getFixedTable().repaint();
	}
	public void refresh() {
		table.fireTableDataChanged();
		table.getTable().doLayout();
		table.getTable().repaint();
		table.getFixedTable().repaint();
		//splitPane.setDividerLocation(this.getWidth() - commandPanel.getPreferredSize().width -5);
		System.err.println("divider location = " + (this.getWidth() - commandPanel.getPreferredSize().width));
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSCluster getSelectedCluster() {
		return selectedCluster;
	}

	/**
	 * 
	 * @param
	 * @return
	 * @throws IOException 
	 */
	public void write(BufferedWriter br) throws IOException {
		Object value;
		if (table.getModel() != null) {
			for (int col = 0; col < table.getModel().getColumnCount(); col++) {
				br.write(table.getModel().getColumnName(col));
				br.write("\t");
			}
			br.newLine();
			for (int row = 0; row < table.getTable().getRowSorter().getViewRowCount(); row++) {
				for (int col = 0; col < table.getTable().getModel().getColumnCount(); col++) {
					value = table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(row), col);
					/*if (col == 5) {
						Vector<CnSAnnotationClusterPValue> v = (Vector<CnSAnnotationClusterPValue>)value;
						br.write(String.valueOf(v.size()));
					}
					else*/
						br.write(value.toString());
					br.write("\t");
				}
				br.newLine();
			}
		}
	}
	
	public int getCurrentThreshold() {
		return (Integer)thresholdSpinner.getValue();
	}
	
	public boolean hideSmallClusters() {
		return hideSmallClustersCheckbox.isSelected();
	}
}
