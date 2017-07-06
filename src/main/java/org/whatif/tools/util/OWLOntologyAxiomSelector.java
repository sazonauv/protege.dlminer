package org.whatif.tools.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class OWLOntologyAxiomSelector {

	public static Set<OWLAxiom> getAssertedAtomicSubsumptionsInClosure(
			OWLOntology o) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for(OWLAxiom ax:o.getLogicalAxioms()) {
			if(ax instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom sax = (OWLSubClassOfAxiom)ax;
				if(sax.getSubClass().isClassExpressionLiteral()) {
					if(sax.getSuperClass().isClassExpressionLiteral()) {
						axioms.add(ax);
					}
				}
			}
		}
		return axioms;
	}
	
	public static Set<OWLAxiom> getLogicalAxiomsInClosure(
			OWLOntology o) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for(OWLOntology ont:o.getImportsClosure())
		for(OWLAxiom ax:ont.getAxioms()) {
			if(ax.isLogicalAxiom()) {
			axioms.add(ax);
			}
		}
		return axioms;
	}

	public static Set<OWLAxiom> getAxiomsWithoutAnnotions(
			Set<? extends OWLAxiom> ax_in) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for(OWLAxiom ax:ax_in) {
			axioms.add(ax.getAxiomWithoutAnnotations());
		}
		return axioms;
	}

}
