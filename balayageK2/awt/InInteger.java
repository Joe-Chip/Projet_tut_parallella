package balayageK2.awt;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class InInteger extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel;
	private JTextField jTextField;
	public int val;
	
	public InInteger(String texte, int init) {
		this(texte, init, 4);
		val = init;
	}

	public InInteger(String texte, int init, int taille) {
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

	public int getVal() {
		return val;
	}
	
	public void checkVal() {
		int oldVal = val;
		try {
			val = Integer.parseInt(jTextField.getText());
		} catch (Exception e) {
			jTextField.setText("?"+jTextField.getText()+"?");
			val = oldVal;
		}
	}
		
	public void setVal(int val) {
		jTextField.setText(""+val);
	}

	public void changeLabel(String newLabel) {
		jLabel.setText(newLabel);
	}

	public void actionPerformed(ActionEvent e) {
		val = Integer.parseInt(jTextField.getText());
	}

	
}
