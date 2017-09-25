package org.whatif.tools.view;

import io.dlminer.learn.AxiomConfig;
import io.dlminer.learn.Hypothesis;
import io.dlminer.main.DLMiner;
import io.dlminer.main.DLMinerInput;
import io.dlminer.refine.OperatorConfig;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

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
	JButton buttonAdd = null;
	JButton buttonRemove = null;

	// hypotheses view
	OWLHypothesesView hypothesesTable = null;

	OWLEntitySelectorPanel entityPanel = null;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		createParametersView();

		createHypothesesView();

		createButtonsView();

		log.info("DL-Miner view is initialized");

	}

	private void createParametersView() {
		JPanel inputPanel = new JPanel();

		JPanel step123Panel = new JPanel();
        step123Panel.setLayout(new GridLayout(2, 3));

        JLabel  chooseParamsLabel= new JLabel("Step 1: Choose parameters", JLabel.LEFT);
        chooseParamsLabel.setFont(new Font("Helvetica", Font.BOLD, 14));
        step123Panel.add(chooseParamsLabel);

        JLabel  chooseFocusLabel= new JLabel("Step 2: Choose focus terms", JLabel.LEFT);
        chooseFocusLabel.setFont(new Font("Helvetica", Font.BOLD, 14));
        step123Panel.add(chooseFocusLabel);

        JLabel  runLabel= new JLabel("Step 3: Run DL-Miner", JLabel.LEFT);
        runLabel.setFont(new Font("Helvetica", Font.BOLD, 14));
        step123Panel.add(runLabel);

		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new GridLayout(4, 2));

		final int defTextFieldSize = 7;

		JLabel  maxHypoNumLabel= new JLabel("Max hypotheses number: ", JLabel.LEFT);
		paramPanel.add(maxHypoNumLabel);
		maxHypoNumField = new JTextField("1000", defTextFieldSize);
		paramPanel.add(maxHypoNumField);

		JLabel  maxConceptLengthLabel = new JLabel("Max expression length: ", JLabel.LEFT);
		paramPanel.add(maxConceptLengthLabel);
		maxConceptLengthField = new JTextField("2", defTextFieldSize);
		paramPanel.add(maxConceptLengthField);

		JLabel  minSupportLabel = new JLabel("Min support: ", JLabel.LEFT);
		paramPanel.add(minSupportLabel);
		minSupportField = new JTextField("1", defTextFieldSize);
		paramPanel.add(minSupportField);

		JLabel  minPrecisionLabel = new JLabel("Min precision: ", JLabel.LEFT);
		paramPanel.add(minPrecisionLabel);
		minPrecisionField = new JTextField("0.9", defTextFieldSize);
		paramPanel.add(minPrecisionField);

        step123Panel.add(paramPanel);

		entityPanel = new OWLEntitySelectorPanel(getOWLEditorKit(), true);
		entityPanel.setPreferredSize(new Dimension(500, 300));

        step123Panel.add(entityPanel);


        buttonRun = new JButton("run");
        buttonRun.setFont(new Font("Helvetica", Font.BOLD, 20));
        buttonRun.addActionListener(this);

        step123Panel.add(buttonRun);

        inputPanel.add(step123Panel);

		add(inputPanel);
	}

	private void createHypothesesView() {
		JPanel axiomPanel = new JPanel();
		axiomPanel.setLayout(new GridLayout(1, 1));
		axiomPanel.setPreferredSize(new Dimension(1000, 400));
		hypothesesTable = new OWLHypothesesView(getOWLModelManager(),
				getOWLWorkspace().getOWLSelectionModel(),
				getOWLEditorKit());
		hypothesesTable.setHypotheses(new HashSet<>());
		JScrollPane axiomScrollPanel = new JScrollPane(hypothesesTable);
		axiomPanel.add(axiomScrollPanel);
		add(axiomPanel);
	}


	private void createButtonsView() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 3));
		buttonsPanel.setPreferredSize(new Dimension(600, 30));

		buttonAdd = new JButton("add");
		buttonAdd.setFont(new Font("Helvetica", Font.BOLD, 20));
		buttonAdd.setPreferredSize(new Dimension(200, 30));
		buttonAdd.addActionListener(this);

		buttonRemove = new JButton("remove");
		buttonRemove.setFont(new Font("Helvetica", Font.BOLD, 20));
		buttonRemove.setPreferredSize(new Dimension(200, 30));
		buttonRemove.addActionListener(this);

		buttonExport = new JButton("export");
		buttonExport.setFont(new Font("Helvetica", Font.BOLD, 20));
		buttonExport.setPreferredSize(new Dimension(200, 30));
		buttonExport.addActionListener(this);

		buttonsPanel.add(buttonAdd);
		buttonsPanel.add(buttonRemove);
		buttonsPanel.add(buttonExport);

		add(buttonsPanel);
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
		} else if (e.getSource().equals(buttonAdd)) {
			addHypotheses();
		} else if (e.getSource().equals(buttonRemove)) {
			removeHypotheses();
		}

	}



	private void addHypotheses() {
		Set<OWLAxiom> selectedHypotheses = hypothesesTable.getAxioms();
		OWLModelManager manager = getOWLModelManager();
		OWLOntology ontology = manager.getActiveOntology();
		List<OWLOntologyChange> changes = new ArrayList<>();
		for (OWLAxiom axiom : selectedHypotheses) {
			changes.add(new AddAxiom(ontology, axiom));
		}
		manager.applyChanges(changes);
		manager.refreshRenderer();
	}

	private void removeHypotheses() {
		Set<OWLAxiom> selectedHypotheses = hypothesesTable.getAxioms();
		OWLModelManager manager = getOWLModelManager();
		OWLOntology ontology = manager.getActiveOntology();
		List<OWLOntologyChange> changes = new ArrayList<>();
		for (OWLAxiom axiom : selectedHypotheses) {
			changes.add(new RemoveAxiom(ontology, axiom));
		}
		manager.applyChanges(changes);
		manager.refreshRenderer();
	}

	private void exportHypotheses() {
		FileDialog fd = new FileDialog((Frame) SwingUtilities.getRoot(this), "Choose a file", FileDialog.SAVE);
		fd.setVisible(true);
		String filename = fd.getFile();
		if (filename == null) {
			JOptionPane.showMessageDialog(this, "The export is cancelled");
		} else {
			Set<OWLAxiom> selectedHypotheses = hypothesesTable.getAxioms();
			if (selectedHypotheses.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Empty output, aborting");
			} else {
				IRI moduleIRI = IRI.create("http://owl.cs.man.ac.uk/dlminer_" + UUID.randomUUID().toString());
				try {
					OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(selectedHypotheses, moduleIRI);
					OutputStream os = new FileOutputStream(new File(fd.getDirectory(), filename));
					o.getOWLOntologyManager().saveOntology(o, os);
					log.info(selectedHypotheses.size()
							+ " selected hypotheses are successfully exported to " + filename);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Cannot export hypotheses, see log");
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

		// language bias
		OperatorConfig operatorConfig = input.getOperatorConfig();
		operatorConfig.maxLength = maxConceptLength;
		operatorConfig.minSupport = minSupport;

		AxiomConfig axiomConfig = input.getAxiomConfig();
		axiomConfig.minPrecision = minPrecision;
		Set<OWLEntity> selectedEntities = entityPanel.getSelectedObjects();
		if (selectedEntities != null && !selectedEntities.isEmpty()) {
			axiomConfig.seedEntities = selectedEntities;
		}

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
		hypothesesTable.setHypotheses(hypotheses);

		log.info("DL-Miner has mined " + hypotheses.size() + " hypotheses");

	}

}
