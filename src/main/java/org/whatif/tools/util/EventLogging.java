package org.whatif.tools.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLAxiom;


public class EventLogging {
	private static boolean prepared = false;
	private static FileWriter writer;
	private static File logfile;
	private static OWLObjectRenderer ren = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	

	public static void prepare(File logf) throws IOException {
		logfile = logf;
		writer = new FileWriter(logfile,true);
		prepared = true;
	}

	public static void clear() {
		prepared = false;
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writer = null;
	}
	
	public static void saveEvent(long timestamp, String tab, String event, String object, String location) {
		if (!prepared) {
			return;
		}
		
		StringBuilder sb = new StringBuilder().append(timestamp+",")
				.append(tab).append(",").append(event).append(",")
				.append(object).append(",").append(location).append("\n");
		try {
			writer.write(sb.toString());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String render(OWLAxiom owlAxiom) {
		if (!prepared) {
			return "";
		}
		return ren.render(owlAxiom);
	}

}
