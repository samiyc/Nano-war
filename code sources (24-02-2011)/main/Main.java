/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;
import javax.swing.*;

public class Main extends JFrame {

	//Lancement du programme
    public static void main(String[] args) {new Main();}

//--------------------------------------------------------------------

	//Les variables
	public static final int FPS=20;
	Jeu    jeu    = new Jeu(this);

//--------------------------------------------------------------------

	//le constructeur
    public Main()
	{
		//configuration de la fenetre
		this.setTitle("Nano War v0.2");
		this.setSize(300, 250);//75
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(jeu);
		jouer();
	}

//--------------------------------------------------------------------
	
	public void jouer()
	{
		while (true)
		{
			try {
				Thread.sleep(1000/FPS);//20fps -> 50ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	
//--------------------------------------------------------------------

	void echo(String txt)
	{
		System.out.println("Main."+txt);
	}
}
