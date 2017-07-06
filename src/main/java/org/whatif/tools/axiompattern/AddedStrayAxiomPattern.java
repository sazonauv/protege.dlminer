package org.whatif.tools.axiompattern;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.util.OWLOntologyAxiomSelector;

public class AddedStrayAxiomPattern implements AxiomPattern {

	AddedStrayAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {

		if (!(o instanceof OWLReasoner)) {
			return false;
		}
		OWLReasoner r = (OWLReasoner) o;
		Set<OWLEntity> signature = new HashSet<OWLEntity>(ax.getSignature());
		Set<OWLEntity> rem = new HashSet<OWLEntity>();
		for (OWLEntity e : signature) {
			if (e.isBuiltIn()) {
				rem.add(e);
			}
		}
		signature.removeAll(rem);
		//System.out.println("Stray..S: " + signature.size() + " " + ax.toString());
		//System.out.println("Stray..S2: ");
		if (signature.size() == 1) {
			Set<OWLAxiom> asserted = OWLOntologyAxiomSelector.getLogicalAxiomsInClosure(r.getRootOntology());
			for (OWLEntity e : signature) {
				for (OWLAxiom axiom : asserted) {
					if (!ax.equals(axiom)) {
						//System.out.println("Stray...SX: "+axiom.toString());

						if (axiom.getSignature().contains(e)) {
							//System.out.println("Stray...T:");

							return false;
						} else {
							//System.out.println("Stray...F");

						}
					}
				}
			}

		} else {
			return false;
		}
		//System.out.println("KLO");
		return true;
	}

	@Override
	public String toString() {
		return "Added Stray Entity";
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
