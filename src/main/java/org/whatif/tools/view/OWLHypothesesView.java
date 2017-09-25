package org.whatif.tools.view;

import io.dlminer.learn.Hypothesis;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.whatif.tools.util.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static org.whatif.tools.view.OWLHypothesesView.ColumnName.*;


/**
 * Created by slava on 07/09/17.
 */
public class OWLHypothesesView extends JTable {

    public enum ColumnName {
        ID("ID"),
        HYPOTHESES("Hypotheses"),
        SUPPORT("Support"),
        ASSUMPTION("Assumption"),
        CONFIDENCE("Confidence"),
        CHECK("Check");

        private String name;

        ColumnName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    /**
     *
     */
    private static final long serialVersionUID = -6087732631558008455L;
    private DefaultTableModel tableModel = new DefaultTableModel();
    private DefaultTableColumnModel tableColumnModel = new DefaultTableColumnModel();
    private Object[] columns = new Object[] {
            ID.getName(), HYPOTHESES.getName(),
            SUPPORT.getName(), ASSUMPTION.getName(), CONFIDENCE.getName(),
            CHECK.getName()
    };
    private final OWLModelManager owlModelManager;
    private final OWLSelectionModel owlSelectionModel;
    private final OWLEditorKit editorKit;
    private TableColumnManager tableColumnManager;
    private int currentfontsize = 14;
    private Collection<Hypothesis> hypotheses = null;
    private Set<OWLAxiom> axioms;

    public OWLHypothesesView(OWLModelManager owlModelManager, OWLSelectionModel owlSelectionModel,
                                 OWLEditorKit editorkit) {
        this.owlModelManager = owlModelManager;
        this.owlSelectionModel = owlSelectionModel;
        this.editorKit = editorkit;

        setColumnModel(tableColumnModel);
        setModel(tableModel);
        tableColumnManager = new TableColumnManager(this);
        tableModel.setColumnIdentifiers(columns);
        setAutoCreateRowSorter(true);
        getRowSorter().toggleSortOrder(tableColumnModel.getColumnIndex(ID.getName()));
        createMouseListener();
    }


    public TableColumnModel getColumnModel() {
        if (tableColumnModel != null) {
            return tableColumnModel;
        } else {
            return super.getColumnModel();
        }
    }

    public class SetHypothesesTask extends SwingWorker<Void, Void> {

        Collection<Hypothesis> hypothesesToSet;
        Object[][] data;
        DecimalFormat df = new DecimalFormat();

        public SetHypothesesTask(Collection<Hypothesis> hypothesesToSet) {
            this.hypothesesToSet = hypothesesToSet;
        }

        @Override
        protected Void doInBackground() throws Exception {
            tableModel.setRowCount(0);
            List<Object[]> rows = new ArrayList<>();
            int id = 0;
            int length = Integer.toString(hypothesesToSet.size()).length();
            for (Hypothesis h : hypothesesToSet) {
                JCheckBox checkBox = new JCheckBox();
                Object[] row = new Object[] {
                        getString(++id, length),
                        h.getFirstClassAxiom(),
                        fn(h.support),
                        fn(h.assumption),
                        fn(h.precision),
                        checkBox
                };
                rows.add(row);
            }

            data = rows.toArray(new Object[rows.size()][ColumnName.values().length]);
            return null;
        }

        @Override
        public void done() {
            tableModel.setDataVector(data, columns);
            TableColumnModel model = getColumnModel();

            TableColumn idColumn = model.getColumn(model.getColumnIndex(ID.getName()));
            idColumn.setCellRenderer(new OWLHypothesesView.LineWrapCellRenderer());
            idColumn.setResizable(true);
            idColumn.setMinWidth(40);
            idColumn.setMaxWidth(70);

            TableColumn hypoColumn = model.getColumn(model.getColumnIndex(HYPOTHESES.getName()));
            hypoColumn.setCellRenderer(new OWLHypothesesView.AxiomListItemRenderer());
            hypoColumn.setResizable(true);


            TableColumn supColumn = model.getColumn(model.getColumnIndex(SUPPORT.getName()));
            supColumn.setCellRenderer(new OWLHypothesesView.LineWrapCellRenderer());
            supColumn.setResizable(true);
            supColumn.setMinWidth(50);
            supColumn.setMaxWidth(70);

            TableColumn asmColumn = model.getColumn(model.getColumnIndex(ASSUMPTION.getName()));
            asmColumn.setCellRenderer(new OWLHypothesesView.LineWrapCellRenderer());
            asmColumn.setResizable(true);
            asmColumn.setMinWidth(70);
            asmColumn.setMaxWidth(80);

            TableColumn confColumn = model.getColumn(model.getColumnIndex(CONFIDENCE.getName()));
            confColumn.setCellRenderer(new OWLHypothesesView.LineWrapCellRenderer());
            confColumn.setResizable(true);
            confColumn.setMinWidth(70);
            confColumn.setMaxWidth(80);

            TableColumn checkColumn = model.getColumn(model.getColumnIndex(CHECK.getName()));
            checkColumn.setCellRenderer(new OWLHypothesesView.AxiomCheckRenderer());
            checkColumn.setMinWidth(50);
            checkColumn.setMaxWidth(60);


            revalidate();
            repaint();
            updateRowHeights();
        }

        private String getString(int number, int length) {
            String numberStr = Integer.toString(number);
            int add = length - numberStr.length();
            if (add <= 0) {
                return numberStr;
            }
            for (int i=0; i<add; i++) {
                numberStr = "0" + numberStr;
            }
            return numberStr;
        }

        private String fn(double number) {
            df.setMaximumFractionDigits(3);
            return df.format(number);
        }
    }

    public void setHypotheses(Collection<Hypothesis> hypotheses) {
        this.hypotheses = hypotheses;
        setAxioms();
        SetHypothesesTask task = new SetHypothesesTask(hypotheses);
        task.execute();
    }

    private void setAxioms() {
        axioms = new HashSet<>();
        for (Hypothesis h : hypotheses) {
            axioms.addAll(h.axioms);
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        System.out.println(c);
        return getValueAt(0, c).getClass();
    }



    @Override
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        revalidate();
        repaint();
    }


    public void clear() {
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columns);
        setAutoCreateRowSorter(true);
        this.setModel(tableModel);
        getRowSorter().toggleSortOrder(0);
        repaint();
    }


    private class AxiomCheckRenderer extends JCheckBox implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            this.setEnabled(true);
            this.setSelected(true);
            return this;
        }

    }



    private class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            this.setText((String) value);
            this.setWrapStyleWord(true);
            this.setLineWrap(true);
            this.setEditable(false);
            return this;
        }

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
                int selectedRow = convertRowIndexToModel(getSelectedRow());
                JCheckBox checkBox = (JCheckBox) getModel().getValueAt(
                        selectedRow, getColumnModel().getColumnIndex(CHECK.getName()));
                checkBox.setSelected(false);
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

    public Set<OWLAxiom> getAxioms() {
        return axioms;
    }
}