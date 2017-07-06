package org.whatif.tools.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JTextArea;
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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class WhatifAxiomTablePlain extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6087732631558008455L;
	DefaultTableModel dm = new DefaultTableModel();
	DefaultTableColumnModel dcm = new DefaultTableColumnModel();
	Object[] columns = new Object[] { "Type", "OWL" };
	final OWLModelManager owlModelManager;
	final OWLSelectionModel owlSelectionModel;
	final String name;
	final OWLEditorKit editorKit;
	TableColumnManager tcm;
	ProfileVilationProvider pvp;
	int currentfontsize = 14;
	Set<OWLAxiom> axioms = null;

	public WhatifAxiomTablePlain(OWLModelManager owlModelManager, OWLSelectionModel owlSelectionModel,
			OWLEditorKit editorkit, String name) {
		this.name = name;
		this.owlModelManager = owlModelManager;
		this.owlSelectionModel = owlSelectionModel;
		this.editorKit = editorkit;

		setColumnModel(dcm);
		setModel(dm);
		tcm = new TableColumnManager(this);
		dm.setColumnIdentifiers(columns);
		setAutoCreateRowSorter(true);

		getRowSorter().toggleSortOrder(dcm.getColumnIndex("Type"));
		// this.setCellSelectionEnabled(true);

		// setSelectionModel(new ForcedListSelectionModel());
		// cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tcm.hideColumn("Type");
		createMouseListener();
		
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
		
		Set<OWLAxiom> axiomsin;
		Object[][] data;

		public SetAxiomTask(Set<OWLAxiom> axiomsin) {
			this.axiomsin = axiomsin;
		}

		@Override
		protected Void doInBackground() throws Exception {
			dm.setRowCount(0);
			List<Object[]> rows = new ArrayList<Object[]>();
			
			for (OWLAxiom key : axiomsin) {
				Object[] row = new Object[] { key.getAxiomType(), key };
				rows.add(row);
			}
			
			data = (Object[][]) rows.toArray(new Object[rows.size()][]);			
			return null;
		}
		
		@Override
		public void done() {
			
			dm.setDataVector(data, columns);

			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setCellRenderer(new LineWrapCellRenderer());
			getColumnModel().getColumn(getColumnModel().getColumnIndex("OWL")).setCellRenderer(new AxiomListItemRenderer());

			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setMinWidth(100);
			getColumnModel().getColumn(getColumnModel().getColumnIndex("Type")).setMaxWidth(100);
			getRowSorter().toggleSortOrder(getColumnModel().getColumnIndex("OWL"));
			for (int i : tcm.getHiddenColumns()) {
				tcm.hideColumn(i);
			}
			revalidate();
			repaint();
			updateRowHeights();
		}
	}

	public void setAxioms(Set<OWLAxiom> axioms) {
		this.axioms = axioms;
		SetAxiomTask setAxioms = new SetAxiomTask(axioms);
		setAxioms.execute();
	}

	@Override
	public Class<?> getColumnClass(int c) {
		System.out.println(c);
		return getValueAt(0, c).getClass();
	}

	public void showColumn(int i) {
		// System.out.println("W3");
		if (tcm.getHiddenColumns().contains(i)) {
			tcm.showColumn(i);
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

		private static final long serialVersionUID = -5382631512217176592L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			this.setText((String) value);
			this.setWrapStyleWord(true);
			this.setLineWrap(true);
			return this;
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
			if (value instanceof OWLAxiom) {
				OWLAxiom item = ((OWLAxiom) value);
				ren.setOntology(o);
				ren.setHighlightKeywords(true);
				ren.setWrap(true);
				// ren.reset();
				ren.setHighlightUnsatisfiableClasses(true);
				// TODO somehow make the size correct
				Component comp = ren.getTableCellRendererComponent(table, item, isSelected, hasFocus, row,
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

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// System.out.println("");
				int selectedRow = convertRowIndexToModel(getSelectedRow());
				OWLAxiom con = (OWLAxiom) getModel().getValueAt(selectedRow, 1);
				if (con instanceof OWLAxiom) {
					OWLEntity ent = WhatifUtils
							.getPrimaryEntityOfAxiom(con);
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

	public Set<OWLAxiom> getAllAxioms() {
		return axioms;
	}
}
