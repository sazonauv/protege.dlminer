package org.whatif.tools.survey.util;

import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;


public class DLSyntaxAxiomRendererSelect implements AxiomRendererSelect {

	OWLObjectRenderer ren = new DLSyntaxObjectRenderer();

	@Override
	public OWLObjectRenderer getRenderer() {
		return ren;
	}
	
	@Override
	public String toString() {
		return "DL Syntax";
	}
	
}
