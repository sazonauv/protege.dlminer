package org.whatif.tools.util;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.consequence.WhatifConsequence;

public class ReasonerRunDiff {

	final long id;

	final Set<OWLAxiom> asserted = new HashSet<OWLAxiom>();
	final Set<WhatifInferenceConsequence> inferred = new HashSet<WhatifInferenceConsequence>();
	final Set<WhatifConsequence> consequences = new HashSet<WhatifConsequence>();
	final Set<AxiomType> inferred_types = new HashSet<AxiomType>();
	long reasoningtime = 0;

	public ReasonerRunDiff(long id, Set<OWLAxiom> asserted,Set<WhatifInferenceConsequence> inferred, Set<AxiomType> inferred_types) {
		this.asserted.addAll(asserted);
		this.inferred.addAll(inferred);
		this.inferred_types.addAll(inferred_types);
		this.id = id;
	}

	public Set<OWLAxiom> getAssertions() {
		return asserted;
	}
	
	public Set<WhatifInferenceConsequence> getInferences() {
		return inferred;
	}
	
	public Set<AxiomType> getAxiomTypes() {
		return inferred_types;
	}
	
	public String getDate() {
		return toString();
	}
	
	@Override
	public String toString() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(id);		
		return calendar.getTime().toString();
	}

	public void addAddedAssertionConsequences(Set<WhatifConsequence> addition_consequences) {
		consequences.addAll(addition_consequences);		
	}

	public Set<WhatifConsequence> getAddedAssertionConsequences() {
		return consequences;
	}

	public void setReasoningTime(long startreasonertime, long endreasonertime) {
		this.reasoningtime = endreasonertime-startreasonertime;
	}
	
	public long getReasoningTime() {
		return reasoningtime;
	}

	public String getReasoningTimeSec() {
		return Math.round((double)getReasoningTime()/1000.00)+" sec";
	}
}
