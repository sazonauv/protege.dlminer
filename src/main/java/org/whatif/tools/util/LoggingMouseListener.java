package org.whatif.tools.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;

import org.protege.editor.owl.model.OWLWorkspace;

public class LoggingMouseListener implements MouseListener {

	String name;
	Component cb;
	OWLWorkspace ws;

	public LoggingMouseListener(Component cb, String name, OWLWorkspace ws) {
		this.name = name;
		this.cb = cb;
		this.ws = ws;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String state = "NA";
		if (cb instanceof AbstractButton) {
			state = ((AbstractButton) cb).isSelected() + "";
		}
		EventLogging.saveEvent(System.currentTimeMillis(), ws.getSelectedTab().getId(), name, state,
				"wii");
	}
}
