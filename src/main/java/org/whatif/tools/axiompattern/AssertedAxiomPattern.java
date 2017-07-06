package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.util.TautologyManager;

public class AssertedAxiomPattern implements AxiomPattern {

	AssertedAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object o) {
		//System.out.println("ASsserted: "+ax.toString());
		if(!(o instanceof Set<?>)) {
			return false;
		}
		
		Set set = (Set)o;
		//System.out.println("SET: "+set.size());
		//System.out.println("In it?: "+set.contains(ax));
		return set.contains(ax);
	}
	
	
	
	@Override
	public String toString() {
		return "Asserted Axiom";
	}

	@Override
	public int getWeight() {
		return 100;
	}

}
