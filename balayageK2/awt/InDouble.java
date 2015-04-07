package balayageK2.awt;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class InDouble extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel;
	private JTextField jTextField;
	public double val;
	
	public InDouble(String texte, double init) {
		this(texte, init, 4);
		val = init;
	}
	
	public InDouble(String texte, double init, int taille) {
		super();
		jLabel = new JLabel(texte);
		jTextField = new JTextField(""+init, taille);
		initialize();
		val = init;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new FlowLayout());
		this.add(jLabel, null);
		this.add(jTextField, null);
		jTextField.addActionListener(this);
	}

	public double getVal() {
		return val;
	}
		
	public void checkVal() {
		double oldVal = val;
		try {
			val = Double.parseDouble(jTextField.getText());
		} catch (Exception e) {
			jTextField.setText("?"+jTextField.getText()+"?");
			val = oldVal;
		}
	}
		
	public void setVal(double val) {
		jTextField.setText(""+val);
		this.val = val;
	}

	public void changeLabel(String newLabel) {
		jLabel.setText(newLabel);
	}

	public void actionPerformed(ActionEvent e) {
		val = Double.parseDouble(jTextField.getText());
	}

}
