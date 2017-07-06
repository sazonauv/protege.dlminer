package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredDataPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredSubDataPropertyAxiomGenerator
		extends InferredDataPropertyAxiomGenerator<OWLSubDataPropertyOfAxiom> {

	final boolean direct;

	public InferredSubDataPropertyAxiomGenerator(boolean direct) {
		this.direct = direct;
	}

	@Override
	protected void addAxioms(OWLDataProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLSubDataPropertyOfAxiom> result) {
		System.out.println("P: " + entity.getIRI().getFragment());
		for (OWLDataProperty sup : reasoner.getSuperDataProperties(entity, direct).getFlattened()) {
			result.add(dataFactory.getOWLSubDataPropertyOfAxiom(entity, sup));
		}
	}

	@Override
	protected Set<OWLDataProperty> getEntities(OWLOntology ont) {
		Set<OWLDataProperty> s = ont.getDataPropertiesInSignature();
		if (!s.isEmpty()) {
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLBottomDataProperty());
			s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLTopDataProperty());
		}
		return s;
	}

	@Override
	public String getLabel() {
		return "Sub data properties";
	}
}
