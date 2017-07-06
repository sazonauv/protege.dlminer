package org.whatif.tools.util;

import org.semanticweb.owlapi.model.OWLEntity;

public class OWLEntityHierarchyNode {
		
		OWLEntity e = null;
		
		public OWLEntityHierarchyNode(OWLEntity in) {
			this.e=in;
		}

		@Override
		public String toString() {
			return e.getIRI().getFragment();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((e == null) ? 0 : e.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OWLEntityHierarchyNode other = (OWLEntityHierarchyNode) obj;
			if (e == null) {
				if (other.e != null)
					return false;
			} else if (!e.equals(other.e))
				return false;
			return true;
		}
	}