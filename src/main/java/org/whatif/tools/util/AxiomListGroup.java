package org.whatif.tools.util;

public enum AxiomListGroup {
	
	NONE("No grouping"),ENTITY("By Entity"),AXIOMTYPE("By Axiomtype");
	private final String name;
	
	AxiomListGroup(String name) {
		this.name=name;
	}
	
	public String toString() {
		return name;
	}
}

