package org.whatif.tools.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.consequence.WhatifInferenceConsequence;

public class TautologyManager {

	final private Set<WhatifInferenceConsequence> tautologies = new HashSet<WhatifInferenceConsequence>();
	final private Set<WhatifInferenceConsequence> non_tautologies = new HashSet<WhatifInferenceConsequence>();
	final private OWLReasoner r;
	final private OWLDataFactory df;
	
	final Map<AxiomType,Integer> failures = new HashMap<AxiomType,Integer>();

	public TautologyManager(OWLReasoner r) {
		this.r = r;
		this.df = r.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
		WhatifUtils.e("Implemeted simple tautology check for subclass.");
	}

	public void checkAxioms(Set<WhatifInferenceConsequence> axioms) {
		for (WhatifInferenceConsequence ax : axioms) {
			isTautology(ax);
		}
	}

	public boolean isTautology(WhatifInferenceConsequence ax) {
		if (tautologies.contains(ax) || non_tautologies.contains(ax)) {
			return tautologies.contains(ax);
		} else {
			if (isTautology(ax.getOWLAxiom())) {
				tautologies.add(ax);
				return true;
			} else {
				non_tautologies.add(ax);
				return false;
			}
		}
	}

	public boolean isTautology(OWLAxiom ax) {
		if (tautologies.contains(ax)) {
			return true;
		} else if (non_tautologies.contains(ax)) {
			return false;
		} else if (ax instanceof OWLEquivalentObjectPropertiesAxiom) {
			OWLEquivalentObjectPropertiesAxiom eax = (OWLEquivalentObjectPropertiesAxiom)ax;
			if(eax.getProperties().size()<=1) {
				return true;
			}
		} else if (ax instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom eax = (OWLEquivalentClassesAxiom)ax;
			if(eax.getClassExpressions().size()<=1) {
				return true;
			}
		} else if (ax instanceof OWLEquivalentDataPropertiesAxiom) {
			OWLEquivalentDataPropertiesAxiom eax = (OWLEquivalentDataPropertiesAxiom)ax;
			if(eax.getProperties().size()<=1) {
				return true;
			}
		}	else if (ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom eax = (OWLSubClassOfAxiom)ax;
			if(eax.getSuperClass().equals(df.getOWLThing())) {
				return true;
			} else if(eax.getSuperClass().equals(eax.getSubClass())) {
				return true;
			} else {
				
				return false;
			}
		}
		else if (failures.containsKey(ax.getAxiomType())) {
			if (failures.get(ax.getAxiomType())>10) {
				return false;
			}
		}
		try {
			return r.isEntailed(ax);
		} catch (Exception e) {
			//e.printStackTrace();
			if(!failures.containsKey(ax.getAxiomType())) {
				failures.put(ax.getAxiomType(), 0);
			}
			failures.put(ax.getAxiomType(),failures.get(ax.getAxiomType())+1);
			//WhatifUtils.p("Failed to determine if tautology: "+e.getClass()+" "+e.getLocalizedMessage()+" "+ax.toString());
			return false;
		}
	}

	public void clear() {
		tautologies.clear();
		non_tautologies.clear();
		failures.clear();
	}
}
