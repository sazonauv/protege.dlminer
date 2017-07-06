package org.whatif.tools.util;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.whatif.tools.axiompattern.ProfileAxiomPattern;

public interface ProfileVilationProvider {
	
	Collection<OWLProfileViolation> getViolations(Class<? extends ProfileAxiomPattern> cl);
}
