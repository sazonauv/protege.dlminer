package org.whatif.tools.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.*;
import javax.swing.table.TableColumn;

import io.dlminer.learn.Hypothesis;
import io.dlminer.main.DLMiner;
import io.dlminer.main.DLMinerInput;
import io.dlminer.refine.OperatorConfig;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.whatif.tools.util.WhatifAxiomTable;
import org.whatif.tools.util.WhatifAxiomTablePlain;
import org.whatif.tools.util.WhatifUtils;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class DLMinerView extends AbstractOWLViewComponent implements ActionListener {
	private static final long serialVersionUID = -4515710047558710080L;

	private static final Logger log = Logger.getLogger(DLMinerView.class);

	// parameters
	JTextField maxHypoNumField = null;
	JTextField maxConceptLengthField = null;
	JTextField minSupportField = null;
	JTextField minPrecisionField = null;

	// buttons
	JButton buttonRun = null;
	JButton buttonExport = null;

	// hypotheses view
	WhatifAxiomTablePlain hypothesesTable = null;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		createParametersView();

		createHypothesesView();

		log.info("DL-Miner view is initialized");

	}

	private void createParametersView() {
		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new GridLayout(5, 2));

		final int defTextFieldSize = 7;

		JLabel  maxHypoNumLabel= new JLabel("Max hypotheses number: ", JLabel.LEFT);
		paramPanel.add(maxHypoNumLabel);
		maxHypoNumField = new JTextField("1000", defTextFieldSize);
		paramPanel.add(maxHypoNumField);

		JLabel  maxConceptLengthLabel = new JLabel("Max expression length: ", JLabel.LEFT);
		paramPanel.add(maxConceptLengthLabel);
		maxConceptLengthField = new JTextField("4", defTextFieldSize);
		paramPanel.add(maxConceptLengthField);

		JLabel  minSupportLabel = new JLabel("Min support: ", JLabel.LEFT);
		paramPanel.add(minSupportLabel);
		minSupportField = new JTextField("1", defTextFieldSize);
		paramPanel.add(minSupportField);

		JLabel  minPrecisionLabel = new JLabel("Min precision: ", JLabel.LEFT);
		paramPanel.add(minPrecisionLabel);
		minPrecisionField = new JTextField("0.9", defTextFieldSize);
		paramPanel.add(minPrecisionField);

		JLabel  emptyLabel = new JLabel(" ", JLabel.LEFT);
		paramPanel.add(emptyLabel);
		buttonRun = new JButton("run");
		buttonRun.setFont(new Font("Helvetica", Font.BOLD, 20));
		buttonRun.addActionListener(this);

		paramPanel.add(buttonRun);

//		selectentity = new OWLEntitySelectorPanel(getOWLEditorKit(), true);

		add(paramPanel);
	}

	private void createHypothesesView() {
		JPanel axiomPanel = new JPanel();
		axiomPanel.setLayout(new GridLayout(1, 1));
		axiomPanel.setPreferredSize(new Dimension(1300, 400));


		hypothesesTable = new WhatifAxiomTablePlain(getOWLModelManager(), getOWLWorkspace().getOWLSelectionModel(),
				getOWLEditorKit(), "hypotheses");
		hypothesesTable.setAxioms(new HashSet<>());
		TableColumn hypoColumn = hypothesesTable.getColumn("OWL");

		JLabel hypoLabel = new JLabel("hypotheses");
		hypoColumn.setHeaderValue(hypoLabel);

		TableColumn supportColumn = new TableColumn();
		JLabel supLabel = new JLabel("support");
		supportColumn.setHeaderValue(supLabel);
		hypothesesTable.addColumn(supportColumn);

		JScrollPane axiomScrollPanel = new JScrollPane(hypothesesTable);
		axiomPanel.add(axiomScrollPanel);

		buttonExport = new JButton("export");
		buttonExport.setFont(new Font("Helvetica", Font.BOLD, 20));
		buttonExport.setPreferredSize(new Dimension(200, 30));
		buttonExport.addActionListener(this);

		add(axiomPanel);
		add(buttonExport);
	}


	protected ActionListener getThis() {
		return this;
	}

	@Override
	protected void disposeOWLView() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(buttonRun)) {
			mineHypotheses();
		} else if (e.getSource().equals(buttonExport)) {
			exportHypotheses();
		}

	}

	private void exportHypotheses() {
		FileDialog fd = new FileDialog((Frame) SwingUtilities.getRoot(this), "Choose a file", FileDialog.SAVE);
		fd.setVisible(true);
		String filename = fd.getFile();
		if (filename == null) {
			JOptionPane.showMessageDialog(this, "The export is cancelled...");
		} else {
			Set<OWLAxiom> selectedHypotheses = hypothesesTable.getAllAxioms();
			if (selectedHypotheses.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Empty output, aborting...");
			} else {
				IRI moduleIRI = IRI.create("http://owl.cs.man.ac.uk/dlminer_" + UUID.randomUUID().toString());
				try {
					OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(selectedHypotheses, moduleIRI);
					OutputStream os = new FileOutputStream(new File(fd.getDirectory(), filename));
					o.getOWLOntologyManager().saveOntology(o, os);
					log.info(selectedHypotheses.size()
							+ " selected hypotheses are successfully exported to " + filename);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Cannot export hypotheses, see log...");
					e.printStackTrace();
				}
			}
		}

	}

	private void mineHypotheses() {

		OWLOntology ontology = getOWLModelManager().getActiveOntology();
		DLMinerInput input = new DLMinerInput(ontology);

		Integer hypothesesNumber = Integer.parseInt(maxHypoNumField.getText());
		Integer maxConceptLength = Integer.parseInt(maxConceptLengthField.getText());
		Double minPrecision = Double.parseDouble(minPrecisionField.getText());
		Integer minSupport = Integer.parseInt(minSupportField.getText());

		input.setMaxHypothesesNumber(hypothesesNumber);
		input.setMinPrecision(minPrecision);

		// language bias
		OperatorConfig config = input.getConfig();
		config.maxLength = maxConceptLength;
		config.minSupport = minSupport;

		// run DL-Miner
		DLMiner miner = new DLMiner(input);
		try {
			miner.init();
			miner.run();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "DL-Miner cannot finish processing, see log...");
			e.printStackTrace();
		}

		final Collection<Hypothesis> hypotheses = miner.getOutput().getHypotheses();
		Set<OWLAxiom> axioms = new HashSet<>();
		for (Hypothesis h : hypotheses) {
			axioms.addAll(h.axioms);
		}

		hypothesesTable.setAxioms(axioms);

		log.info("DL-Miner has mined " + axioms.size() + " hypotheses");

	}

}
