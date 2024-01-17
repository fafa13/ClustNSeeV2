/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 31 juil. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationanalysis;

import java.awt.Dimension;
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
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

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
import org.cytoscape.clustnsee3.internal.gui.util.search.CnSSearchAnnotationComponent;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSAnnotationAnalysisPanel extends CnSPanelSplitCommand {
	private static final long serialVersionUID = 7516097716357051453L;
	private CnSSearchAnnotationComponent annotationSearchComponent;
	private CnSButton clearButton;
	private ImageIcon icon_delete;
	private CnSAnnotationTable table;
	private CnSButton exportDataButton;
	
	public CnSAnnotationAnalysisPanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void setSelectedAnnotation(CnSNodeAnnotation annotation) {
		if (annotation != null) System.err.println("CnSAnnotationAnalysisPanel.setSelectedAnnotation : " + annotation.getValue());
		table.setSelectedAnnotation(annotation);
		table.fireTableDataChanged();
		repaintTable();
	}
	private void initListeners() {
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				annotationSearchComponent.getTextField().setText("");
				annotationSearchComponent.searchForAnnotation();
			}
		});
		table.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL, this.getClass());
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
		                cluster = ((CnSAnnotationTableModel)table.getTable().getModel()).getCluster(modelRow);
		            }
		    		if (cluster != null) {
		    			ev.addParameter(CnSResultsPanel.CLUSTER_NAME, Integer.parseInt(cluster.getName()));
		    			CnSEventManager.handleMessage(ev, true);
		    		}
		    		ev = new CnSEvent(CnSPartitionPanel.INIT_ANNOTATION_PANEL, CnSEventManager.PARTITION_PANEL, this.getClass());
		    		if (cluster != null) ev.addParameter(CnSPartitionPanel.CLUSTER, cluster);
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
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.EXPORT_ANNOTATION_TERM_ANALYSIS_DATA, CnSEventManager.PARTITION_PANEL, this.getClass());
					ev.addParameter(CnSPartitionPanel.OUTPUT_FILE, file);
					CnSEventManager.handleMessage(ev, true);
				}
			}
		});
	}

	public void initGraphics() {
		CnSPanel annotationsPanel = new CnSPanel();
		annotationsPanel.setBorder(BorderFactory.createEtchedBorder());
		annotationSearchComponent = new CnSSearchAnnotationComponent(CnSPartitionPanel.SEARCH, CnSEventManager.PARTITION_PANEL, CnSPartitionPanel.ANNOTATION);
		annotationsPanel.addComponent(new JLabel("Focus on annotation :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
		annotationSearchComponent.getTextField().setColumns(20);
		annotationsPanel.addComponent(annotationSearchComponent.getTextField(), 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		icon_delete = new ImageIcon(getClass().getResource("/org/cytoscape/clustnsee3/internal/resources/delete_annotation.gif"));
		clearButton = new CnSButton(icon_delete);
		clearButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 6, icon_delete.getIconHeight()));
		clearButton.setFocusable(false);
		annotationsPanel.addComponent(clearButton, 2, 0, 1, 1, 0.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		//addComponent(annotationsPanel, 0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 0, 0, 0, 0);
		
		exportDataButton = new CnSButton("Export data");
		//addComponent(exportDataButton, 1, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 0, 5, 0, 0);
		
		table = new CnSAnnotationTable();
		//addComponent(table.getScrollPane(), 0, 1, 2, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		
		table.getTable().setTableHeader(new JTableHeader(table.getTable().getColumnModel()) {
			private static final long serialVersionUID = -7453185345738046988L;

			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
				ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, false);
				rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					case 0 : return rBundle.getString("CnSAnnotationTableModel.annotatedNodes_MO");
					case 1 : return rBundle.getString("CnSAnnotationTableModel.annotatedNodesPercent_MO");
					case 2 : return rBundle.getString("CnSAnnotationTableModel.selectedAnnotationPhyper_MO");
					case 3 : return rBundle.getString("CnSAnnotationTableModel.selectedAnnotationMajorityPercent_MO");
				}
				return null;
			}
		});
		table.getTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		table.getFixedTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		commandPanel = new CnSPanel();
		commandPanel.addComponent(annotationsPanel, 0, 0, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		commandPanel.addComponent(exportDataButton, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 5, 5, 5, 5, 0, 0);
		
		initGraphics(commandPanel, table.getScrollPane());
	}
	public void init(CnSPartition partition) {
		CnSAnnotationTableModel model = new CnSAnnotationTableModel(partition);
		table.setModel(model);
		((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).setToolTipText("toto");
    	

		RowFilter<CnSAnnotationTableModel,Integer> annotationFilter = new RowFilter<CnSAnnotationTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSAnnotationTableModel, ? extends Integer> entry) {
				CnSAnnotationTableModel model = entry.getModel();
				CnSCluster cluster = model.getCluster(entry.getIdentifier());
				CnSEvent ev = new CnSEvent(CnSPartitionPanel.GET_HIDE_SMALL_CLUSTERS, CnSEventManager.PARTITION_PANEL, this.getClass());
				boolean b = (Boolean)CnSEventManager.handleMessage(ev, false);
				if (b && cluster.getNbNodes() < 5) return false;
				if (model.getSelectedAnnotation() == null) return true;
				ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
				Vector<?> clusterAnnotations = (Vector<?>)CnSEventManager.handleMessage(ev, false);
				return clusterAnnotations.contains(model.getSelectedAnnotation());
			}
		};
		TableRowSorter<CnSAnnotationTableModel> sorter = new TableRowSorter<CnSAnnotationTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		table.getTable().setRowSorter(sorter);
		table.getFixedTable().setRowSorter(sorter);
		setColumnsWidth();
		table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
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
			tc.setPreferredWidth(pWidth);//if (columnIndex == 5) return CnSEnrichmentStatValue.class;
		    //System.err.println("colmun = " + table.getTable().getModel().getColumnName(col + 1) + " ; id = " + col + " ; pwidth = " + pWidth);
		}
		
		pWidth = headerFontMetrics.stringWidth(table.getFixedTable().getColumnName(0)) + table.getFixedTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
		pWidth = Math.max(pWidth,  minWidth);
		tc = table.getFixedTable().getColumnModel().getColumn(0);
		tc.setMinWidth(minWidth);
		tc.setMaxWidth(maxWidth);
		for (int row = 0; row < table.getFixedTable().getRowCount(); row++)
			pWidth = Math.max(pWidth, table.getFixedTable().prepareRenderer(table.getFixedTable().getDefaultRenderer(table.getFixedTable().getColumnClass(0)), row, 0).getPreferredSize().width + 10 + table.getFixedTable().getIntercellSpacing().width);
        pWidth = Math.min(pWidth, maxWidth);
        //System.err.println("colmun = " + table.getFixedTable().getModel().getColumnName(0) + " ; " + "pwidth = " + pWidth);
		tc.setPreferredWidth(pWidth);
	}
	public void clear() {
		table.getTable().setModel(new DefaultTableModel());
		table.clear();
		table.getTable().doLayout();
		table.getTable().repaint();
		
	}
	public void repaintTable() {
		table.getTable().repaint();
		table.getFixedTable().repaint();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSSearchAnnotationComponent getSearchComponent() {
		return annotationSearchComponent;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSNodeAnnotation getSelectedAnnotation() {
		return table.getSelectedAnnotation();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void write(BufferedWriter br) {
		if (table.getModel() != null) {
			try {
				for (int col = 0; col < table.getModel().getColumnCount(); col++) {
					br.write(table.getModel().getColumnName(col));
					br.write("\t");
				}
				br.newLine();
				if (table.getTable().getRowSorter() != null) {
					for (int row = 0; row < table.getTable().getRowSorter().getViewRowCount(); row++) {
						for (int col = 0; col < table.getModel().getColumnCount(); col++) {
							br.write(table.getModel().getValueAt(table.getTable().getRowSorter().convertRowIndexToModel(row), col).toString());
							br.write("\t");
						}
						br.newLine();
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void selectCluster(CnSCluster cluster) {
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
	
	public void fireTableDataChanged() {
		table.fireTableDataChanged();
		repaintTable();
	}
}
