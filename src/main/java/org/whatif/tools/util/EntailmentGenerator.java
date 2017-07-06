package org.whatif.tools.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.whatif.tools.axiomgenerator.*;


public class EntailmentGenerator {

	public static Set<OWLAxiom> getInferredAxioms(OWLReasoner r, OWLOntology o,
			Set<AxiomType> types, boolean inferredonly) {
		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (AxiomType type : types) {
			if (type.equals(AxiomType.SUBCLASS_OF)) {
				WhatifUtils.p("SUBCLASS_OF");
				axioms.addAll(generateInferrences(r, o,
						new InferredSubClassAxiomGenerator(true)));
			}
			else if (type.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
				WhatifUtils.p("SUB_OBJECT_PROPERTY");
				axioms.addAll(generateInferrences(r, o,
						new InferredSubObjectPropertyAxiomGenerator(true)));
			}
			else if (type.equals(AxiomType.SUB_DATA_PROPERTY)) {
				WhatifUtils.p("SUB_DATA_PROPERTY");
				axioms.addAll(generateInferrences(r, o,
						new InferredSubDataPropertyAxiomGenerator(true)));
			}
			else if (type.equals(AxiomType.DISJOINT_CLASSES)) {
				WhatifUtils.p("DISJOINT_CLASSES");
				axioms.addAll(generateInferrences(r, o,
						new InferredDisjointClassesAxiomGenerator(false)));
			}
			else if (type.equals(AxiomType.CLASS_ASSERTION)) {
				WhatifUtils.p("CLASS_ASSERTION");
				axioms.addAll(generateInferrences(r, o,
						new InferredClassAssertionAxiomGenerator(false)));
			}
			else if (type.equals(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
				WhatifUtils.p("OBJECT_PROPERTY_ASSERTION");
				axioms.addAll(generateInferrences(r, o,
						new InferredObjectPropertyAssertionGenerator()));
			}
			else if (type.equals(AxiomType.DATA_PROPERTY_ASSERTION)) {
				WhatifUtils.p("DATA_PROPERTY_ASSERTION");
				axioms.addAll(generateInferrences(r, o,
						new InferredDataPropertyAssertionGenerator()));
			}
			else if (type.equals(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
				WhatifUtils.p("INVERSE_OBJECT_PROPERTIES");
				axioms.addAll(generateInferrences(r, o,
						new InferredInverseObjectPropertiesAxiomGenerator()));
			}
			else if (type.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)||type.equals(AxiomType.SYMMETRIC_OBJECT_PROPERTY)||type.equals(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)||type.equals(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)||type.equals(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)||type.equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)||type.equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
				WhatifUtils.p("TRANSITIVE_OBJECT_PROPERTY");
				axioms.addAll(generateInferrences(r, o,
						new InferredObjectPropertyCharacteristicAxiomGenerator()));
			}
			else if (type.equals(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
				WhatifUtils.p("FUNCTIONAL_DATA_PROPERTY");
				axioms.addAll(generateInferrences(r, o,
						new InferredDataPropertyCharacteristicAxiomGenerator()));
			}
			else if (type.equals(AxiomType.EQUIVALENT_CLASSES)) {
				WhatifUtils.p("EQUIVALENT_CLASSES");
				axioms.addAll(generateInferrences(r, o,
						new InferredEquivalentClassAxiomGenerator()));
			}
			else if (type.equals(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
				WhatifUtils.p("EQUIVALENT_DATA_PROPERTIES");
				axioms.addAll(generateInferrences(r, o,
						new InferredEquivalentDataPropertiesAxiomGenerator()));
			}
			else if (type.equals(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
				WhatifUtils.p("EQUIVALENT_OBJECT_PROPERTIES");
				axioms.addAll(generateInferrences(r, o,
						new InferredEquivalentObjectPropertyAxiomGenerator()));
			}
			else {
				WhatifUtils.p("Unsupported Axiom Type " + type.getName());
			}
		}
		if (inferredonly) {
			axioms.removeAll(OWLOntologyAxiomSelector
					.getAxiomsWithoutAnnotions(OWLOntologyAxiomSelector.getLogicalAxiomsInClosure(o)));
		}
		return axioms;
	}

	private static Collection<? extends OWLAxiom> generateInferrences(OWLReasoner r,
			OWLOntology o, InferredAxiomGenerator<? extends OWLAxiom> gen) {
		// List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new
		// ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		// gens.add(gen);
		return gen.createAxioms(o.getOWLOntologyManager().getOWLDataFactory(), r);
	}

}
