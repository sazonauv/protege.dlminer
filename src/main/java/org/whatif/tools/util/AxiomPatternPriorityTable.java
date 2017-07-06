package org.whatif.tools.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.whatif.tools.axiompattern.AxiomPattern;
import org.whatif.tools.axiompattern.AxiomPatternFactory;

public class AxiomPatternPriorityTable extends JTable {

	DefaultTableModel dm = new DefaultTableModel();
	Object[] columns = new Object[] { "Pattern", "Priority" };
	UpdateInferenceInspector up = null;
	// Map<AxiomPattern,AxiomPriority> priomap = new
	// HashMap<AxiomPattern,AxiomPriority>();

	public AxiomPatternPriorityTable(UpdateInferenceInspector up) {
		this.up = up;
		dm.setColumnIdentifiers(columns);
		setAutoCreateRowSorter(true);
		this.setModel(dm);
		getRowSorter().toggleSortOrder(0);
		initialise();
	}

	private void initialise() {
		dm.setRowCount(0);
		List<Object[]> rows = new ArrayList<Object[]>();
		rows.add(new Object[] { "Inference Patterns", null });
		rows.add(new Object[] { AxiomPatternFactory.getInconsistentOntologyAxiomPattern(), AxiomPriority.P1 });
		rows.add(new Object[] { AxiomPatternFactory.getUnsatisfiableAxiomPattern(), AxiomPriority.P1 });
		rows.add(new Object[] { AxiomPatternFactory.getEquivalenceClassAxiomPattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getSubClassIfEqualAxiomPattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getSubClassIfNotEqualAxiomPattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getDisjointClassDirectPattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getDisjointClassIndirectPattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getMostSpecificTypeAxiomPattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getPropertyCharacteristicAxiomPattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getAssertedAxiomPattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getPropertyEqualsInversePattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getDefaultPattern(), AxiomPriority.P3 });

		rows.add(new Object[] { null, null });
		rows.add(new Object[] { "Profile Violations", null });

		rows.add(new Object[] { AxiomPatternFactory.getDLProfilePattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getELProfilePattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getQLProfilePattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getRLProfilePattern(), AxiomPriority.P4 });

		rows.add(new Object[] { null, null });
		rows.add(new Object[] { "Assertional Consequences", null });

		rows.add(new Object[] { AxiomPatternFactory.getAddedAxiomAlreadyImpliedPattern(), AxiomPriority.REMOVE });
		//rows.add(new Object[] { AxiomPatternFactory.getAddedAxiomIncreasedProfilePattern(), AxiomPriority.P2 });
		rows.add(new Object[] { AxiomPatternFactory.getAddedTautologyPattern(), AxiomPriority.P4 });
		rows.add(new Object[] { AxiomPatternFactory.getAddedStrayEntityPattern(), AxiomPriority.P4 });

		/*
		 * priomap.put(AxiomPatternFactory.getUnsatisfiableAxiomPattern(),
		 * AxiomPriority.CRITICAL);
		 * priomap.put(AxiomPatternFactory.getSubClassIfEqualAxiomPattern(),
		 * AxiomPriority.LOW);
		 * priomap.put(AxiomPatternFactory.getEquivalenceClassAxiomPattern(),
		 * AxiomPriority.HIGH);
		 * priomap.put(AxiomPatternFactory.getSubClassIfNotEqualAxiomPattern(),
		 * AxiomPriority.HIGH);
		 * priomap.put(AxiomPatternFactory.getDisjointClassDirectPattern(),
		 * AxiomPriority.HIGH);
		 * priomap.put(AxiomPatternFactory.getDisjointClassIndirectPattern(),
		 * AxiomPriority.LOW);
		 * priomap.put(AxiomPatternFactory.getLessSpecificTypeAxiomPattern(),
		 * AxiomPriority.MEDIUM);
		 * priomap.put(AxiomPatternFactory.getMostSpecificTypeAxiomPattern(),
		 * AxiomPriority.HIGH);
		 * priomap.put(AxiomPatternFactory.getPropertyCharacteristicAxiomPattern
		 * (), AxiomPriority.MEDIUM);
		 * priomap.put(AxiomPatternFactory.getDefaultPattern(),
		 * AxiomPriority.MEDIUM);
		 */

		Object[][] data = (Object[][]) rows.toArray(new Object[rows.size()][]);
		dm.setDataVector(data, columns);
		// getColumn("Priority").setCellRenderer(new JComboRenderer());
		TableColumn col = getColumnModel().getColumn(1);
		JComboBox comboBox = new JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(AxiomPriority.P1);
		model.addElement(AxiomPriority.P2);
		model.addElement(AxiomPriority.P3);
		model.addElement(AxiomPriority.P4);
		model.addElement(AxiomPriority.REMOVE);
		comboBox.setModel(model);
		col.setCellEditor(new DefaultCellEditor(comboBox));

		model = new DefaultComboBoxModel();
		model.addElement(AxiomPriority.P1);
		model.addElement(AxiomPriority.P2);
		model.addElement(AxiomPriority.P3);
		model.addElement(AxiomPriority.P4);
		model.addElement(AxiomPriority.REMOVE);

		ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
		renderer.setModel(model);
		col.setCellRenderer(renderer);
		/*
		 * comboBox.addItemListener (new ItemListener () {
		 * 
		 * @Override public void itemStateChanged(ItemEvent e) {
		 * System.out.println("ITEM"+e.getItem());
		 * if(e.getStateChange()==ItemEvent.SELECTED) {
		 * 
		 * 
		 * } //updateView(); } });
		 */
		increaseRowHeight();
		repaint();
	}

	protected void updateView() {
		if (up != null) {
			up.updateInspector();
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (e.getType() == TableModelEvent.UPDATE) {
			updateView();
		}
		repaint();
	}

	public class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value != null) {
				setSelectedItem(value);
				return this;
			} else {
				return new JLabel("");
			}
		}

	}

	public AxiomPriority getPriority(AxiomPattern p) {
		AxiomPriority matchedpriority = AxiomPriority.P3;
		int nRow = dm.getRowCount();
		for (int i = 0; i < nRow; i++) {
			Object patt = dm.getValueAt(i, 0);
			if(patt!=null) {
				if(patt instanceof AxiomPattern) {
					AxiomPattern pattern = (AxiomPattern) patt;
					AxiomPriority priority = (AxiomPriority) dm.getValueAt(i, 1);
					if (pattern.equals(p)) {
						return priority;
					}
				}
			}
			
		}
		return matchedpriority;
	}

	private void increaseRowHeight() {
		int nRow = dm.getRowCount();
		for (int i = 0; i < nRow; i++) {
			setRowHeight(i, 30);
		}
	}

}
