package org.whatif.tools.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.Border;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class OWLOntologyChangeList extends MList {
	private static final long serialVersionUID = 2024889684812090240L;

	private OWLOntologyManager manager;

    private OWLEditorKit editorKit;


    public OWLOntologyChangeList(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        this.manager = editorKit.getModelManager().getOWLOntologyManager();
        setCellRenderer(new AxiomListItemRenderer());
        setAxioms();
    }
    
    public Set<OWLOntologyChange> getSelectedAxioms() {
    	Set<OWLOntologyChange> axioms = new HashSet<OWLOntologyChange>();
    	for (int i = 0; i < getModel().getSize(); i++) {
            Object item = getModel().getElementAt(i);
            if(item instanceof OntologyChangeListItem) {
    			axioms.add(((OntologyChangeListItem)item).axiom);
    		}
        }
    	return axioms;
    }
    
    private void setAxioms() {
        List<Object> items = new ArrayList<Object>();
        setListData(items.toArray());
        setFixedCellHeight(24);
    }
    
    public void addAxioms(Set<OWLOntologyChange> axioms) {
        List<Object> items = new ArrayList<Object>();
       ListModel model = getModel();
       
       for(int i=0; i < model.getSize(); i++){
            Object o =  model.getElementAt(i);  
            items.add(o);
       }
       
       for (OWLOntologyChange ax : axioms) {
    	   items.add(ax.getClass().getSimpleName());
    	   items.add(new OntologyChangeListItem(ax, ax.getOntology()));
        }
       
        setListData(items.toArray());
        setFixedCellHeight(24);
    }
    
    


    protected void handleDelete() {
        super.handleDelete();
    }


    protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected,
                                         boolean cellHasFocus) {
        if (value instanceof OntologyChangeListItem) {
            return BorderFactory.createMatteBorder(1, 20, 1, 1, list.getBackground());
        }
        else {
            return super.createPaddingBorder(list, value, index, isSelected, cellHasFocus);
        }
    }


    private class AxiomListItemRenderer implements ListCellRenderer {

        private OWLCellRenderer ren;


        public AxiomListItemRenderer() {
            ren = new OWLCellRenderer(editorKit);
        }


        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value instanceof OntologyChangeListItem) {
            	OntologyChangeListItem item = ((OntologyChangeListItem) value);
                ren.setOntology(item.ontology);
                ren.setHighlightKeywords(true);
                ren.setWrap(false);
                return ren.getListCellRendererComponent(list, item.axiom.getAxiom(), index, isSelected, cellHasFocus);
            }
            else {
                return ren.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }
    }


    private class OntologyChangeListItem implements MListItem {

        private OWLOntologyChange axiom;

        private OWLOntology ontology;


        public OntologyChangeListItem(OWLOntologyChange axiom, OWLOntology ontology) {
            this.axiom = axiom;
            this.ontology = ontology;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return true;
        }

        public boolean handleDelete() {
            try {
            	if(axiom.isAddAxiom()) {
            		manager.applyChange(new RemoveAxiom(ontology, axiom.getAxiom()));
            	}
            	else if(axiom.isRemoveAxiom()) {
            		manager.applyChange(new AddAxiom(ontology, axiom.getAxiom()));
            	}
                return true;
            }
            catch (OWLOntologyChangeException e) {
                throw new OWLRuntimeException(e);
            }
        }


        public String getTooltip() {
            return "Change in " + ontology.getOntologyID();
        }
    }



	public void setAllAxiomsSelected() {
		int start = 0;
	    int end = getModel().getSize() - 1;
	    if (end >= 0) {
	      setSelectionInterval(start, end);
	    }			
	}

}
