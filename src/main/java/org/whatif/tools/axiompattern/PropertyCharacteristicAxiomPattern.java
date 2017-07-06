package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class PropertyCharacteristicAxiomPattern implements AxiomPattern {

	PropertyCharacteristicAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object r) {
		return (ax instanceof OWLObjectPropertyCharacteristicAxiom);
	}

	@Override
	public String toString() {
		return "Propery Characteristic";
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
