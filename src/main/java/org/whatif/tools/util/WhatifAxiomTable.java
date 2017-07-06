package org.whatif.tools.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.whatif.tools.axiompattern.AxiomPattern;
import org.whatif.tools.consequence.WhatifConsequence;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.view.EntailmentInspectorView;

public class WhatifAxiomTable extends JTable {

	DefaultTableModel dm = new DefaultTableModel();
	DefaultTableColumnModel dcm = new DefaultTableColumnModel();
	Object[] columns = new Object[] { "P", "OWL", "Type", "Info", "G" };
	final OWLModelManager owlModelManager;
	final OWLSelectionModel owlSelectionModel;
	final String name;
	final OWLEditorKit editorKit;
	TableColumnManager tcm;
	WhatifExplanationDialog dlg = null;
	ProfileVilationProvider pvp;
	int currentfontsize = 14;
	Map<AxiomPriority, Map<AxiomPattern, Map<WhatifConsequence, Set<WhatifConsequence>>>> grouping = new HashMap<AxiomPriority, Map<AxiomPattern, Map<WhatifConsequence, Set<WhatifConsequence>>>>();

	public WhatifAxiomTable(OWLModelManager owlModelManager, OWLSelectionModel owlSelectionModel,
			OWLEditorKit editorkit, String name, ProfileVilationProvider pvp) {
		this.name = name;
		this.owlModelManager = owlModelManager;
		this.owlSelectionModel = owlSelectionModel;
		this.pvp = pvp;
		this.editorKit = editorkit;

		setColumnModel(dcm);
		/*
		 * getColumnModel().addColumnModelListener(new
		 * TableColumnModelListener() {
		 * 
		 * @Override public void columnSelectionChanged(ListSelectionEvent e) {
		 * // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void columnRemoved(TableColumnModelEvent e) { //
		 * TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void columnMoved(TableColumnModelEvent e) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void columnMarginChanged(ChangeEvent e) {
		 * updateRowHeights(); }
		 * 
		 * @Override public void columnAdded(TableColumnModelEvent e) { // TODO
		 * Auto-generated method stub
		 * 
		 * } });
		 */
		setModel(dm);
		tcm = new TableColumnManager(this);
		dm.setColumnIdentifiers(columns);
		setAutoCreateRowSorter(true);

		getRowSorter().toggleSortOrder(dcm.getColumnIndex("P"));
		// this.setCellSelectionEnabled(true);

		// setSelectionModel(new ForcedListSelectionModel());
		// cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tcm.hideColumn("Type");
		createMouseListener();
		dlg = new WhatifExplanationDialog(owlModelManager, pvp, this);
		
	}

	protected OWLSelectionModel getOWLSelectionModel() {
		return owlSelectionModel;
	}

	public TableColumn getColumn(String s) {
		return tcm.getColumn(s);
	}

	public TableColumnModel getColumnModel() {
		if (dcm != null) {
			return dcm;
		} else {
			return super.getColumnModel();
		}
	}
	
	public class SetAxiomTask extends SwingWorker<Void, Void> {
		
		Map<WhatifConsequence, AxiomPriority> axiomsin;
		Object[][] data;

		public SetAxiomTask(Map<WhatifConsequence, AxiomPriority> axiomsin) {
			this.axiomsin = axiomsin;
		}

		@Override
		protected Void doInBackground() throws Exception {
			dm.setRowCount(0);
			/// System.out.println("THISTHISTHIS:" + getThis());
			List<Object[]> rows = new ArrayList<Object[]>();

			// Map<WhatifConsequence, AxiomPriority> axioms = new
			// HashMap<WhatifConsequence, AxiomPriority>();
			grouping.clear();
			for (WhatifConsequence key : axiomsin.keySet()) {
				if (key.getOWLAxiom() instanceof OWLClassAssertionAxiom) {
					OWLClassAssertionAxiom ca = (OWLClassAssertionAxiom) key.getOWLAxiom();
					AxiomPriority p = axiomsin.get(key);
					AxiomPattern pat = key.getHighestPriorityAxiomPattern();
					if (!grouping.containsKey(p)) {
						grouping.put(p, new HashMap<AxiomPattern, Map<WhatifConsequence, Set<WhatifConsequence>>>());
					}
					if (!grouping.get(p).containsKey(pat)) {
						grouping.get(p).put(pat, new HashMap<WhatifConsequence, Set<WhatifConsequence>>());
					}
					boolean ingrouping = false;
					for (WhatifConsequence con : grouping.get(p).get(pat).keySet()) {
						if (con.getOWLAxiom() instanceof OWLClassAssertionAxiom) {
							OWLClassAssertionAxiom conca = (OWLClassAssertionAxiom) con.getOWLAxiom();
							if (conca.getClassExpression().equals(ca.getClassExpression())) {
								grouping.get(p).get(pat).get(con).add(key);
								ingrouping = true;
							}
						}
					}
					if (!ingrouping) {
						grouping.get(p).get(pat).put(key, new HashSet<WhatifConsequence>());
						Object[] row = new Object[] { axiomsin.get(key), key, key.getConsequenceType(), "?", ">" };
						rows.add(row);
					}
				} else if (key.getOWLAxiom() instanceof OWLObjectPropertyAssertionAxiom) {
					OWLObjectPropertyAssertionAxiom ca = (OWLObjectPropertyAssertionAxiom) key.getOWLAxiom();
					AxiomPriority p = axiomsin.get(key);
					AxiomPattern pat = key.getHighestPriorityAxiomPattern();
					if (!grouping.containsKey(p)) {
						grouping.put(p, new HashMap<AxiomPattern, Map<WhatifConsequence, Set<WhatifConsequence>>>());
					}
					if (!grouping.get(p).containsKey(pat)) {
						grouping.get(p).put(pat, new HashMap<WhatifConsequence, Set<WhatifConsequence>>());
					}
					boolean ingrouping = false;
					for (WhatifConsequence con : grouping.get(p).get(pat).keySet()) {
						if (con.getOWLAxiom() instanceof OWLObjectPropertyAssertionAxiom) {
							OWLObjectPropertyAssertionAxiom conca = (OWLObjectPropertyAssertionAxiom) con.getOWLAxiom();
							if (conca.getProperty().equals(ca.getProperty())) {
								grouping.get(p).get(pat).get(con).add(key);
								ingrouping = true;
							}
						}
					}
					if (!ingrouping) {
						grouping.get(p).get(pat).put(key, new HashSet<WhatifConsequence>());
						Object[] row = new Object[] { axiomsin.get(key), key, key.getConsequenceType(), "?", ">" };
						rows.add(row);
					}
				}

				else {
					Object[] row = new Object[] { axiomsin.get(key), key, key.getConsequenceType(), "?", "" };
					rows.add(row);
				}
			}
			
			data = (Object[][]) rows.toArray(new Object[rows.size()][]);			
			return null;
		}
		
		@Override
		public void done() {
			
			dm.setDataVector(data, columns);

			getColumnModel().getColumn(getColumnModel().getColumnIndex("Info")).setCellRenderer(new ButtonRenderer());
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Info"))
					.setCellEditor(new ButtonEditor(new JCheckBox()));

			// getColumnModel().getColumn(getColumnModel().getColumnIndex("G")).setCellRenderer(new
			// ButtonRenderer());
			getColumnModel().getColumn(getColumnModel().getColumnIndex("G"))
					.setCellEditor(new GroupButtonEditor(new JCheckBox()));

			getColumnModel().getColumn(getColumnModel().getColumnIndex("P")).setCellRenderer(new ColorRenderer());
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setCellRenderer(new LineWrapCellRenderer());

			getColumnModel().getColumn(getColumnModel().getColumnIndex("OWL")).setCellRenderer(new AxiomListItemRenderer());
			// .setCellRenderer(new AxiomAreaRenderer());
			// setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			getColumnModel().getColumn(getColumnModel().getColumnIndex("P")).setMinWidth(20);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("P")).setMaxWidth(30);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setMinWidth(100);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setMaxWidth(100);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Info")).setMinWidth(45);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Info")).setMaxWidth(45);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("G")).setMinWidth(25);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("G")).setMaxWidth(25);
			// getColumnModel().getColumn(0).setResizable(false);
			// getColumnModel().getColumn(1).setPreferredWidth(70);
			// getColumnModel().getColumn(2).setResizable(false);
			// getColumnModel().getColumn(3).setResizable(false);
			getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex("OWL"));
			getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex("P"));
			// getRowSorter().toggleSortOrder(0);
			for (int i : tcm.getHiddenColumns()) {
				tcm.hideColumn(i);
			}
			revalidate();
			repaint();
			updateRowHeights();
		}
	}

	public void setAxioms(Map<WhatifConsequence, AxiomPriority> axiomsin) {
		SetAxiomTask setAxioms = new SetAxiomTask(axiomsin);
		setAxioms.execute();
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void showColumn(int i) {
		// System.out.println("W3");
		if (tcm.getHiddenColumns().contains(i)) {
			tcm.showColumn(i);
		}
		// System.out.println("W4");
	}

	public void hideColumn(String s) {
		try {
			int i = getColumn(s).getModelIndex();
			if (!tcm.getHiddenColumns().contains(i)) {
				System.out.println("HIDE");
				tcm.hideColumn(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("W4");
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		// updateRowHeights();
		revalidate();
		repaint();
	}

	public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			this.setText((String) value);
			this.setWrapStyleWord(true);
			this.setLineWrap(true);
			return this;
		}

	}

	class ColorRenderer extends JLabel implements TableCellRenderer {

		public ColorRenderer() {
			setOpaque(true); // MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table, Object priority, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (((AxiomPriority) priority) == AxiomPriority.P1) {
				setBackground(new Color(227, 111, 111));
			} else if (((AxiomPriority) priority) == AxiomPriority.P2) {
				setBackground(new Color(255, 239, 141));
			} else {
				setBackground(new Color(255, 255, 255));
			}
			setText(priority.toString());
			return this;
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// System.out.println(value);
			if (value.toString() == "") {
				return new JLabel("");
			}
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getSelectionBackground());
				// setBackground(UIManager.getColor("Button.background"));
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
		private WhatifConsequence con;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			//button.setOpaque(true);
			button.setContentAreaFilled(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EventLogging.saveEvent(System.currentTimeMillis(), "table:" + WhatifAxiomTable.this.name,
							"click_explanation", EventLogging.render(con.getOWLAxiom()), "wii");
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			con = (WhatifConsequence) table.getValueAt(row, 1);
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
				dlg.reset(con);
				//JOptionPane.showMessageDialog(button, dlg);
				JOptionPane optionPane = new JOptionPane();
		        optionPane.setMessage(dlg);
		        //optionPane.setOptionType(JOptionPane.OK_OPTION);
		        //optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
		        //JDialog.setDefaultLookAndFeelDecorated(true);
		        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(WhatifAxiomTable.this), "Information");
		        dialog.setModal(false);
		        dialog.add(dlg);
		        dialog.setLocationRelativeTo(button);
		        dialog.setSize(400, 300);
		        dialog.setVisible(true);
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
		setAutoCreateRowSorter(true);
		this.setModel(dm);
		getRowSorter().toggleSortOrder(0);
		repaint();
	}

	private class AxiomListItemRenderer implements TableCellRenderer {

		private OWLCellRenderer ren;
		private OWLOntology o;

		public AxiomListItemRenderer() {
			ren = new OWLCellRenderer(editorKit);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof WhatifConsequence) {
				WhatifConsequence item = ((WhatifConsequence) value);
				ren.setOntology(o);
				ren.setHighlightKeywords(true);
				ren.setWrap(true);
				// ren.reset();
				ren.setHighlightUnsatisfiableClasses(true);
				// ren.se
				// TODO somehow make the size correct
				Component comp = ren.getTableCellRendererComponent(table, item.getOWLAxiom(), isSelected, hasFocus, row,
						column);

				// comp.setPreferredSize(new
				// Dimension(getColumnModel().getColumn(getColumnModel().getColumnIndex("OWL")).getWidth(),comp.getHeight()));
				// comp.setMaximumSize(new
				// Dimension(getColumnModel().getColumn(getColumnModel().getColumnIndex("OWL")).getWidth(),comp.getHeight()));

				return comp;
			} else {
				return ren.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}
	}

	public void updateRowHeights() {
		for (int row = 0; row < getRowCount(); row++) {
			int rowHeight = getRowHeight();

			for (int column = 0; column < getColumnCount(); column++) {
				Component comp = prepareRenderer(getCellRenderer(row, column), row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}

			setRowHeight(row, rowHeight);
		}
	}

	class GroupButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;
		private WhatifConsequence con;
		private AxiomPriority prio;
		private int row;
		boolean allow;

		private boolean isPushed;

		public GroupButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EventLogging.saveEvent(System.currentTimeMillis(), "table:" + WhatifAxiomTable.this.name,
							"click_ungroup", EventLogging.render(con.getOWLAxiom()), "wii");
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {

			con = (WhatifConsequence) table.getValueAt(row, 1);
			prio = (AxiomPriority) table.getValueAt(row, 0);
			label = (value == null) ? "" : value.toString();
			this.row = convertRowIndexToModel(row);
			// System.out.println("VAVA: "+value);
			this.allow = !value.toString().isEmpty();

			isPushed = true;

			return button;
		}

		public Object getCellEditorValue() {
			System.out.println("getCellEditor: " + allow + " " + isPushed);
			if (isPushed && allow) {
				// System.out.println("A1");
				if (grouping.containsKey(prio)) {
					// System.out.println("A2");
					if (grouping.get(prio).containsKey(con.getHighestPriorityAxiomPattern())) {

						// System.out.println("A3");
						if (grouping.get(prio).get(con.getHighestPriorityAxiomPattern()).containsKey(con)) {
							Set<WhatifConsequence> all = grouping.get(prio).get(con.getHighestPriorityAxiomPattern())
									.get(con);
							// System.out.println(getColumn("G").getModelIndex());
							// System.out.println(row);

							for (WhatifConsequence c : all) {
								Object[] row = new Object[] { prio, c, c.getConsequenceType(), "?", "" };
								dm.addRow(row);
							}

							getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex("OWL"));
							getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex("P"));
							// getRowSorter().toggleSortOrder(0);

							for (int i : tcm.getHiddenColumns()) {
								tcm.hideColumn(i);
							}
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									updateRowHeights();
								}
							});
							isPushed = false;

						}
					}
				}

			}

			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;

			// WhatifAxiomTable.this.repaint();
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
			dm.setValueAt("", row, getColumn("G").getModelIndex());
		}

	}

	private class ForcedListSelectionModel extends DefaultListSelectionModel {

		public ForcedListSelectionModel() {
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		@Override
		public void clearSelection() {
		}

		@Override
		public void removeSelectionInterval(int index0, int index1) {
		}

	}

	public void resetFontSize() {
		currentfontsize = 14;
		setFont(new Font("Serif", Font.BOLD, currentfontsize));
		repaint();
	}

	public void decreaseFontSize() {
		currentfontsize--;
		setFont(new Font("Serif", Font.BOLD, currentfontsize));
		repaint();
	}

	public void increaseFontSize() {
		currentfontsize++;
		setFont(new Font("Serif", Font.BOLD, currentfontsize));
		repaint();
	}

	private void createMouseListener() {
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// System.out.println("");
				int selectedRow = convertRowIndexToModel(getSelectedRow());
				WhatifConsequence con = (WhatifConsequence) getModel().getValueAt(selectedRow, 1);
				if (con instanceof WhatifInferenceConsequence) {
					EventLogging.saveEvent(System.currentTimeMillis(), "table:" + WhatifAxiomTable.this.name,
							"click_consequence", EventLogging.render(con.getOWLAxiom()), "wii");
					OWLEntity ent = WhatifUtils
							.getPrimaryEntityOfAxiom(((WhatifInferenceConsequence) con).getOWLAxiom());
					getOWLSelectionModel().setSelectedEntity(ent);
				}
			}
		});
	}

	public void sortColumn(String string) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex(string));
			}
		});
		
	}
}
