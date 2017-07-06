package org.whatif.tools.axiompattern;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.consequence.WhatifConsequence;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.util.WhatifUtils;
import org.whatif.tools.view.EntailmentInspectorView;

public class AddedAxiomRedundantPattern implements AxiomPattern {

	AddedAxiomRedundantPattern() {
	}
	
	@Override
	public String toString() {
		return "Axiom was already implied";
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object r) {
		Set<Explanation<OWLAxiom>> expl = new HashSet<Explanation<OWLAxiom>>();
		try {
			expl.addAll(EntailmentInspectorView.getExplanationGenerator()
				.getExplanations(ax, 2));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		for (Explanation<OWLAxiom> ex : expl) {
			if (!ex.getAxioms().contains(ax)) {
				//There is at least one Non self justification
				//WhatifUtils.p("There is at least one justification not involving AX "+ax+": "+ex.toString());
				return true;
			}
		}
		return false;
	}

}
