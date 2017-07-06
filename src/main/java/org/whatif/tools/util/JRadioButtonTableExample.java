package org.whatif.tools.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class JRadioButtonTableExample extends JFrame {

	  public JRadioButtonTableExample() {
	    super("JRadioButtonTable Example");
	    UIDefaults ui = UIManager.getLookAndFeel().getDefaults();
	    UIManager.put("RadioButton.focus", ui.getColor("control"));

	   
	  }

	  public static void main(String[] args) {
	    JRadioButtonTableExample frame = new JRadioButtonTableExample();
	    frame.addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        System.exit(0);
	      }
	    });
	  }
	}