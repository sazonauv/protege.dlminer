package org.whatif.tools.axiomgenerator;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredDataPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredEquivalentDataPropertiesAxiomGenerator
		extends InferredDataPropertyAxiomGenerator<OWLEquivalentDataPropertiesAxiom> {

	@Override
	protected void addAxioms(OWLDataProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLEquivalentDataPropertiesAxiom> result) {
		System.out.println("P: " + entity.getIRI().getFragment());
		Set<OWLDataProperty> props = new HashSet<OWLDataProperty>(
				reasoner.getEquivalentDataProperties(entity).getEntities());
		props.add(entity);
		if (props.size() > 1) {
			result.add(dataFactory.getOWLEquivalentDataPropertiesAxiom(props));
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
		return "Equivalent data properties";
	}
}