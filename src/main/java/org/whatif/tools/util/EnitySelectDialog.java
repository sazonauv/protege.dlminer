package org.whatif.tools.util;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.semanticweb.owlapi.model.OWLEntity;
import org.whatif.tools.view.EntailmentInspectorView;

import java.beans.*; //property change stuff
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
 
/* 1.4 example used by DialogDemo.java. */
public class EnitySelectDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 98298723989898L;
	private OWLEntitySelectorPanel selectentity;   
    private final JComponent dd;
    private JButton bt_done = new JButton("Done");
 
    /** Creates the reusable dialog. 
     * @param entityselect */
    public EnitySelectDialog(Frame aFrame, JComponent parent, OWLEntitySelectorPanel entityselect) {
        super(aFrame, true);
        dd = parent;
        setTitle("Select Entities");
 
        selectentity = entityselect;//
        setLocationRelativeTo(parent);
        
        //Make this dialog display it.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(selectentity,BorderLayout.CENTER);
        getContentPane().add(bt_done,BorderLayout.SOUTH);
       
        //getContentPane().setPreferredSize(new Dimension(100, 200));
        //getContentPane().repaint();
        pack();
//Make this dialog display it.
        //setContentPane(optionPane);
        
        //Handle window closing correctly.
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        /*addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 *
            }
        }); */
        
        bt_done.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clearAndHide();
				if(dd instanceof EntailmentInspectorView) {
					((EntailmentInspectorView)dd).updateEntitySelectTA();
				}
			}
		});
 
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                selectentity.requestFocusInWindow();
            }
        });
 
      
     }
 
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }

	public Set<OWLEntity> getEntities() {
		return selectentity.getSelectedObjects();
	}
}