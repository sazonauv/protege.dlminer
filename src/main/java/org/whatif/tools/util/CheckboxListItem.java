package org.whatif.tools.util;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.whatif.tools.view.EntailmentInspectorView;

class CheckboxListItem {
	   private OWLAxiom con;
	   private boolean isSelected = false;
	 
	   public CheckboxListItem(OWLAxiom con) {
	      this.con = con;
	   }
	 
	   public boolean isSelected() {
	      return isSelected;
	   }
	 
	   public void setSelected(boolean isSelected) {
	      this.isSelected = isSelected;
	   }
	 
	   public String toString() {
	      return EntailmentInspectorView.getRenderer().render(con);
	   }

	public OWLAxiom getAxiom() {
		return con;
	}
	}