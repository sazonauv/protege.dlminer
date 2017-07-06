package org.whatif.tools.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.ExplanationDialog;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.whatif.tools.axiompattern.AxiomPattern;
import org.whatif.tools.axiompattern.ProfileAxiomPattern;
import org.whatif.tools.consequence.WhatifAddedAssertionConsequence;
import org.whatif.tools.consequence.WhatifConsequence;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.view.EntailmentInspectorView;

public class WhatifExplanationDialog extends JPanel {

	WhatifConsequence ax = null;
	JPanel p = null;
	JButton bt_delete = new JButton("<html><center>" + "Delete selected" + "<br>" + "and finish" + "</center></html>");
	JButton bt_alljust = new JButton("<html><center>" + "More" + "<br>" + "explanations" + "</center></html>");
	JList<CheckboxListItem> list = new JList<CheckboxListItem>();
	OWLModelManager owlModelManager;
	Component parent;
	MouseListener ml;
	ProfileVilationProvider pvp;

	WhatifExplanationDialog(OWLModelManager omm, ProfileVilationProvider pvp, Component parent) {
		this.parent = parent;
		this.owlModelManager = omm;
		this.pvp = pvp;
		setLayout(new GridLayout(1, 1));
		p = new JPanel();
		p.setLayout(new BorderLayout());

		bt_delete.setPreferredSize(new Dimension(80, 40));
		bt_delete.setMaximumSize(new Dimension(80, 40));

		bt_delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CheckboxListItem> items = list.getSelectedValuesList();
				boolean deleted = false;
				for (CheckboxListItem i : items) {
					if (!deleted)
						deleted = true;
					owlModelManager.applyChange(new RemoveAxiom(owlModelManager.getActiveOntology(), i.getAxiom()));
				}
				java.awt.Window w = SwingUtilities.getWindowAncestor((JButton) e.getSource());

				if (w != null) {
					w.setVisible(false);
				}
				if (deleted) {
					JOptionPane.showMessageDialog((JButton) e.getSource(),
							"Axioms deleted... Please run the reasoner again!");
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				JList<CheckboxListItem> list = (JList<CheckboxListItem>) event.getSource();
				int index = list.locationToIndex(event.getPoint());
				CheckboxListItem item = (CheckboxListItem) list.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());
				list.repaint(list.getCellBounds(index, index));
			}
		});

		bt_alljust.setPreferredSize(new Dimension(80, 20));
		bt_alljust.setMaximumSize(new Dimension(80, 20));

		add(p);
	}

	void reset(WhatifConsequence con) {

		p.removeAll();
		bt_alljust.removeMouseListener(ml);
		StringBuilder sb = new StringBuilder();

		if (con instanceof WhatifInferenceConsequence) {
			prepareLogicalExplanationDialog(con, sb);
		} else if (con instanceof WhatifAddedAssertionConsequence) {
			JTextArea expl = new JTextArea();
			ax = con;
			AxiomPattern pa = con.getHighestPriorityAxiomPattern();
			if (pa instanceof ProfileAxiomPattern) {
				WhatifUtils.p("ProfileAxiomPatternProfileAxiomPattern");
				Collection<OWLProfileViolation> vios = pvp.getViolations(((ProfileAxiomPattern) pa).getClass());
				WhatifUtils.p("Size:" + vios.size());
				for (OWLProfileViolation vio : vios) {
					if (vio.getAxiom().equals(ax.getOWLAxiom())) {
						sb.append(pa.toString() + " \n" + "\n");
						sb.append(vio.getClass().getSimpleName().replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2") + ": \n");
						sb.append(EntailmentInspectorView.getRenderer().render(ax.getOWLAxiom()));
						break;
					}
				}
			}
			expl.setText(sb.toString());
			expl.setWrapStyleWord(true);
			expl.setLineWrap(true);
			expl.setAutoscrolls(true);
			JScrollPane scroll = new JScrollPane(expl);
			scroll.setPreferredSize(new Dimension(200, 100));
			p.add(scroll, BorderLayout.CENTER);
			EventLogging.saveEvent(System.currentTimeMillis(), "explanation computed",
					"click_explanation_computed", EventLogging.render(con.getOWLAxiom()), "wii");
			
		}
	}

	private void prepareLogicalExplanationDialog(WhatifConsequence con, StringBuilder sb) {
		JLabel explanationlabel = new JLabel();
		explanationlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ax = (WhatifInferenceConsequence) con;
		Set<Explanation<OWLAxiom>> expl = new HashSet<Explanation<OWLAxiom>>();
		boolean consistent = owlModelManager.getOWLReasonerManager().getCurrentReasoner().isConsistent();
		try {
			if (consistent) {
				expl.addAll(EntailmentInspectorView.getExplanationGenerator().getExplanations(ax.getOWLAxiom(), 5));
			} else {
				expl.addAll(EntailmentInspectorView.getInconsistentExplanationGenerator()
						.getExplanations(ax.getOWLAxiom(), 5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Explanation<OWLAxiom> smallest = null;
		WhatifUtils.p(expl.size());
		for (Explanation<OWLAxiom> ex : expl) {
			if (smallest == null) {
				smallest = ex;
			} else if (smallest.getAxioms().size() > ex.getAxioms().size()) {
				smallest = ex;
			}
		}

		if (smallest == null) {
			p.add(new JLabel("No explanation generated."));
		} else {
			DefaultListModel<CheckboxListItem> lm = new DefaultListModel<CheckboxListItem>();
			list.setModel(lm);
			for (OWLAxiom axiom : smallest.getAxioms()) {
				lm.addElement(new CheckboxListItem(axiom));
				// String s =
				// EntailmentInspectorView.getRenderer().render(axiom);
				// sb.append(s+" \n");
			}
			list.setCellRenderer(new CheckboxListRenderer());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Add a mouse listener to handle changing selection

			ml = new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					ExplanationManager em = owlModelManager.getExplanationManager();
					em.reload();
					Collection<ExplanationService> teachers = em.getTeachers(ax.getOWLAxiom());
					if (!teachers.isEmpty()) {
						final ExplanationDialog explanation = new ExplanationDialog(em, ax.getOWLAxiom());
						JOptionPane op = new JOptionPane(explanation, JOptionPane.PLAIN_MESSAGE,
								JOptionPane.DEFAULT_OPTION);
						JDialog dlg = op.createDialog(p, "All Explanations");
						dlg.addComponentListener(new ComponentAdapter() {
							@Override
							public void componentHidden(ComponentEvent e) {
								explanation.dispose();
							}
						});
						dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dlg.setModal(false);
						dlg.setResizable(true);
						dlg.pack();
						dlg.setVisible(true);
					}

				}
			};

			bt_alljust.addMouseListener(ml);

			if (!consistent) {
				sb.append("The ontology is inconsistent! ");
			}

			// StringBuilder sb = new StringBuilder();
			if (ax.getOWLAxiom() instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom sax = (OWLSubClassOfAxiom) ax.getOWLAxiom();
				sb.append("That " + render(sax.getSubClass()) + " is a sub-class of " + render(sax.getSuperClass())
						+ " follows from:");
			} else if (ax.getOWLAxiom() instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom sax = (OWLEquivalentClassesAxiom) ax.getOWLAxiom();
				sb.append("That ");
				for (OWLClassExpression e : sax.getClassExpressionsAsList()) {
					sb.append(render(e) + " and ");
				}
				if (sb.length() > 0) {
					sb.setLength(sb.length() - 5);
				}
				sb.append(" are equivalent follows from:");
			} else {
				sb.append("Explanation for " + render(ax.getOWLAxiom()));
			}
		}
		explanationlabel.setText(sb.toString());
		list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(explanationlabel, BorderLayout.NORTH);
		p.add(list, BorderLayout.CENTER);
		// p.add(Box.createRigidArea(new Dimension(10, 0)));
		// list.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel p2 = new JPanel(new GridLayout(1, 2));
		p.add(p2, BorderLayout.SOUTH);
		p2.add(bt_delete);
		p2.add(bt_alljust);
	}

	private String render(OWLObject o) {
		return EntailmentInspectorView.getRenderer().render(o);
	}
}
