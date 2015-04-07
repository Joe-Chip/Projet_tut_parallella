package balayageK2.fonctions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import balayageK2.CalculAncienMyrberg;
import balayageK2.CalculParametriques;
import balayageK2.CalculCascade3D;

import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.PanelDessin3D;

public class AncienMyrberg extends  CalculAncienMyrberg {

	public AncienMyrberg(BufferedReader br) {
		this();
		try {
			readParam(br);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public AncienMyrberg() {
		initObjetsAWT();
		titre = new JLabel("Ancien Myrberg", JLabel.CENTER);
	}
	
	
	/*
	public double foo(double valM1, double valM2) {
		return valM1 * valM1 * valM1 + b * valM2 + a;
	}
*/

	public void calculM() {
		y = (mu)*(y)*(1 - y);
	}

	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
	}
	
}
