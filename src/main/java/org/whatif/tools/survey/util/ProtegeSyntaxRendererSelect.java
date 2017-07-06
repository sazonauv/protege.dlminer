package org.whatif.tools.survey.util;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.util.SimpleRenderer;

public class ProtegeSyntaxRendererSelect implements AxiomRendererSelect {

	OWLObjectRenderer ren = new SimpleRenderer();
	
	@Override
	public OWLObjectRenderer getRenderer() {
		return ren;
	}

	@Override
	public String toString() {
		return "Verbose Syntax";
	}
	
}
