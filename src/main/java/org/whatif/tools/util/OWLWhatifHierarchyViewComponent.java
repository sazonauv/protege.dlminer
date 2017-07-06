package org.whatif.tools.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.consequence.WhatifConsequence;

import com.sun.org.apache.xpath.internal.axes.SubContextList;

public class OWLWhatifHierarchyViewComponent extends JPanel {
	private JTree tree;
	private JEditorPane htmlPane;
	private DefaultMutableTreeNode topnode;
	private OWLEntityHierarchyNode top;
	private DefaultTreeModel model;
	
	public OWLWhatifHierarchyViewComponent(OWLEntity top) {
		super(new GridLayout(1,0));
		this.top=new OWLEntityHierarchyNode(top);
		topnode = new DefaultMutableTreeNode(this.top);
		tree = new JTree(topnode);
		topnode = (DefaultMutableTreeNode)tree.getModel().getRoot();
		model = (DefaultTreeModel)tree.getModel();
		JScrollPane treeView = new JScrollPane(tree);
		tree.setCellEditor(new EntryEditor());
        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        add(treeView);
	}

	public void setAxioms(Set<WhatifConsequence> asserted_axioms) {
		//System.out.println("setAxioms():Hierarchy "+asserted_axioms.size());
		Map<OWLEntityHierarchyNode,DefaultMutableTreeNode> index = new HashMap<OWLEntityHierarchyNode,DefaultMutableTreeNode>();
		index.put(this.top, this.topnode);
		Set<OWLEntityHierarchyNode> hasParent = new HashSet<OWLEntityHierarchyNode>();
		//Map<DefaultMutableTreeNode,Set<DefaultMutableTreeNode>> subs = new HashMap<DefaultMutableTreeNode,Set<DefaultMutableTreeNode>>();
		topnode.removeAllChildren();
		for(WhatifConsequence con:asserted_axioms) {
			if(con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax= (WhatifInferenceConsequence)con;
			
			if(top.e instanceof OWLClass) {
				processOWLClass(index, ax.getOWLAxiom(), hasParent);
			}
			else if(top.e instanceof OWLObjectProperty) {
				processOWLObjectProperty(index, ax.getOWLAxiom(), hasParent);
			}
			else if(top.e instanceof OWLDataProperty) {
				processOWLDataProperty(index, ax.getOWLAxiom(), hasParent);
			}
			}
		}
		// In case the entity is not connected to top, add it underneath it
		for(OWLEntityHierarchyNode n:index.keySet()) {
			if(!hasParent.contains(n)&&!n.equals(top)) {
				//System.out.println("noParent: "+n);
				topnode.add(index.get(n));
			}
		}
		model.reload();
	}
	
	private void processOWLDataProperty(Map<OWLEntityHierarchyNode, DefaultMutableTreeNode> index, OWLAxiom ax, Set<OWLEntityHierarchyNode> hasParent) {
		if(ax.isOfType(AxiomType.SUB_DATA_PROPERTY)) {
			OWLSubDataPropertyOfAxiom sub_ax = (OWLSubDataPropertyOfAxiom)ax;
			OWLDataPropertyExpression subc = sub_ax.getSubProperty();
			OWLDataPropertyExpression superc = sub_ax.getSuperProperty();
			if(!subc.isAnonymous()&&!superc.isAnonymous()) {
				extractOWLEntityNodes(index, (OWLEntity)subc, (OWLEntity)superc, hasParent);
			}
		}
	}

	private void extractOWLEntityNodes(Map<OWLEntityHierarchyNode, DefaultMutableTreeNode> index,
			OWLEntity subc, OWLEntity superc, Set<OWLEntityHierarchyNode> hasParent) {
		OWLEntityHierarchyNode s1 = new OWLEntityHierarchyNode(subc);
		OWLEntityHierarchyNode s2 = new OWLEntityHierarchyNode(superc);
		if(!index.containsKey(s1)) {
			index.put(s1, new DefaultMutableTreeNode(s1));
		}
		
		if(!index.containsKey(s2)) {
			index.put(s2, new DefaultMutableTreeNode(s2));
		}
		hasParent.add(s1);
		index.get(s2).add(index.get(s1));
	}

	
	private void processOWLObjectProperty(Map<OWLEntityHierarchyNode, DefaultMutableTreeNode> index, OWLAxiom ax, Set<OWLEntityHierarchyNode> hasParent) {
		if(ax.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {
			OWLSubObjectPropertyOfAxiom sub_ax = (OWLSubObjectPropertyOfAxiom)ax;
			OWLObjectPropertyExpression subc = sub_ax.getSubProperty();
			OWLObjectPropertyExpression superc = sub_ax.getSuperProperty();
			if(!subc.isAnonymous()&&!superc.isAnonymous()) {
				extractOWLEntityNodes(index, (OWLEntity)subc, (OWLEntity)superc, hasParent);
			}
		}
	}

	private void processOWLClass(Map<OWLEntityHierarchyNode, DefaultMutableTreeNode> index, OWLAxiom ax, Set<OWLEntityHierarchyNode> hasParent) {
		if(ax.isOfType(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom)ax;
			OWLClassExpression subc = sub_ax.getSubClass();
			OWLClassExpression superc = sub_ax.getSuperClass();
			if(!subc.isAnonymous()&&!superc.isAnonymous()) {
				extractOWLEntityNodes(index, (OWLEntity)subc, (OWLEntity)superc, hasParent);
			}
		}
	}
	class EntryEditor extends DefaultCellEditor {
	    private JTextField box;
	    public EntryEditor() {
	        super(new JTextField());
	        box = ((JTextField) editorComponent);
	    }
	    @Override
	    public Component getTreeCellEditorComponent(final JTree tree, final Object value,
	            final boolean isSelected, final boolean expanded, final boolean leaf, final int row) {
	    	OWLEntity entry = (OWLEntity) ((DefaultMutableTreeNode) value).getUserObject();
	        box.setText(entry.getIRI().getFragment());
	        return box;
	    }
	}
	
	
	
	
}
