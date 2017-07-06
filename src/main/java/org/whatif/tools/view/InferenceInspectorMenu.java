package org.whatif.tools.view;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * This plugin doesn't really do much and is intended to be deleted when a developer creates his own content.
 * 
 * @author redmond
 *
 */
public class InferenceInspectorMenu extends ProtegeOWLAction {
	private static final long serialVersionUID = 749843192372192393L;
	private Logger logger = Logger.getLogger(InferenceInspectorMenu.class);

	@Override
	public void initialise() throws Exception {		
	}

	@Override
	public void dispose() throws Exception {		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		logger.info("Inference Inspector Menu invoked");
		OWLWorkspace workspace = getOWLWorkspace();
		StringBuilder message = new StringBuilder();
		message.append("Inference Inspector"+"\n"+"\n");
		message.append("Making consequences of modelling actions explicit."+"\n"+"\n");
		message.append("2016, University of Manchester, Whatif Project"+"\n");
		message.append("Published under GNU Lesser General Public License"+"\n");
		message.append("https://github.com/matentzn/inference-inspector"+"\n");
		JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, workspace);
		JOptionPane.showMessageDialog(parent, message.toString());
	}


}
