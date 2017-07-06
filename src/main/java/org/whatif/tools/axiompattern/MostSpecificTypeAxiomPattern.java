package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class MostSpecificTypeAxiomPattern implements AxiomPattern {

	MostSpecificTypeAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object o) {
		if(!(o instanceof OWLReasoner)) {
			return false;
		}
		OWLReasoner r = (OWLReasoner)o;
		if (ax instanceof OWLClassAssertionAxiom) {
			OWLClassAssertionAxiom cax = (OWLClassAssertionAxiom)ax;
			OWLClassExpression ce = cax.getClassExpression();
			NodeSet<OWLClass> type = r.getTypes((OWLNamedIndividual) cax.getIndividual(), true);
			for(OWLClass c:type.getFlattened()) {
				if(r.getSubClasses(ce, false).containsEntity(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Most specific type";
	}

	@Override
	public int getWeight() {
		return 0;
	}
	
}
