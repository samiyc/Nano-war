/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game;
import game_component.*;

import java.awt.*;

/**
 *
 * @author samuel
 */
public class FirstStepGame extends NanoGame {

	int level;
	boolean fog_ok;

	public FirstStepGame(){}
	public FirstStepGame(int w, int h)
	{
		playeur = SERV;
		width = w;
		height = h;
		speed = 5;
		level = 1;

		mov_zone.x = 10;
		mov_zone.y = 55;
		mov_zone.w = width-15;
		mov_zone.h = height-60;

		make_fog();
	}
	
//--------------------------------------------------------------------

	@Override
	public void init_map()
	{
		map.clear();
		if (level == 1)
		{
			fog_ok = false;
			Bulle b1 = new Bulle(2, width/3, height/2, SERV, 30, 20);
			Bulle b2 = new Bulle(1, width*2/3, height/2, CLI, 15, 10);
			map.add(b1);
			map.add(b2);
		}
		if (level == 2)
		{
			fog_ok = false;
			Bulle b1 = new Bulle(3, width/3-9 , height/2+30, NEUTRE, 10, 10);
			Bulle b2 = new Bulle(2, width/3-34, height/2, NEUTRE, 10, 10);
			Bulle b3 = new Bulle(1, width/3   , height/2-20, SERV, 15, 15);
			Bulle b4 = new Bulle(1, width*2/3 , height/2, CLI, 20, 20);
			map.add(b1);
			map.add(b2);
			map.add(b3);
			map.add(b4);
		}
		if (level == 3)
		{
			fog_ok = true;
			Bulle b1 = new Bulle(2, width/5, height/3, SERV, 20, 20);
			Bulle b2 = new Bulle(2, width*4/5, height*2/3, CLI, 10, 10);
			Bulle b3 = new Bulle(1, width/3-38, height/2, NEUTRE, 20, 200);
			Bulle b4 = new Bulle(1, width/3+50, height/2-20, NEUTRE, 30, 200);
			Bulle b5 = new Bulle(2, width*2/3, height/3, NEUTRE, 20, 200);
			Bulle b6 = new Bulle(2, width*4/5-50, height*2/3, NEUTRE, 15, 200);
			map.add(b1);
			map.add(b2);
			map.add(b3);
			map.add(b4);
			map.add(b5);
			map.add(b6);
		}
	}

	@Override
	public boolean isLunching()
	{
		return false;
	}

//*****************************************************************************
//*****************************************************************************

	@Override
	public void paint(Graphics2D g)
	{
		int cr, cv, cb;

		//--------------------------------------------------------------------

		//AFFICHAGE DES BULLES
		for(Bulle b : map)
		{

			if (b.playeur == NEUTRE)        {cr=80; cv=80; cb=80;}
			else if (b.playeur == playeur)  {cr=0; cv=0; cb=200;}
			else                            {cr=200; cv=0; cb=0;}

			int nr = b.rayon;
			if (b.playeur == playeur)
			{
				nr = b.rayon + 2;
				g.setColor(new Color(0, 200, 0));
				if(b.status == Bulle.SELECTED) g.fillOval(b.x-nr, b.y-nr, 2*nr, 2*nr);
				nr = b.rayon;
			}

			//LAYER PRINTER
			for(int i=0; /*nr>0*/i<3; i++)
			{
				g.setColor(new Color(cr, cv, cb));
				g.fillOval(b.x-nr, b.y-nr, 2*nr, 2*nr);
				cr+=8; cv+=8; cb+=8; nr-=3;
				if(cr>250)cr=250;
				if(cv>250)cv=250;
				if(cb>250)cb=250;
			}

			//affichages des ressources
			g.setColor(Color.black);
			if (b.moov == Bulle.MOVE) g.drawString(""+b.res, b.x-4, b.y-6);
			else g.drawString(""+b.res, b.x-7, b.y+5);

		}//fin affichage bulle

		//--------------------------------------------------------------------

		//SWITCH LEVEL
		if (compte_a_rebour > 0)
		{
			compte_a_rebour--;
			if (compte_a_rebour < 30) fog_fondre(-10);
			else if (compte_a_rebour == 30)
			{
				level++;
				init_map();
			}
			else fog_fondre(10);

			paint_fog(g);
		}

		//--------------------------------------------------------------------

		//AFFICHAGE DU BROUILLARD
		if (fog_ok)
		{
			update_fog(80);
			paint_fog(g);
		}

		//--------------------------------------------------------------------

		//AFFICHAGE DU FOOTER
		g.setColor(new Color(180, 110, 90));
		g.fillRect(0, height-50, width, 50);

		//--------------------------------------------------------------------
		
		//AFFICHAGES DES STATISTIQUES
		g.setColor(Color.black);

		if (level < 4) g.drawString("Etape : "+level+" / 3", 100, 25);
		else g.drawString("FIN DU TUTORIEL", width/2-50, height/2);

		String info = "";
		if (level==1)
		{
			info = "Selectionnez l'unité bleu, puis faite un clique droit sur l'unité rouge.";
		}
		if (level==2)
		{
			info = "Attaquer les unités neutre permet d'obtenir plus de ressources.";
		}
		if (level==3)
		{
			info = "Ici, l'objectif c'est d'atteindre l'enemi sans toucher les unités neutre.";
		}
		if (compte_a_rebour == 0) g.drawString("Informations : "+info, 15, height - 35);

	}//fin paint function

//*****************************************************************************
//*****************************************************************************

	@Override
	public void update_stat()
	{
		if (compte_a_rebour == 0)
		{
			cli_stt.nb_bulle = 0;
			serv_stt.nb_bulle = 0;
			for(Bulle b : map)
			{
				if (b.playeur == SERV) serv_stt.nb_bulle += b.res;
				if (b.playeur == CLI)  cli_stt.nb_bulle += b.res;
			}

			if (cli_stt.nb_bulle == 0)
			{
				fog_ok = false;
				compte_a_rebour = 60;
			}
		}
	}

//--------------------------------------------------------------------

	@Override
	void echo(String txt)
	{
		System.out.println("FirstStepGame."+txt);
	}
}