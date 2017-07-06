package org.whatif.tools.axiomgenerator;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredObjectPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredEquivalentObjectPropertyAxiomGenerator
		extends InferredObjectPropertyAxiomGenerator<OWLEquivalentObjectPropertiesAxiom> {

	@Override
	protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLEquivalentObjectPropertiesAxiom> result) {
		Set<OWLObjectPropertyExpression> equivProps = new HashSet<OWLObjectPropertyExpression>(
				reasoner.getEquivalentObjectProperties(entity).getEntities());
		equivProps.add(entity);
		if (equivProps.size() > 1) {
			result.add(dataFactory.getOWLEquivalentObjectPropertiesAxiom(equivProps));
		}
	}

	@Override
	public String getLabel() {
		return "Equivalent object properties";
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

}