package balayageK2.awt;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class PanelHTML extends JEditorPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dimension dim = new Dimension(800,800);
	
	public PanelHTML(String nomFichier) {
		try {
			setPage(ClassLoader.getSystemResource(nomFichier));
		} catch (IOException e) {
			setText("Erreur de chargement du fichier : "+nomFichier);
		}
		setEditable(false);
	}
	
	public Dimension getPreferredSize() {
		return dim;
	}
}
