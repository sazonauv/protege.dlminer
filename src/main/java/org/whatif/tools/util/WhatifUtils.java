package org.whatif.tools.util;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

//import uk.ac.manchester.cs.diff.Ecco;

public class WhatifUtils {
	
	public static void p(Object o) {
		System.out.println(o);
	}
	
	public static OWLEntity getPrimaryEntityOfAxiom(OWLAxiom ax) {
		if (ax instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom axe = (OWLEquivalentClassesAxiom) ax;
			for (OWLClassExpression a : axe.getClassExpressions()) {
				if (a.isClassExpressionLiteral()) {
					return (OWLClass) a;
				}
			}

		} else if (ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom axe = (OWLSubClassOfAxiom) ax;
			for (OWLClassExpression a : axe.getSubClass().getNestedClassExpressions()) {
				if (a.isClassExpressionLiteral()) {
					return (OWLClass) a;
				}
			}
		} else {
			// TODO
			for (OWLEntity a : ax.getClassesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getObjectPropertiesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getDataPropertiesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getIndividualsInSignature()) {
				return a;
			}
		}
		return null;
	}

	public void sleep() {
		try {
			Thread.sleep(25);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Set<OWLEntity> getSignature(Set<OWLAxiom> axioms) {
		Set<OWLEntity> signature = new HashSet<OWLEntity>();
		for(OWLAxiom ax:axioms) {
			signature.addAll(ax.getSignature());
		}
		return signature;
	}
	
	public static boolean directlyDisjointWith(OWLReasoner r, OWLClassExpression ce0, OWLClassExpression ce1) {
		return disjointWithLR(r, ce0, ce1)&&disjointWithLR(r, ce1, ce0);
	}
	
	private static boolean disjointWithLR(OWLReasoner r, OWLClassExpression ce0, OWLClassExpression ce1) {
		Set<OWLClass> d0 = r.getDisjointClasses(ce0).getFlattened();
		d0.remove(ce0);
		Set<OWLClass> s1 = r.getSuperClasses(ce1, false).getFlattened();
		s1.remove(ce1);
		d0.retainAll(s1);
		return d0.isEmpty();
	}

	private String stripIRI(OWLAxiom axiom) {
		String ax = axiom.toString();
		Set<String> iris = new HashSet<String>();
		for (OWLEntity e : axiom.getSignature()) {
			if (!e.getIRI().getFragment().isEmpty()) {
				iris.add(e.getIRI().toString().replaceAll(e.getIRI().getFragment(), ""));
			}
		}
		for (String iri : iris) {
			ax = ax.replaceAll(iri, "");
		}
		return ax;
	}
	
	public static Set<OWLEntity> getPrimaryAxiomEntities(OWLAxiom ax) {
		Set<OWLEntity> signature = new HashSet<OWLEntity>();
		if(ax instanceof OWLSubClassOfAxiom) {
			signature.addAll(((OWLSubClassOfAxiom)ax).getSubClass().getSignature());
		}
		else if(ax instanceof OWLSubPropertyAxiom) {
			signature.addAll(((OWLSubPropertyAxiom)ax).getSubProperty().getSignature());
		}
		else {
			signature.addAll(ax.getSignature());
		}
		return signature;
	}
	
	private Set<OWLEntity> getAffectedSuperClasses(Set<OWLAxiom> axs,OWLModelManager man) {
		WhatifUtils.p("getAffectedSuperClasses()");
		Set<OWLEntity> affected = new HashSet<OWLEntity>();
		/*
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(man
				.getActiveOntology().getAxioms());
		axioms.removeAll(axs);
		Ecco ecco = analyseDiff(axioms,man);
		if (ecco == null) {
			//JOptionPane.showMessageDialog(this, "Computing Differences Failed!");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Structurally Equivalent: "
					+ ecco.areStructurallyEquivalent() + "\n");
			sb.append("Concept Changes: " + ecco.foundChangesToConcepts()
					+ "\n");
			for(OWLClass conceptchange:ecco.getConceptChanges().getRHSGeneralisedConcepts()) {
				sb.append("Generalised concepts: " + conceptchange.getIRI().getFragment()
						+ "\n");
			}
			//JOptionPane.showMessageDialog(this, sb.toString());
		}
		for (OWLAxiom ax : axioms)
			affected.addAll(ax.getClassesInSignature());
			*/
		return affected;
	}
	


	private void analyseDiff(OWLModelManager man,Set<OWLAxiom> selectedaxioms) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(man
				.getActiveOntology().getAxioms());
		/*if (moduleaxioms.getSelectedIndices().length == 0) {
			moduleaxioms.setAllAxiomsSelected();
		}
		Set<OWLAxiom> selection = selectedaxioms;
		axioms.removeAll(selection);
		Ecco ecco = analyseDiff(axioms,man);
		if (ecco == null) {
			//JOptionPane.showMessageDialog(this, "Computing Differences Failed!");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Structurally Equivalent: "
					+ ecco.areStructurallyEquivalent() + "\n");
			sb.append("Concept Changes: " + ecco.foundChangesToConcepts()
					+ "\n");
			//JOptionPane.showMessageDialog(this, sb.toString());
		}
		*/
	}
/*
	private Ecco analyseDiff(Set<OWLAxiom> axioms,OWLModelManager man) {
		OWLOntology ont1;
		try {
			ont1 = OWLManager.createOWLOntologyManager().createOntology(axioms);
			Ecco ecco = new Ecco(ont1,man.getActiveOntology());
			ecco.computeDiff();
			return ecco;
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
*/
	public static void e(String string) {
		System.err.println(string);
	}

}
