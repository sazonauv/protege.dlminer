package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;
import org.whatif.tools.util.WhatifUtils;

/**
 * Generates inferred disjoint axioms - note that this currently uses a very
 * simple inefficient algorithm.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

	final boolean direct;

	public InferredDisjointClassesAxiomGenerator(boolean direct) {
		this.direct = direct;
	}

	@Override
	protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
			Set<OWLDisjointClassesAxiom> result) {
		for (OWLClass cl : reasoner.getDisjointClasses(entity).getFlattened()) {
			if (direct) {
				if (WhatifUtils.directlyDisjointWith(reasoner, entity, cl)) {
					if (!result.contains(dataFactory.getOWLDisjointClassesAxiom(cl, entity))) {
						result.add(dataFactory.getOWLDisjointClassesAxiom(entity, cl));
					}
				}
			} else {
				if (!result.contains(dataFactory.getOWLDisjointClassesAxiom(cl, entity))) {
					result.add(dataFactory.getOWLDisjointClassesAxiom(entity, cl));
				}
			}
		}
	}

	@Override
	public String getLabel() {
		return "Disjoint classes";
	}
}