package org.whatif.tools.consequence;

import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.whatif.tools.axiompattern.AxiomPattern;
import org.whatif.tools.axiompattern.AxiomPatternFactory;
import org.whatif.tools.view.EntailmentInspectorView;

public class WhatifAddedAssertionConsequence implements WhatifConsequence {

	final OWLAxiom axiom;
	final Set<AxiomPattern> matchingpatterns;
	
	
	public WhatifAddedAssertionConsequence(OWLAxiom ax, Set<AxiomPattern> matchingpatterns) {
		this.axiom = ax;
		this.matchingpatterns = matchingpatterns;
	}

	public OWLAxiom getOWLAxiom() {
		return axiom;
	}
	
	public AxiomType getOWLAxiomType() {
		return axiom.getAxiomType();
	}
	
	public String toString() {
		return getConsequenceDescription();
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((axiom == null) ? 0 : axiom.hashCode());
		result = prime * result + ((matchingpatterns == null) ? 0 : matchingpatterns.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof WhatifAddedAssertionConsequence)) {
			return false;
		}
		WhatifAddedAssertionConsequence other = (WhatifAddedAssertionConsequence) obj;
		if (axiom == null) {
			if (other.axiom != null) {
				return false;
			}
		} else if (!axiom.equals(other.axiom)) {
			return false;
		}
		if (matchingpatterns == null) {
			if (other.matchingpatterns != null) {
				return false;
			}
		} else if (!matchingpatterns.equals(other.matchingpatterns)) {
			return false;
		}
		return true;
	}

	@Override
	public String getConsequenceType() {
		return getHighestPriorityAxiomPattern().toString();
	}

	@Override
	public String getConsequenceDescription() {
		return EntailmentInspectorView.getRenderer().render(getOWLAxiom());
	}

	@Override
	public AxiomPattern getHighestPriorityAxiomPattern() {
		AxiomPattern at_out = AxiomPatternFactory.getDefaultPattern();
		for(AxiomPattern at:matchingpatterns) {
			if(at_out==null) {
				at_out=at;
			} else if(at_out.getWeight()<at.getWeight()) {
				at_out = at;
			}
		}
		return at_out;
	}
	
}