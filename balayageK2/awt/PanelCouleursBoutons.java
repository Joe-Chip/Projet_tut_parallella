package balayageK2.awt;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PanelCouleursBoutons extends JPanel implements ActionListener {
	JRadioButton boutonDefaut;
	JRadioButton boutonV;
	JRadioButton boutonH;
	JRadioButton boutonD;
	JRadioButton boutonG;
	PanelDessin2D panelDessin2D;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PanelCouleursBoutons(PanelDessin2D panel) {
		panelDessin2D = panel;
		panel.panelCouleursBoutons = this;
		boutonDefaut = new JRadioButton("*", true);
		boutonV = new JRadioButton("V", true);
		boutonH = new JRadioButton("H", true);
		boutonD = new JRadioButton("D", true);
		boutonG = new JRadioButton("G", true);
		setLayout(new GridLayout(1,4));
		add(boutonDefaut);
		add(boutonV);
		add(boutonH);
		add(boutonD);
		add(boutonG);
		boutonDefaut.addActionListener(this);
		boutonV.addActionListener(this);
		boutonH.addActionListener(this);
		boutonD.addActionListener(this);
		boutonG.addActionListener(this);
		validate();
		if (getParent()!=null) getParent().validate();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==boutonDefaut ||
				e.getSource()==boutonV ||
				e.getSource()==boutonH ||
				e.getSource()==boutonD ||
				e.getSource()==boutonG
				) {
			panelDessin2D.redessiner();
		}
	}
}


