/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game;

import game_component.*;
import main.*;

/**
 *
 * @author samuel
 */
public class PerfectBot extends NanoGame {

	int x=-99, y=-99, ld, lr;
	int old_nb_bulle=-1;
	int patr;
	int limit_rush_res=100;

	//temp
	int dist, res;

	public PerfectBot(int w, int h, int f, int s, int u)
	{
		super(SERV, w, h, f, s, u);//le joueur est le serveur / l'ordinateur le client
	}

	@Override
	public void update(int frame_count)
	{
		if (compte_a_rebour == 0)
		{
			if ((frame_count % 5) == 0) play_bot(); //~1s
			update_stat();
			move_bulle();
			if (frame_count%(25-speed) == 0) update_bulle();
		}
		else //reset bot
		{
			x=-99; y=-99;
			old_nb_bulle=-1;
			limit_rush_res=100;
		}
	}

	//----------------------------------------------------------------

	void play_bot()
	{
		int nb_bulle;
		//Zone t = new Zone();
		boolean dest_lock = false;
		
		//analyse
		patr = 0;
		nb_bulle = 0;
		for(Bulle b : map) if(b.playeur == CLI)
		{
			nb_bulle++;
			patr += b.clas;
		}
		
		//verif qu'il n'y a pas de menaces adversse
		if(old_nb_bulle <= nb_bulle)
		{
			//dest lock
			for(int i=0; (i<map.size()) && !dest_lock; i++)
			{Bulle b = map.get(i);
				if (b.in(x, y) && (b.playeur != CLI)) dest_lock = true;
			}
		}
		old_nb_bulle = nb_bulle;

		//recherche d'une nouvelle cible
		if (!dest_lock)
		{
			ld = 999; lr = 999;
			
			//send moov
			for(int i=0; i<map.size(); i++)
			{Bulle b = map.get(i);
				
				//micro
				if((b.playeur == CLI) && (b.moov == Bulle.DONT_MOVE) && (b.res > 3))
				{
					//dest
					for(int j=0; j<map.size(); j++)
					{Bulle c = map.get(j);
						
						if ((c.playeur != CLI) && (c.moov == Bulle.DONT_MOVE))
						{
							if (c.clas == 1)
							{
								analyse(b, c);
							}
							else if ((c.clas == 2) && (patr > 4))
							{
								analyse(b, c);
							}
							else if ((c.clas == 3) && (patr > 20))
							{
								analyse(b, c);
							}
							else if ((c.clas == 4) && (patr > 30))
							{
								analyse(b, c);
							}
						}
						
					}//end dest
					
				}//end micro
				
			}//end send moov
			
			//si patr>7 and range <100 -> augmentation du range
			if (ld == 999)
			{
				limit_rush_res *= 2;
			}
			else
			{
				limit_rush_res = 100;
			}
			
		}//end dest_lock
		//*****************************************
		
		//attaque groupé
		select_all_bulle(CLI_ON_SERV, false, true);
		send_bull(CLI_ON_SERV, x, y);
		
	}//fin bot play

	//----------------------------------------------------------------

	void analyse(Bulle b, Bulle c)
	{
		if (patr < 10)
		{
			dist = b.dist(c);
			//if (c.playeur == SERV) dist = ((dist*2)/3);
			if(dist < ld)
			{
				ld = dist; x=c.x; y=c.y;
			}
		}
		else
		{
			dist = b.dist(c);
			if (c.playeur == SERV) dist = ((dist*2)/3);
			if (dist < limit_rush_res)
			{
				ld = dist;
				res = c.res;
				if (c.playeur == SERV) dist = ((dist*2)/3);
				if(res < lr)
				{
					lr = res; x=c.x; y=c.y;
				}
			}
		}
	}//fin analyse

	//----------------------------------------------------------------

}//fin classe
