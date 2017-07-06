package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.util.OWLOntologyAxiomSelector;
import org.whatif.tools.util.TautologyManager;

public class TautologyAxiomPattern implements AxiomPattern {

	TautologyAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object o) {
		if(!(o instanceof OWLReasoner)) {
			return false;
		}
		TautologyManager tm = (TautologyManager)o;
		return tm.isTautology(ax);
	}
	
	@Override
	public String toString() {
		return "Tautology Added";
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
