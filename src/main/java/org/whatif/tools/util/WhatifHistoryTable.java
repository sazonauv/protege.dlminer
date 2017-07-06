package org.whatif.tools.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class WhatifHistoryTable extends JTable {

	DefaultTableModel dm = new DefaultTableModel() {
        @Override
        public Class getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Integer.class;
                case 1:
                    return ReasonerRunDiff.class;
                case 2:
                    return JRadioButton.class;
                default:
                    return String.class;
            }
        }
    };
	Object[] columns = new Object[] { "Nr.","Date", "Comp","Time" };
	final TableRowSorter<TableModel> sorter;
	List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();

	public WhatifHistoryTable() {
		super();
		dm.setColumnIdentifiers(columns);
		this.setModel(dm);
		getColumn("Comp").setCellRenderer(new RadioButtonRenderer());
		getColumn("Comp").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		sorter = new TableRowSorter<TableModel>(getModel());
		setRowSorter(sorter);
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.setSortable(0, false);
		sorter.sort();	
		
		}
	
	 public String getToolTipText(MouseEvent e) {
         java.awt.Point p = e.getPoint();
         int rowIndex = rowAtPoint(p);

        String tip = null;
         
         if ((rowIndex > -1 && rowIndex < getRowCount())) {

             tip = getValueAt(rowIndex, 1).toString();

         }
        

         return tip;
     }

	public void setHistoryRun(ReasonerRunDiff cl,JRadioButton rb) {
		int rowCount = getRowCount() + 1;
		Object[] row = new Object[] { new Integer(rowCount), cl, rb, cl.getReasoningTimeSec()};
		dm.addRow(row);		
		
		//setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getColumnModel().getColumn(0).setPreferredWidth(30);
		//getColumnModel().getColumn(0).setMaxWidth(30);
		getColumnModel().getColumn(1).setMinWidth(0);
		getColumnModel().getColumn(1).setMaxWidth(0);
		getColumnModel().getColumn(1).setPreferredWidth(0);
		getColumnModel().getColumn(2).setPreferredWidth(30);
		//getColumnModel().getColumn(2).setMaxWidth(30);
		sorter.sort();	
		sorter.setSortable(0, false);
		increaseRowHeight();
		repaint();
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		repaint();
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * @version 1.0 11/09/98
	 */

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				//
				//
				JOptionPane.showMessageDialog(button, label + ": Ouch!");
				// System.out.println(label + ": Ouch!");
			}
			isPushed = false;
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}

	public void clear() {
		dm = new DefaultTableModel();
		dm.setColumnIdentifiers(columns);
		this.setModel(dm);
		getColumn("Comp").setCellRenderer(new RadioButtonRenderer());
		getColumn("Comp").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		repaint();
	}
	
	private void increaseRowHeight() {
		int nRow = dm.getRowCount();
		for (int i = 0; i < nRow; i++) {
			setRowHeight(i, 30);
		}
	}
}
