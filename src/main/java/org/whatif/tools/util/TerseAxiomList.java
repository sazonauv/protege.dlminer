package org.whatif.tools.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.whatif.tools.view.EntailmentInspectorView;


public class TerseAxiomList extends JList<Object> {

	DefaultListModel<Object> listModel = new DefaultListModel<Object>();
	
	List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
	
	public TerseAxiomList() {
		super();
		setModel(listModel);
		setCellRenderer(new MyCellRenderer()); 
		addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            int index = list.locationToIndex(evt.getPoint());
		            if(listModel.getElementAt(index) instanceof JLabel) {
		            	showAll();
		            }
		        }
		    }
		});
	}
	
	public void addAxiom(OWLAxiom axiom) {
		axioms.add(axiom);
		if(axioms.size()<5) {
			listModel.addElement(axiom);
		}
		else if(axioms.size()==5) {
			JLabel label = new JLabel("More..");
			listModel.addElement(label);
		}		
	}
	
	protected void showAll() {
		listModel.clear();
		for(OWLAxiom ax:axioms) {
			listModel.addElement(ax);
		}
		repaint();
	}

	public void removeAxiom(OWLAxiom axiom) {
		listModel.removeElement(axiom);
	}
	
	public void clearAxioms() {
		axioms.clear();
		listModel.clear();
	}
	
	class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {

		  public MyCellRenderer() {
		    setOpaque(true);
		  }

		  public Component getListCellRendererComponent(JList list, Object value, int index,
		      boolean isSelected, boolean cellHasFocus) {
			  
			if(value instanceof OWLAxiom) { 
		    setText(render((OWLObject)value));
			}
			else {
				setText(((JLabel)value).getText());
			}
		    
		    if (isSelected) {
		      setBackground(list.getSelectionBackground());
		      setForeground(list.getSelectionForeground());
		    } else {
		      setBackground(list.getBackground());
		      setForeground(list.getForeground());
		    }
		    return this;
		  }
		}

	public void addAxioms(Set<OWLAxiom> axioms) {
		for(OWLAxiom ax:axioms) {
			addAxiom(ax);
		}
	}
	
	private String render(OWLObject o) {
		return EntailmentInspectorView.getRenderer().render(o);
	}

	public void reset() {
		clearAxioms();
	}
	
}
