package org.whatif.tools.axiompattern;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

public class ViolatesOWLDLAxiomPattern implements AxiomPattern, ProfileAxiomPattern {

	ViolatesOWLDLAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {
		if (!(o instanceof OWLProfileReport)) {
			return false;
		}
		// Unfortunately I have to generate the Report from the outside, to
		// avoid having to redo it for every call.
		OWLProfileReport prof = (OWLProfileReport) o;
		List<OWLProfileViolation> l = prof.getViolations();
		for (OWLProfileViolation vo : l) {
			if (vo.getAxiom().equals(ax)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "OWL DL Profile Violation";
	}

	@Override
	public int getWeight() {
		return -1;
	}

}
