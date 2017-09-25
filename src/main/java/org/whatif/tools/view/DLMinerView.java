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
	JCheckBox checkAllBox = null;

	// hypotheses view
	OWLHypothesesView hypothesesTable = null;

	OWLEntitySelectorPanel entityPanel = null;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		createParametersView();

		createHypothesesView();

		log.info("DL-Miner view is initialized");

	}

	private void createParametersView() {

		Font defFont = this.getFont();
		Font boldFont = new Font(defFont.getFontName(), Font.BOLD, defFont.getSize());

		JPanel step123Panel = new JPanel();
		GroupLayout layout = new GroupLayout(step123Panel);
        step123Panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

        JLabel  chooseParamsLabel= new JLabel("Step 1: Choose parameters", JLabel.LEFT);
        chooseParamsLabel.setFont(boldFont);

        JLabel  chooseFocusLabel= new JLabel("Step 2: Choose focus terms (optional)", JLabel.LEFT);
        chooseFocusLabel.setFont(boldFont);

        JLabel  runLabel= new JLabel("Step 3: Run DL-Miner", JLabel.LEFT);
        runLabel.setFont(boldFont);

		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new GridLayout(4, 2));
		paramPanel.setPreferredSize(new Dimension(200, 200));

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

		entityPanel = new OWLEntitySelectorPanel(getOWLEditorKit(), true);
		entityPanel.setPreferredSize(new Dimension(500, 300));

        buttonRun = new JButton("RUN");
        buttonRun.setFont(boldFont);
		buttonRun.setMinimumSize(new Dimension(200, 50));
        buttonRun.addActionListener(this);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooseParamsLabel)
								.addComponent(paramPanel)
								.addComponent(runLabel)
								.addComponent(buttonRun))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooseFocusLabel)
								.addComponent(entityPanel))

		);

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(chooseParamsLabel)
								.addComponent(paramPanel)
								.addComponent(runLabel)
								.addComponent(buttonRun))
						.addGroup(layout.createSequentialGroup()
								.addComponent(chooseFocusLabel)
								.addComponent(entityPanel))
		);

		add(step123Panel);
	}

	private void createHypothesesView() {
		JPanel step45Panel = new JPanel();
		GroupLayout layout = new GroupLayout(step45Panel);
		step45Panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		Font defFont = this.getFont();
		Font boldFont = new Font(defFont.getFontName(), Font.BOLD, defFont.getSize());

		JLabel  hypoLabel = new JLabel("Step 4: Explore and select hypotheses", JLabel.LEFT);
		hypoLabel.setFont(boldFont);

		JPanel axiomPanel = new JPanel();
		axiomPanel.setLayout(new GridLayout(1, 1));
		axiomPanel.setPreferredSize(new Dimension(1000, 400));
		hypothesesTable = new OWLHypothesesView(getOWLModelManager(),
				getOWLWorkspace().getOWLSelectionModel(),
				getOWLEditorKit());
		hypothesesTable.setHypotheses(new HashSet<>());
		JScrollPane axiomScrollPanel = new JScrollPane(hypothesesTable);
		axiomPanel.add(axiomScrollPanel);

		JLabel  addLabel = new JLabel("Step 5: Add or save hypotheses", JLabel.LEFT);
		addLabel.setFont(boldFont);

		buttonAdd = new JButton("ADD");
		buttonAdd.setFont(boldFont);
		buttonAdd.setMinimumSize(new Dimension(200, 50));
		buttonAdd.addActionListener(this);

		buttonRemove = new JButton("REMOVE");
		buttonRemove.setFont(boldFont);
		buttonRemove.setMinimumSize(new Dimension(200, 50));
		buttonRemove.addActionListener(this);

		buttonExport = new JButton("SAVE");
		buttonExport.setFont(boldFont);
		buttonExport.setMinimumSize(new Dimension(200, 50));
		buttonExport.addActionListener(this);

		JLabel checkAllLabel = new JLabel("Select all", JLabel.LEFT);
		checkAllLabel.setFont(boldFont);

		checkAllBox = new JCheckBox();
		checkAllBox.addActionListener(this);


		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(hypoLabel)
							.addComponent(axiomPanel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(addLabel)
								.addComponent(buttonAdd)
								.addComponent(buttonRemove)
								.addComponent(buttonExport)
								.addComponent(checkAllLabel)
								.addComponent(checkAllBox)
						));

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(hypoLabel)
								.addComponent(axiomPanel))
						.addGroup(layout.createSequentialGroup()
								.addComponent(addLabel)
								.addComponent(buttonAdd)
								.addComponent(buttonRemove)
								.addComponent(buttonExport)
								.addComponent(checkAllLabel)
								.addComponent(checkAllBox)
						));

		add(step45Panel);
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
		} else if (e.getSource().equals(checkAllBox)) {
			hypothesesTable.setAllCheckboxes(checkAllBox.isSelected());
		}

	}



	private void addHypotheses() {
		Set<OWLAxiom> selectedHypotheses = hypothesesTable.getSelectedAxioms();
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
		Set<OWLAxiom> selectedHypotheses = hypothesesTable.getSelectedAxioms();
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
			Set<OWLAxiom> selectedHypotheses = hypothesesTable.getSelectedAxioms();
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
