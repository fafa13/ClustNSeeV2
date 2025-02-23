/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSComparePartitionsResultDialog extends JDialog {
	private static final long serialVersionUID = 663921405674284603L;
	private JLabel jaccardLabel;
	private JTable contingencyTable;
	private CnSContingencyTableModel contingencyTableModel;
	private CnSButton closeButton;
	
	public CnSComparePartitionsResultDialog(CnSPartition part1, CnSPartition part2, Vector<Vector<Integer>> data) {
		super();
		contingencyTableModel = new CnSContingencyTableModel(data, part1, part2);
		initGraphics(part1.toString(), part2.toString(), data);
		initListeners();
	}
	
	private void initGraphics(String part1Name, String part2Name, Vector<Vector<Integer>> data) {
		setTitle("Compare partitions " + part1Name + " and " + part2Name);
		CnSPanel mainPanel = new CnSPanel();
		CnSPanel indexesPanel = new CnSPanel();
		CnSPanel contingencyPanel = new CnSPanel();
		indexesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Comparison indexes"));
		mainPanel.addComponent(indexesPanel, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 30, 30);
		contingencyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Contingency table of " + part1Name + " (columns) vs " + part2Name + " (rows)"));
		mainPanel.addComponent(contingencyPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 5, 5, 0, 5, 30, 30);
		closeButton = new CnSButton("Close");
		mainPanel.addComponent(closeButton, 0, 2, 1, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		indexesPanel.addComponent(new JLabel("Jaccard index :"), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 20, 10, 10, 00, 0, 0);
		jaccardLabel = new JLabel();
		indexesPanel.addComponent(jaccardLabel, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 20, 10, 10, 10, 0, 0);
		
		contingencyTable = new JTable(contingencyTableModel);
		contingencyTable.setDefaultRenderer(Integer.class, new DataCellRenderer());
		contingencyTable.getColumnModel().getColumn(0).setCellRenderer(new RowHeaderCellRenderer());
		contingencyTable.setCellSelectionEnabled(true);
		contingencyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contingencyTable.setShowGrid(true);
		contingencyPanel.addComponent(new JScrollPane(contingencyTable), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 20, 10, 10, 10, 0, 0);

		getContentPane().add(mainPanel);
	}
	
	private void initListeners() {
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
	
	private class RowHeaderCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -8690657045355086626L;
		
		/**
		 * @param
		 * @return
		 */
		public RowHeaderCellRenderer() {
			super();
			setHorizontalAlignment(JLabel.CENTER);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
				setForeground(Color.red);
			}
			
			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setOpaque(true);
			return this;
		}	
	}
	
	private class DataCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -2476241713017411985L;

		/**
		 * @param
		 * @return
		 */
		public DataCellRenderer() {
			super();
			setHorizontalAlignment(SwingConstants.CENTER);
		    setBackground(Color.white);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (((Integer)value).intValue() > 0)
		        setFont(getFont().deriveFont(Font.BOLD));
		    else
		        setFont(getFont().deriveFont(Font.PLAIN));
		    if (isSelected)
		    	setForeground(Color.blue);
		    else
		    	setForeground(Color.black);
		    setText((value == null) ? "" : value.toString());
			
		    return this;
		}	
	}
}
