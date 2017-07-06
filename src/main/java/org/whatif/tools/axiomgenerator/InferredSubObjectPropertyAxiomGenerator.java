package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredObjectPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredSubObjectPropertyAxiomGenerator
		extends InferredObjectPropertyAxiomGenerator<OWLSubObjectPropertyOfAxiom> {

	final boolean direct;

	public InferredSubObjectPropertyAxiomGenerator(boolean direct) {
		this.direct = direct;
	}

	@Override
	protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLSubObjectPropertyOfAxiom> result) {
		for (OWLObjectPropertyExpression sup : reasoner.getSuperObjectProperties(entity, direct).getFlattened()) {
			result.add(dataFactory.getOWLSubObjectPropertyOfAxiom(entity, sup));
		}
	}

	@Override
	protected Set<OWLObjectProperty> getEntities(OWLOntology ont) {
		Set<OWLObjectProperty> s = ont.getObjectPropertiesInSignature();
		if (!s.isEmpty()) {
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLBottomObjectProperty());
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLTopObjectProperty());
		}
		return s;
	}

	@Override
	public String getLabel() {
		return "Sub object properties";
	}
}