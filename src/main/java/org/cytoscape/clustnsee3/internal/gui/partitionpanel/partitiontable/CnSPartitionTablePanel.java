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

//import java.awt.Dimension;
import java.awt.FontMetrics;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JLabel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
//import org.cytoscape.clustnsee3.internal.gui.util.search.CnSSearchAnnotationComponent;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
//import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSPartitionTablePanel extends CnSPanel {
	private static final long serialVersionUID = 4538284737604540732L;
	
	//private CnSSearchAnnotationComponent annotationSearchComponent;
	private CnSPartitionTable table;
	//private CnSButton exportDataButton;
	//private CnSButton clearButton;
	//private ImageIcon icon_delete;
	private CnSCluster selectedCluster;
	
	public CnSPartitionTablePanel() {
		super();
		initGraphics();
		initListeners();
		selectedCluster = null;
	}
	
	public void initGraphics() {
//		annotationSearchComponent = new CnSSearchAnnotationComponent(CnSPartitionPanel.SEARCH, CnSEventManager.PARTITION_PANEL, CnSPartitionPanel.ANNOTATION);
//		addComponent(new JLabel("Focus on annotation :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
//		addComponent(annotationSearchComponent.getTextField(), 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 0, 5, 0, 5, 0, 0);
//		icon_delete = new ImageIcon(getClass().getResource("/org/cytoscape/clustnsee3/internal/resources/delete_annotation.gif"));
//		clearButton = new CnSButton(icon_delete);
//		clearButton.setPreferredSize(new Dimension(icon_delete.getIconWidth(), icon_delete.getIconHeight()));
//		clearButton.setFocusable(false);
//		
//		addComponent(clearButton, 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
//		
		table = new CnSPartitionTable();
		addComponent(table.getScrollPane(), 0, 1, 3, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		
		table.getTable().setTableHeader(new JTableHeader(table.getTable().getColumnModel()) {
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR);
				ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev);
				rBundle = CyActivator.getResourcesBundle();
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
//		CnSPanel commandPanel = new CnSPanel();
//		commandPanel.setBorder(BorderFactory.createEtchedBorder());
//		exportDataButton = new CnSButton("Export data");
//		commandPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
//		addComponent(commandPanel, 0, 2, 3, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
	}
	
	private void initListeners() {
//		exportDataButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.LOOK_FOR_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
//				ev.addParameter(CnSNodeAnnotationManager.PREFIX, "GO:0012");
//				Vector<?> ret = (Vector<?>)CnSEventManager.handleMessage(ev);
//				for (Object s : ret) System.err.println(s.toString());
//			}
//		});
//		clearButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				annotationSearchComponent.getTextField().setText("");
//				annotationSearchComponent.searchForAnnotation();
//			}
//		});
		table.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
		    	CnSCluster cluster = null;
		    	int selectedRow = -1;
		    	if (!e.getValueIsAdjusting()) {
		    		if (table.getTable().isRowSelected(e.getLastIndex()))
		    			selectedRow = e.getLastIndex();
		    		else if (table.getTable().isRowSelected(e.getFirstIndex()))
		    			selectedRow = e.getFirstIndex();
		    		selectedRow = table.getTable().getSelectedRow();
		    		System.err.println("CnSPartitionTablePanel.valueChanged : ");
		    		
		    		if (selectedRow != -1) {
		    			int modelRow = table.getTable().convertRowIndexToModel(selectedRow);
		                cluster = ((CnSPartitionTableModel)table.getTable().getModel()).getCluster(modelRow);
		                System.err.println("  selected row = " +  selectedRow);
		                System.err.println("  model row = " +  modelRow);
		                System.err.println("  cluster name of selected row= " + ((CnSPartitionTableModel)table.getTable().getModel()).getCluster(selectedRow).getName());
		    		}
		    		if (cluster != null) {
		    			System.err.println("  cluster name = " + cluster.getName());
		    			System.err.println("  source = " + e.getSource());
		    			System.err.println("  first index = " + e.getFirstIndex());
		    			System.err.println("  last index = " + e.getLastIndex());
		    			ev.addParameter(CnSResultsPanel.CLUSTER_NAME, Integer.parseInt(cluster.getName()));
		    			CnSEventManager.handleMessage(ev);
		    		}
		    		ev = new CnSEvent(CnSPartitionPanel.INIT_ANNOTATION_PANEL, CnSEventManager.PARTITION_PANEL);
		    		if (cluster != null)ev.addParameter(CnSPartitionPanel.CLUSTER, cluster);
		    		CnSEventManager.handleMessage(ev);
		    	}
		    	else {
		    		System.err.println("CnSPartitionTablePanel.valueIsAdjusting : ");
		    		System.err.println("  selected row = " + table.getTable().getSelectedRow());
		    	}
		    }
		});
	}
	
	public void init(CnSPartition partition) {
		System.err.println("CnSPartitionTablePanel.init(" + partition.getName() + ")");
		CnSPartitionTableModel model = new CnSPartitionTableModel(partition);
		table.setModel(model);
		System.err.println("Header renderer = " + table.getTable().getTableHeader().getDefaultRenderer());
		((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).setToolTipText("toto");
    	//((JLabel)table.getTable().getColumnModel().getColumn(0).getHeaderRenderer()).setToolTipText("toto");


		RowFilter<CnSPartitionTableModel,Integer> annotationFilter = new RowFilter<CnSPartitionTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSPartitionTableModel, ? extends Integer> entry) {
				CnSPartitionTableModel model = entry.getModel();
				if (model.getSelectedAnnotation() == null) return true;
				CnSCluster cluster = model.getCluster(entry.getIdentifier());
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
				Vector<?> clusterAnnotations = (Vector<?>)CnSEventManager.handleMessage(ev);
				return clusterAnnotations.contains(model.getSelectedAnnotation());
			}
		};
		TableRowSorter<CnSPartitionTableModel> sorter = new TableRowSorter<CnSPartitionTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		table.getTable().setRowSorter(sorter);
		table.getFixedTable().setRowSorter(sorter);
		sorter.addRowSorterListener(new RowSorterListener() {
			
			@Override
			public void sorterChanged(RowSorterEvent e) {
				System.err.println("SORTING : " + e.getType());
				if (e.getType().equals(RowSorterEvent.Type.SORTED)) {
					//selectCluster(selectedCluster);
					System.err.println("..... " + table.getTable().getSelectedRow());
				}
				else if (e.getType().equals(RowSorterEvent.Type.SORT_ORDER_CHANGED)) {
					System.err.println("..... " + table.getTable().getSelectedRow());
				}
			}
		});
		setColumnsWidth();
		table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		System.err.println("CnSPartitionTablePanel.setColumnsWidth");
		FontMetrics headerFontMetrics = table.getTable().getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < table.getModel().getColumnCount() - 1; col++) {
			pWidth = headerFontMetrics.stringWidth(table.getModel().getColumnName(col)) + table.getTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = table.getTable().getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < table.getModel().getRowCount(); row++)
				pWidth = Math.max(pWidth, table.getTable().prepareRenderer(table.getTable().getDefaultRenderer(table.getModel().getColumnClass(col)), row, col).getPreferredSize().width + 10 + table.getTable().getIntercellSpacing().width);
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		}
		
		pWidth = headerFontMetrics.stringWidth(table.getFixedTable().getColumnName(0)) + table.getFixedTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
		pWidth = Math.max(pWidth,  minWidth);
		tc = table.getFixedTable().getColumnModel().getColumn(0);
		tc.setMinWidth(minWidth);
		tc.setMaxWidth(maxWidth);
		for (int row = 0; row < table.getFixedTable().getModel().getRowCount(); row++)
			pWidth = Math.max(pWidth, table.getFixedTable().prepareRenderer(table.getFixedTable().getDefaultRenderer(table.getFixedTable().getColumnClass(0)), row, 0).getPreferredSize().width + 10 + table.getFixedTable().getIntercellSpacing().width);
        pWidth = Math.min(pWidth, maxWidth);
		tc.setPreferredWidth(pWidth);
	}
	
	public void selectCluster(CnSCluster cluster) {
		selectedCluster = cluster;
		System.err.println("CnSPartitionTablePanel.selectCluster : " + cluster);
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
				System.err.println("CnSPartitionTablePanel.selectCluster : " + viewRow);
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
		if (annotation != null) System.err.println("CnSPartitionTablePanel.setSelectedAnnotation : " + annotation.getValue());
		table.setSelectedAnnotation(annotation);
		table.fireTableDataChanged();
		repaintTable();
	}
	public CnSNodeAnnotation getSelectedAnnotation() {
		return table.getSelectedAnnotation();
	}
//	public void setAnnotation(CnSNodeAnnotation annot) {
//		System.err.println("CnSPartitionTablePanel.setAnnotation : " + annot.getValue());
//		annotationSearchComponent.setAnnotation(annot);
//	}
	
	public void clear() {
		System.err.println("CnSPartitionTablePanel.clear");
		table.getTable().setModel(new DefaultTableModel());
		table.clear();
		table.getTable().doLayout();
		table.getTable().repaint();
		selectedCluster = null;
	}
	public void repaintTable() {
		System.err.println("CnSPartitionTablePanel.repaintTable");
		table.getTable().repaint();
		table.getFixedTable().repaint();
	}
	public void refresh() {
		System.err.println("CnSPartitionTablePanel.refresh");
		table.fireTableDataChanged();
		table.getTable().doLayout();
		table.getTable().repaint();
		table.getFixedTable().repaint();
	}
	
//	public String getSearchedAnnotation() {
//		return annotationSearchComponent.getText();
//	}

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
		for (int col = 0; col < table.getModel().getColumnCount(); col++) {
			br.write(table.getModel().getColumnName(col));
			br.write("\t");
		}
		br.newLine();
		for (int row = 0; row < table.getModel().getRowCount(); row++) {
			if (table.getTable().getRowSorter().convertRowIndexToView(row) != -1) {
				for (int col = 0; col < table.getModel().getColumnCount(); col++) {
					br.write(table.getModel().getValueAt(row, col).toString());
					br.write("\t");
				}
				br.newLine();
			}
		}
	}
}
