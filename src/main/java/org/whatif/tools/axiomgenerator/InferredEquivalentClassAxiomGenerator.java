package org.whatif.tools.axiomgenerator;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;

/**
 * Generates inferred equivalent classes axioms.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredEquivalentClassAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

	@Override
	protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLEquivalentClassesAxiom> result) {
		Set<OWLClassExpression> equivalentClasses = new HashSet<OWLClassExpression>(
				reasoner.getEquivalentClasses(entity).getEntities());
		equivalentClasses.add(entity);
		if (equivalentClasses.size() > 1) {
			result.add(dataFactory.getOWLEquivalentClassesAxiom(equivalentClasses));
		}
	}

	@Override
	protected Set<OWLClass> getEntities(OWLOntology ont) {
		Set<OWLClass> s = ont.getClassesInSignature();
		if (!s.isEmpty()) {
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLNothing());
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
		}
		return s;
	}

	@Override
	public String getLabel() {
		return "Equivalent classes";
	}
}