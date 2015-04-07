package balayageK2.awt;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

public class PanelCommande extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton lectureSauvegarde = new JButton("restauration données");
	JButton créerSauvegarde = new JButton("sauvegarde données");
	JButton créerImage = new JButton("créer jpg");
	JButton printCycle = new JButton("print Cycle");
	JButton go = new JButton("go");
	JButton stop = new JButton("stop");
	
	public PanelCommande(JButton boutonMainHTML) {
		this.setLayout(new GridLayout(1,0));
		add(boutonMainHTML, 0);
		add(lectureSauvegarde, 0);
		add(créerSauvegarde, 0);
		add(créerImage, 0);
		add(printCycle, 0);
		add(go, 1);
	}

	boolean modeBoutonGo = true;
	
	public void enleverBoutonGo() {
		if (modeBoutonGo) {
			modeBoutonGo = false;
			remove(go);
			add(stop, 1);
			validate();
			repaint();
		}
	}
	
	public void ajouterBoutonGo() {
		if (!modeBoutonGo) {
			modeBoutonGo = true;
			remove(stop);
			add(go, 1);
			validate();
			repaint();
		}
	}
	
	public void addActionListener(ActionListener l) {
		go.addActionListener(l);
		stop.addActionListener(l);
		lectureSauvegarde.addActionListener(l);
		créerSauvegarde.addActionListener(l);
		créerImage.addActionListener(l);
		printCycle.addActionListener(l);
	}
}
