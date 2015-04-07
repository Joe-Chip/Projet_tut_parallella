package balayageK2.awt;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class PanelTexte extends JScrollPane {
	private static final long serialVersionUID = 1L;
	String texte="";
	JTextPane paneText;
	
	public PanelTexte() {
		super(new JTextPane());
		paneText = (JTextPane) ((Container)getComponent(0)).getComponent(0);
	}
	
	public void raz() {
		texte="";
		paneText.setText(texte);
	}
	
	public String getTexte() {
		return texte;
	}
	
	public void print(String txt) {
		texte=texte+txt;
		paneText.setText(texte);
	}
	
	public void println(String txt) {
		texte=texte+txt;
		println();
	}
	
	public void println() {
		texte=texte+System.getProperty("line.separator");
		paneText.setText(texte);
	}

	public void setFont(Font font) {
		if (paneText!=null) paneText.setFont(font);
	}
}
