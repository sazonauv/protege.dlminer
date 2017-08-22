package org.whatif.tools.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.whatif.tools.util.WhatifAxiomTablePlain;
import org.whatif.tools.util.WhatifUtils;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class DLMinerView extends AbstractOWLViewComponent implements ActionListener {
	private static final long serialVersionUID = -4515710047558710080L;

	private static final Logger log = Logger.getLogger(DLMinerView.class);

	JPanel buttonpanel = null;
//	JPanel selectpanel = null;
	JPanel axiompanel = null;
	JScrollPane axiomscrollpanel = null;
	JPanel buttselectpanel = null;

//	OWLEntitySelectorPanel selectentity = null;
//	OWLOntologySelectorPanel selectontology = null;

	JButton button_bottom = null;
	JButton button_export = null;

	WhatifAxiomTablePlain hypotheses = null;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new GridLayout(2, 1));

		buttonpanel = new JPanel();
		buttonpanel.setLayout(new GridLayout(1, 2));
		buttonpanel.setPreferredSize(new Dimension(100, 30));

//		selectpanel = new JPanel();
//		selectpanel.setLayout(new GridLayout(1, 2));

		buttselectpanel = new JPanel();
		buttselectpanel.setLayout(new BorderLayout());
		buttselectpanel.setPreferredSize(new Dimension(100, 30));

//		selectentity = new OWLEntitySelectorPanel(getOWLEditorKit(), true);
//		selectontology = new OWLOntologySelectorPanel(getOWLEditorKit());
//		selectontology.setSelection(getOWLModelManager().getActiveOntology());

		button_bottom = new JButton("run");
		button_bottom.setFont(new Font("Helvetica", Font.BOLD, 20));
		button_export = new JButton("export");
		button_export.setFont(new Font("Helvetica", Font.BOLD, 20));

		buttonpanel.add(button_bottom);
		buttonpanel.add(button_export);

//		selectpanel.add(selectontology);
//		selectpanel.add(selectentity);

		buttselectpanel.add(buttonpanel, BorderLayout.PAGE_START);
//		buttselectpanel.add(selectpanel, BorderLayout.CENTER);

		button_bottom.addActionListener(this);
		button_export.addActionListener(this);

		axiompanel = new JPanel();
		axiompanel.setLayout(new GridLayout(1, 1));
		axiompanel.setPreferredSize(new Dimension(100, 500));

		hypotheses = new WhatifAxiomTablePlain(getOWLModelManager(), getOWLWorkspace().getOWLSelectionModel(),
				getOWLEditorKit(), "MinedAxioms");
		hypotheses.setAxioms(new HashSet<>());
		axiomscrollpanel = new JScrollPane(hypotheses);
		axiompanel.add(axiomscrollpanel);

		add(axiompanel);
		add(buttselectpanel);

		log.info("DL-Miner view initialized");

	}

	protected ActionListener getThis() {
		return this;
	}

	@Override
	protected void disposeOWLView() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(button_bottom)) {

			mineAxioms(ModuleType.BOT);

		} else if (e.getSource().equals(button_export)) {
			exportAxioms();
		}

	}

	private void exportAxioms() {
		FileDialog fd = new FileDialog((Frame) SwingUtilities.getRoot(this), "Choose a file", FileDialog.SAVE);
		fd.setVisible(true);
		String filename = fd.getFile();
		if (filename == null) {
			JOptionPane.showMessageDialog(this, "Export is cancelled...");
		} else {
			WhatifUtils.p("Saving hypotheses to " + filename);
			Set<OWLAxiom> selection = hypotheses.getAllAxioms();
			if (selection.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Empty output, aborting...");
			} else {
				IRI moduleIRI = IRI.create("http://owl.cs.man.ac.uk/dlminer_" + UUID.randomUUID().toString());
				try {
					OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(selection, moduleIRI);
					OutputStream os = new FileOutputStream(new File(fd.getDirectory(), filename));
					o.getOWLOntologyManager().saveOntology(o, os);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Couldn't save ontology, see log..");
					e.printStackTrace();
				}
			}
		}

	}

	private void mineAxioms(ModuleType type) {

		OWLOntology o = null;
		OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
		try {
//			o = mgr.createOntology(selectontology.getSelectedOntology().getAxioms());
			o = mgr.createOntology(getOWLModelManager().getActiveOntology().getAxioms());
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(mgr, o, type);

		try {
//			Set<OWLAxiom> mod = sme.extract(selectentity.getSelectedObjects());
			Set<OWLAxiom> mod = sme.extract(o.getSignature());
			Set<OWLAxiom> mod_axioms = new HashSet<OWLAxiom>();
			for (OWLAxiom ax : mod) {
				if (ax.isLogicalAxiom()) {
					mod_axioms.add(ax);
				}
			}
			hypotheses.setAxioms(mod_axioms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
