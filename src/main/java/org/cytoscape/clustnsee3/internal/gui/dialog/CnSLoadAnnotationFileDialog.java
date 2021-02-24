/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 déc. 2020
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSLoadAnnotationFileDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -906592512029403037L;
	
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	
	class DataTableCellRenderer extends DefaultTableCellRenderer {
	    private static final long serialVersionUID = 6794360191485492806L;
	    private DefaultTableCellRenderer renderer;

	    public DataTableCellRenderer(JTable table) {
	    	super();
	    	renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(String.class);
	    }
	    
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (!isSelected)
	        	if ((row + 1) < getFromLine()) 
	        		c.setForeground(Color.LIGHT_GRAY);
	        	else
	        		c.setForeground(Color.BLACK);
	        return c;
	    }
	}

	class DataTableHeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -5481419485529001820L;
		private DefaultTableCellRenderer renderer;

		public DataTableHeaderRenderer(JTable table) {
			super();
			renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
		}
		
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if ((column == ((Integer)nodeColSpinner.getValue()).intValue()) || (column == ((Integer)annColSpinner.getValue()).intValue())) {
	        	c.setForeground(Color.BLUE);
	        	c.setFont(c.getFont().deriveFont(Font.BOLD));
	        }
	        return c;
	    }
	}
	
	class TM extends AbstractTableModel {
		private static final long serialVersionUID = 7914092055267256875L;
		private int nb_col;
		private Vector<String> data;
		
		public TM() {
			super();
			data = new Vector<String>();
			nb_col = 0;
		}
		public void setNbCol(int nc) {
			nb_col = nc;
		}
		public void addData(String s) {
			data.addElement(s);
		}
		public void clear() {
			data.clear();
		}
		public String getData(int i) {
			return data.elementAt(i);
		}
		public int getNbCol() {
			return nb_col;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int col) {
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return nb_col + 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int col) {
			if (col > 0)
				return "Col #" + String.valueOf(col);
			return "";
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return data.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int column) {
			if (column > 0) {
				String[] word = data.elementAt(row).split(colTabRadioButton.isSelected()?"\t":(colComRadioButton.isSelected()?",":(colSemRadioButton.isSelected()?";":" ")));
				if (word.length > column - 1) return word[column - 1];
				return "";
			}
			return String.valueOf(row + 1);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
	
	private JTextField fileTextField;
	private CnSButton fileButton, okButton, cancelButton, clearButton;
	private File currentDirectory;
	private JRadioButton colTabRadioButton, colComRadioButton, colSemRadioButton, colSpaRadioButton, annTabRadioButton, annComRadioButton, annSemRadioButton, annSpaRadioButton;
	private JSpinner lineSpinner, nodeColSpinner, annColSpinner;
	
	private JTable dataTable;
	private TM tm;
	
	private int exit_option;
	private File file = null;
	private DataTableCellRenderer cellRenderer;
	private DataTableHeaderRenderer headerRenderer;
	
	private static CnSLoadAnnotationFileDialog instance = null;
	private int NB_LINE_READ = 1000;
	
	public static CnSLoadAnnotationFileDialog getInstance() {
		if (instance == null) 
			instance = new CnSLoadAnnotationFileDialog();
		else
			instance.setExitOption(1);
		return instance;
	}
	
	private CnSLoadAnnotationFileDialog() {
		super();
		setModal(true);
		currentDirectory = null;
		tm = new TM();
		exit_option = CANCEL_OPTION;
		initGraphics();
		initListeners();
	}
	
	private void initGraphics() {
		CnSPanel mainPanel = new CnSPanel();
		setContentPane(mainPanel);
		setTitle("Load annotation file");
		
		CnSPanel filePanel = new CnSPanel("File name");
		filePanel.initGraphics();
		fileTextField = new JTextField(30);
		filePanel.addComponent(fileTextField, 1, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 5, 5, 5, 0, 0, 0);
		ImageIcon icon = new ImageIcon(getClass().getResource("/open.gif"));
		fileButton = new CnSButton(icon);
		filePanel.addComponent(fileButton, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(filePanel, 0, 0, 2, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		CnSPanel colSepPanel = new CnSPanel("Column separator");
		colSepPanel.initGraphics();
		ButtonGroup bg = new ButtonGroup();
		colTabRadioButton = new JRadioButton("tabulation", true);
		bg.add(colTabRadioButton);
		colSepPanel.addComponent(colTabRadioButton, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		colComRadioButton = new JRadioButton("comma", false);
		bg.add(colComRadioButton);
		colSepPanel.addComponent(colComRadioButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		colSemRadioButton = new JRadioButton("semicolon", false);
		bg.add(colSemRadioButton);
		colSepPanel.addComponent(colSemRadioButton, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		colSpaRadioButton = new JRadioButton("space", false);
		bg.add(colSpaRadioButton);
		colSepPanel.addComponent(colSpaRadioButton, 3, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(colSepPanel, 0, 1, 1, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		CnSPanel annSepPanel = new CnSPanel("Annotation separator");
		annSepPanel.initGraphics();
		bg = new ButtonGroup();
		annTabRadioButton = new JRadioButton("tabulation", true);
		bg.add(annTabRadioButton);
		annSepPanel.addComponent(annTabRadioButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		annComRadioButton = new JRadioButton("comma", false);
		bg.add(annComRadioButton);
		annSepPanel.addComponent(annComRadioButton, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		annSemRadioButton = new JRadioButton("semicolon", false);
		bg.add(annSemRadioButton);
		annSepPanel.addComponent(annSemRadioButton, 3, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		annSpaRadioButton = new JRadioButton("space", false);
		bg.add(annSpaRadioButton);
		annSepPanel.addComponent(annSpaRadioButton, 4, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(annSepPanel, 1, 1, 1, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 0, 0);
		
		CnSPanel impPanel = new CnSPanel();
		impPanel.addComponent(new JLabel("Start line :"), 0, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		lineSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
		impPanel.addComponent(lineSpinner, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		impPanel.addComponent(new JLabel("Node name column :"), 2, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 15, 5, 0, 0, 0);
		nodeColSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		impPanel.addComponent(nodeColSpinner, 3, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		impPanel.addComponent(new JLabel("Annotation column :"), 4, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 15, 5, 0, 0, 0);
		annColSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		impPanel.addComponent(annColSpinner, 5, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		clearButton = new CnSButton("Clear");
		impPanel.addComponent(clearButton, 6, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		dataTable = new JTable(tm);
		cellRenderer = new DataTableCellRenderer(dataTable);
		dataTable.setDefaultRenderer(String.class, cellRenderer);
		headerRenderer = new DataTableHeaderRenderer(dataTable);
		dataTable.getTableHeader().setDefaultRenderer(headerRenderer);
		impPanel.addComponent(new JScrollPane(dataTable), 0, 1, 7, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 5, 5, 5, 5, 0, 0);
		impPanel.setBorder(BorderFactory.createEtchedBorder());
		mainPanel.addComponent(impPanel, 0, 2, 2, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		
		CnSPanel commandPanel = new CnSPanel();
		okButton = new CnSButton("OK");
		commandPanel.addComponent(okButton, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		cancelButton = new CnSButton("Cancel");
		commandPanel.addComponent(cancelButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		mainPanel.addComponent(commandPanel, 0, 4, 2, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		pack();
	}
	
	private void initListeners() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit_option = CANCEL_OPTION;
				dispose();
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit_option = OK_OPTION;
				dispose();
			}
		});
		
		fileTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int nb_line;
				String s;
				String[] word;
				try {
					file = new File(fileTextField.getText());
					if (file.exists()) {
						BufferedReader br = new BufferedReader(new FileReader(file));
						tm.clear();
						tm.setNbCol(0);
						nb_line = 0;
						while ((s = br.readLine()) != null && nb_line < NB_LINE_READ) {
							tm.addData(s);
							word = s.split(colTabRadioButton.isSelected()?"\t":(colComRadioButton.isSelected()?",":(colSemRadioButton.isSelected()?";":" ")));
							tm.setNbCol(Math.max(tm.getNbCol(), word.length));
							nb_line++;
						}
						br.close();
						tm.fireTableStructureChanged();
						setColumnsWidth();
					}
					else {
						file= null;
						JOptionPane.showMessageDialog(null, "This file doest not exist !", "Unknown file", JOptionPane.ERROR_MESSAGE, null);
					}
				}
				catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Exception occured while loading file", JOptionPane.ERROR_MESSAGE, null);
					e.printStackTrace();
				}
			}
		});
		
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				JFileChooser jfc = new JFileChooser(currentDirectory);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("Annotation file", "annot"));
				int ret = jfc.showOpenDialog(null), nb_line;
				file = null;
				boolean toload = false;
				String s;
				String[] word;
	
				currentDirectory = jfc.getCurrentDirectory();
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
						fileTextField.setText(file.getCanonicalPath());
						BufferedReader br = new BufferedReader(new FileReader(file));
						tm.clear();
						tm.setNbCol(0);
						nb_line = 0;
						while ((s = br.readLine()) != null && nb_line < NB_LINE_READ) {
							tm.addData(s);
							word = s.split(colTabRadioButton.isSelected()?"\t":(colComRadioButton.isSelected()?",":(colSemRadioButton.isSelected()?";":" ")));
							tm.setNbCol(Math.max(tm.getNbCol(), word.length));
							nb_line++;
						}
						br.close();
						tm.fireTableStructureChanged();
						setColumnsWidth();
					} 
					catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Exception occured while loading file", JOptionPane.ERROR_MESSAGE, null);
						e.printStackTrace();
					}
				}
			}
		});
		lineSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg) {
				tm.fireTableStructureChanged();
				setColumnsWidth();
			}
		});
		colTabRadioButton.addActionListener(this);
		colComRadioButton.addActionListener(this);
		colSemRadioButton.addActionListener(this);
		colSpaRadioButton.addActionListener(this);
		nodeColSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg) {
				tm.fireTableStructureChanged();
				setColumnsWidth();
			}
		});
		annColSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg) {
				tm.fireTableStructureChanged();
				setColumnsWidth();
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tm.clear();
				tm.setNbCol(0);
				tm.fireTableStructureChanged();
			}
		});
	}
	
	public int getExitOption() {
		return exit_option;
	}
	
	public void setExitOption(int eo) {
		exit_option = eo;
	}
	
	public File getSelectedFile() {
		return file;	
	}
	public int getFromLine() {
		return ((Integer)lineSpinner.getValue()).intValue();
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		
		for (int col = 0; col < tm.getColumnCount() - 1; col++) {
			tc = dataTable.getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			pWidth = minWidth;
			for (int row = 0; row < tm.getRowCount(); row++)
				pWidth = Math.max(pWidth, dataTable.prepareRenderer(cellRenderer, row, col).getPreferredSize().width + dataTable.getIntercellSpacing().width);
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String[] word;
		tm.setNbCol(0);
		for (int i = 0; i < tm.getRowCount(); i++) {
			word = tm.getData(i).split(colTabRadioButton.isSelected()?"\t":(colComRadioButton.isSelected()?",":(colSemRadioButton.isSelected()?";":" ")));
			tm.setNbCol(Math.max(tm.getNbCol(), word.length));
		}
		tm.fireTableStructureChanged();
		setColumnsWidth();
	}
}
