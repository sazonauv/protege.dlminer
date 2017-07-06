package org.whatif.tools.survey.util;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;


public class ManchesterSyntaxAxiomRendererSelect implements AxiomRendererSelect {

	OWLObjectRenderer ren = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	@Override
	public OWLObjectRenderer getRenderer() {
		return ren;
	}
	
	@Override
	public String toString() {
		return "Manchester Syntax";
	}
	
}
