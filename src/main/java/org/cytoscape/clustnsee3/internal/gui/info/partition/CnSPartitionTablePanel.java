/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionProperty;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSPartitionTablePanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -3877080938361953871L;
	private static CnSPartitionTablePanel instance;
	private CnSPartitionTable table;
	private CnSButton importAnnotationButton, addAnnotationColumnButton, exportDataButton;
	
	public static final int INIT = 1;
	public static final int CLEAR = 2;
	
	public static final int PARTITION = 1001;
	
	public static CnSPartitionTablePanel getInstance() {
		if (instance == null)
			instance = new CnSPartitionTablePanel("Partition table");
		return instance;
	}
	
	private CnSPartitionTablePanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	protected void initGraphics() {
		table = new CnSPartitionTable();
		addComponent(table.getScrollPane(), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 0, 0, 0, 0, 0, 0);
		table.getTable().getTableHeader().setDefaultRenderer(new CnSPartitionTableHeaderRenderer());
		CnSPanel commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		importAnnotationButton = new CnSButton("Import annotation");
		commandPanel.addComponent(importAnnotationButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		addAnnotationColumnButton = new CnSButton("Add annotation column");
		commandPanel.addComponent(addAnnotationColumnButton, 1, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 2, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(commandPanel, 0, 1, 1, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 0, 0, 0, 0, 0);
	}
	
	private void initListeners() {
		importAnnotationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				if (partition != null) {
					JFileChooser jfc = new JFileChooser();
					jfc.addChoosableFileFilter(new FileNameExtensionFilter("Annotation file", "annot"));
					int ret = jfc.showOpenDialog(null);
					boolean toload = false;
					String s;
					File file = null;
					
					if (ret == JFileChooser.APPROVE_OPTION) {
						toload = true;
						file = jfc.getSelectedFile();
						if (!file.exists()) {
							JOptionPane.showMessageDialog(null, "The file you have selected doest not exist !", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
							toload = false;
						}	
					}
					if (toload) {
						try {
							System.err.println("Importing annotations from " + file.getName());
							
							BufferedReader br= new BufferedReader(new FileReader(file));
							Set<CyNode> n;
						
							while ((s = br.readLine()) != null) {
								if (s.startsWith(">")) {
									String[] words = s.split("\t");
									table.getModel().addAnnotation(new CnSPartitionProperty<Integer>(partition, words[0].substring(1)));
									
								}
								else if (!s.equals("")) {
									
								}
							}
							br.close();
							table.fireTableDataChanged();
						}
						catch (FileNotFoundException ex) {
							ex.printStackTrace();
						} 
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		addAnnotationColumnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				FisherExact fe = new FisherExact(1000);
				double p = fe.getCumlativeP(88, 2, 904, 6);
				System.err.println("Fisher = " + p);
			}
			
		});
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		switch (event.getAction()) {
			case INIT :
				CnSPartition partition = (CnSPartition)event.getParameter(PARTITION);
				CnSPartitionTableModel model = new CnSPartitionTableModel(partition);
				table.setModel(model);
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Nb. nodes"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Intra cluster edges"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Extra cluster edges"));
				model.addAnnotation(new CnSPartitionProperty<Double>(partition, "Intra/extra edges ratio"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Mono-clustered nodes"));
				model.addAnnotation(new CnSPartitionProperty<Integer>(partition, "Multi-clustered nodes"));
		    	
		    	int preferredWidth = 0;
		    	TableCellRenderer r0 = table.getFixedTable().getColumnModel().getColumn(0).getCellRenderer();
				for (int row = 0; row < table.getFixedTable().getRowCount(); row++) {
					Component c = r0.getTableCellRendererComponent(table.getFixedTable(), model.getValueAt(row, 0), true, false, row, 0);
				    int width = c.getPreferredSize().width + table.getFixedTable().getIntercellSpacing().width + 5;
				    preferredWidth = Math.max(preferredWidth, width);
				}
				table.getFixedTable().getColumnModel().getColumn(0).setPreferredWidth(preferredWidth);
				table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
				break;
			
			case CLEAR :
				table.getModel().clear();
				table.getTable().setModel(new DefaultTableModel());;
				table.clear();
				table.getTable().doLayout();
				table.getTable().repaint();
				break;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getIcon()
	 */
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
