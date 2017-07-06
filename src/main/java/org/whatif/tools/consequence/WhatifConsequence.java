package org.whatif.tools.consequence;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.whatif.tools.axiompattern.AxiomPattern;

public interface WhatifConsequence {
	
	public String getConsequenceType();

	public String getConsequenceDescription();
	
	public OWLAxiom getOWLAxiom();

	AxiomPattern getHighestPriorityAxiomPattern();

}
