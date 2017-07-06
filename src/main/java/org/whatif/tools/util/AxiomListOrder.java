package org.whatif.tools.util;

public enum AxiomListOrder {
	DEFAULT("Default Order"),ALPHABETICAL("Alphabetical");
	
	private final String name;
	
	AxiomListOrder(String name) {
		this.name=name;
	}
	
	public String toString() {
		return name;
	}
}
