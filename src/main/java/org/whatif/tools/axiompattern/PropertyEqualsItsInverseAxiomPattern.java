package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class PropertyEqualsItsInverseAxiomPattern implements AxiomPattern {

	PropertyEqualsItsInverseAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object r) {
		if (ax instanceof OWLEquivalentObjectPropertiesAxiom) {
			OWLEquivalentObjectPropertiesAxiom eax = (OWLEquivalentObjectPropertiesAxiom) ax;
			Set<OWLObjectPropertyExpression> props = eax.getProperties();
			if (props.size() == 2) {
				boolean oneplain = false;
				boolean oneinverse = false;
				for (OWLObjectPropertyExpression e : props) {
					if (e instanceof OWLObjectProperty) {
						oneplain = true;
					} else if (e instanceof OWLObjectInverseOf) {
						if (!((OWLObjectInverseOf) e).getInverse().isAnonymous()) {
							oneinverse = true;
						}
					}
				}
				return (oneplain && oneinverse);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Property equals inverse";
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
