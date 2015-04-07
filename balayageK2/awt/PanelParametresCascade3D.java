package balayageK2.awt;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelParametresCascade3D extends PanelParametres {

	InDouble valB = new InDouble("B", -0.261, 8);
	//String[] lstChoixPlanStr = { "A", "B" };
	//JComboBox lstChoixPlan = new JComboBox(lstChoixPlanStr);
	
	public PanelParametresCascade3D(String[] lstChoixInit) {
		lstChoix = new JComboBox(lstChoixInit);
		initialize();
	}

	protected void initialize() {
		this.setLayout(new BorderLayout());
		add(titre, BorderLayout.NORTH);
		add(contenu, BorderLayout.CENTER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setHgap(2);
		gridLayout.setVgap(2);
		gridLayout.setColumns(4);
		contenu.setLayout(gridLayout);
		contenu.setSize(800, 40);
		minX.changeLabel("min X");
		maxX.changeLabel("max X");
		pasX.changeLabel("pas X");
		minY.changeLabel("min Y");
		maxY.changeLabel("max Y");
		contenu.add(minX);
		contenu.add(maxX);
		contenu.add(pasX);
		//this.add(epsilon);
		contenu.add(nbrIter);
		contenu.add(minY);
		contenu.add(maxY);
		//this.add(pasY);
		JPanel jp = new JPanel();
		jp.add(lstChoix);
		//jp.add(lstChoixPlan);
		contenu.add(jp);
		contenu.add(valB);
		//this.add(afficherCourbes);
	}

	public double getValB() {return valB.getVal();}

	public void setValB(double val) { valB.setVal(val);}
	
    public void saveData (BufferedWriter bw) throws IOException {
    	bw.write(""+minX.getVal()); bw.newLine();
    	bw.write(""+maxX.getVal()); bw.newLine();
    	bw.write(""+pasX.getVal()); bw.newLine();
    	bw.write(""+minY.getVal()); bw.newLine();
    	bw.write(""+maxY.getVal()); bw.newLine();
    	bw.write(""+nbrIter.getVal()); bw.newLine();
    	bw.write(""+valB.getVal()); bw.newLine();
    	bw.write(""+lstChoix.getSelectedIndex()); bw.newLine();
    }
    
    public void readData (BufferedReader br) throws IOException {
    	minX.setVal(Double.parseDouble(br.readLine()));
    	maxX.setVal(Double.parseDouble(br.readLine()));
    	pasX.setVal(Double.parseDouble(br.readLine()));
    	minY.setVal(Double.parseDouble(br.readLine()));
    	maxY.setVal(Double.parseDouble(br.readLine()));
    	nbrIter.setVal(Integer.parseInt(br.readLine()));
    	valB.setVal(Double.parseDouble(br.readLine()));
    	lstChoix.setSelectedIndex(Integer.parseInt(br.readLine()));
    }

}
