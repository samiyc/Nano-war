/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game;

import game_v2_component.BoutonInt;
import main.*;
import game_component.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author samuel
 */
public class NanoGame {

	public static final int NEUTRE=0, CLI=1, SERV=2, CLI_ON_SERV=3;
	public static final int DEF_CMP_REBOUR = 80;
	public static final int DEBUT=0, FIN=1;

	public ArrayList<FogBulle>  fog       = new ArrayList<FogBulle>();
	public ArrayList<Bulle>     map       = new ArrayList<Bulle>();
	public String               event     = new String();
	public Zone                 zone      = new Zone();
	public Zone                 mov_zone  = new Zone();

	public UserStat             cli_stt   = new UserStat();
	public UserStat             serv_stt  = new UserStat();

	public Image trois, deux, un, go, loose, win;

	public int playeur;
	public int width;
	public int height;
	public int fog_range;
	public int speed;
	public int pop;
	public int compte_a_rebour = 0;
	public int type_de_decompte;

	//----------------------------------------------------------------

	public NanoGame(){}
	public NanoGame(int p, int w, int h, int f, int s, int u)
	{
		playeur = p;
		width = w;
		height = h;
		fog_range = 165-f*15;
		speed = s;
		pop = u;

		zone.x = 40;
		zone.y = 100;
		zone.w = width  - 2*zone.x - 5;
		zone.h = height - 2*zone.y - 10;

		mov_zone.x = 10;
		mov_zone.y = 55;
		mov_zone.w = width-15;
		mov_zone.h = height-60;

		trois = Toolkit.getDefaultToolkit().getImage("image/3.gif");
		deux = Toolkit.getDefaultToolkit().getImage("image/2.gif");
		un = Toolkit.getDefaultToolkit().getImage("image/1.gif");
		go = Toolkit.getDefaultToolkit().getImage("image/go.gif");
		loose = Toolkit.getDefaultToolkit().getImage("image/lose.gif");
		win = Toolkit.getDefaultToolkit().getImage("image/win.gif");

		make_fog();
	}

//--------------------------------------------------------------------

	public void start()
	{
		init_map();
		cli_stt  = new UserStat();
		serv_stt  = new UserStat();
	}

	public void init_map()
	{
		int x, y, r;
		int nx, ny, t;
		int var;
		Boolean n;

		//initialisation du compte a rebour
		compte_a_rebour = DEF_CMP_REBOUR;
		type_de_decompte = DEBUT;

		//reset des statistiques
		cli_stt.reset();
		serv_stt.reset();

		//effacement de la carte
		map.clear();

		//Placement des bulles sur la carte
		Bulle b, b2;

		b = new Bulle(); b.rayon = 30;
		t = zone.calc_surface()/b.calc_surface();
		t = t/(10-pop);

		for(int i=0; i<t; i++)
		{
			//on choisie aléatoirement un point
			if (i==0) //point de depart des joueurs
			{
				x = zone.x+(int)(Math.random()*(double)(zone.w/2-50));
			}
			else
			x = zone.x+(int)(Math.random()*(double)(zone.w));
			y = zone.y+(int)(Math.random()*(double) zone.h);
			
			r = ((int)(Math.random()*9.1)+1);
			if ( r <6)            r = 1;
			if ((r >5) && (r <9)) r = 2;
			if ( r==9)            r = 3;
			if ( r==10)           r = 4;

			//Joueur ou bien neutre
			if (i==0) 
			{
				var = playeur; r = 1;
			}
			else      var = NEUTRE;

			//definition des dimension de la bulle
			int c = r;
			int s = r*r*10;
			r = r*8+5;

			b = new Bulle(c, x, y, var, r, s);

			//on verifie que la nouvelle bulle ne soit pas en contact avec une bulle déjà existante
			n = true;
			for(int j=0; ((j<map.size()) && n); j++)
			{
				Bulle d = map.get(j);
				if (d.contact(b)) n = false;
			}

			//ajout de la bulle a la map
			if (n)
			{
				//placement du joueur adversse
				if (i==0)
				{
					if (playeur == CLI)  var=SERV;
					else var=CLI;
				}
				else var = NEUTRE;

				//gestion de la symetrie
				nx = (zone.w - (b.x-zone.x))+zone.x;
				ny = (zone.h - (b.y-zone.y))+zone.y;
				b2 = new Bulle(c, nx, ny, var, r, s);

				//on fait attention a ce que les bulle ne soit pas en contact
				if (!b.contact(b2))
				{
					map.add(b);
					map.add(b2);
				}
			}
		}
	}

	public boolean isLunching()
	{
		if (compte_a_rebour > 0) return true;
		return false;
	}

	//***********************************************************************************************
	//                       DEBUT PAINT
	//***********************************************************************************************
	public void paint(Graphics2D g)
	{
		int cr, cv, cb;

		//--------------------------------------------------------------------

		//AFFICHAGE DES BULLES
		for(Bulle b : map)
		{
			if ((b.playeur != playeur) &&  (b.in_fog(fog)))  {/*NOTHING*/}
			else
			{

				if (b.playeur == NEUTRE)        {cr=80; cv=80; cb=80;}
				else if (b.playeur == playeur)  {cr=0; cv=0; cb=150;}
				else                            {cr=200; cv=0; cb=0;}

				int nr = b.rayon;
				if ((b.playeur == playeur) && (b.status == Bulle.SELECTED))
				{
					nr = b.rayon + 2;
					g.setColor(new Color(0, 200, 0));
					g.fillOval(b.x-nr, b.y-nr, 2*nr, 2*nr);
					nr = b.rayon;
				}

				//LAYER PRINTER
				int nbf = 3;
				if (b.moov == Bulle.MOVE) nbf = 1;
				for(int i=0; i<nbf; i++)
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

			}//fin test
		
		}//fin affichage bulle

		//--------------------------------------------------------------------

		//AFFICHAGE DU COMPTE A REBOUR
		if (compte_a_rebour > 0)
		{
			int x = zone.x + zone.w/2;
			int y = zone.y + zone.h/2;

			compte_a_rebour--;
			if (type_de_decompte == DEBUT)//COMPTE A REBOUR 3, 2, 1, GO
			{
				if      (compte_a_rebour <= 10) g.drawImage(go, x-(go.getWidth(null)/2), y-(go.getHeight(null)/2), null);
				else if (compte_a_rebour <= 20) ;
				else if (compte_a_rebour <= 30) g.drawImage(un, x-(un.getWidth(null)/2), y-(un.getHeight(null)/2), null);
				else if (compte_a_rebour <= 40) ;
				else if (compte_a_rebour <= 50) g.drawImage(deux, x-(deux.getWidth(null)/2), y-(deux.getHeight(null)/2), null);
				else if (compte_a_rebour <= 60) ;
				else if (compte_a_rebour <= 70) g.drawImage(trois, x-(trois.getWidth(null)/2), y-(trois.getHeight(null)/2), null);
			}
			else //COMPTE A REBOUR : You win !!! / You lose
			{
				set_fog_value(0);

				//restart
				if ((playeur == SERV) && compte_a_rebour == 0) init_map();

				if (compte_a_rebour > Main.FPS*20)
				{
					if (playeur == CLI)
					{
						if(cli_stt.nb_bulle == 0)  g.drawImage(loose, x-(loose.getWidth(null)/2), y-(loose.getHeight(null)/2), null);
						else                       g.drawImage(win, x-(win.getWidth(null)/2), y-(win.getHeight(null)/2), null);
					}
					else
					{
						if(serv_stt.nb_bulle == 0) g.drawImage(loose, x-(loose.getWidth(null)/2), y-(loose.getHeight(null)/2), null);
						else                       g.drawImage(win, x-(win.getWidth(null)/2), y-(win.getHeight(null)/2), null);
					}
				}
				else
				{
					paint_score(g);
				}
			}

		}//fin compte a rebou

		//--------------------------------------------------------------------

		//AFFICHAGE DU BROUILLARD
		if (compte_a_rebour == 0)
		{
			update_fog(fog_range);
			paint_fog(g);
		}

		//--------------------------------------------------------------------

		//AFFICHAGE DU FOOTER
		g.setColor(new Color(180, 110, 90));
		g.fillRect(0, height-50, width, 50);

		//--------------------------------------------------------------------

		//AFFICHAGES DES STATISTIQUES
		g.setColor(Color.black);

		int toto = cli_stt.nb_win+serv_stt.nb_win;
		if (playeur == CLI)
		{
			g.drawString("Vous : "+cli_stt.nb_win+"   Adverssaire : "+serv_stt.nb_win+"   Total : "+toto, 100, 25);
		}
		else
		{
			g.drawString("Vous : "+serv_stt.nb_win+"   Adverssaire : "+cli_stt.nb_win+"   Total : "+toto, 100, 25);
		}
		g.setColor(Color.black);
		g.drawString("NB : "+serv_stt.nb_res, 50, height - 35);
		g.drawString("NB : "+cli_stt.nb_res, width-100, height - 35);

		//TEST
//		if (test) paint_score(g);
//		if (one_bulle_selected(playeur, Bulle.DONT_MOVE)) b_evo.paint(g);

	}//fin paint function

	//***********************************************************************************************
	//                       FIN PAINT
	//***********************************************************************************************

	public void update(int frame_count)
	{
		if (compte_a_rebour == 0)
		{
			update_stat();
			move_bulle();
			if (frame_count%(25-speed) == 0) update_bulle();
		}
	}

	public void move_bulle()
	{
		for(int i=0; i<map.size(); i++)
		{Bulle b = map.get(i);

			if(b.moov == Bulle.MOVE)
			{
				b.moov_to_dest((speed+2)/3, mov_zone);
				
				boolean n = true;

				for(int j=0; (j<map.size()) && n; j++)
				{Bulle e = map.get(j);
					
					if(!b.equals(e) && e.contact(b))
					{
						n = false;
						if(e.playeur != b.playeur)
						{
							if (e.playeur == NEUTRE)
							{
								int dep;
								if (e.res > b.res) dep = b.res;
								else dep = e.res;
								if (b.playeur == CLI ) cli_stt.som_spend_neutre += dep;
								if (b.playeur == SERV) serv_stt.som_spend_neutre += dep;
							}

							e.res -= b.res;
							map.remove(b);
							if (e.res == 0)
							{
								e.playeur = NEUTRE;
								if (e.moov == Bulle.MOVE) map.remove(e);
							}
							else if(e.res < 0)
							{
								e.res = -e.res;
								
								if (e.playeur != NEUTRE)
								{
									if (b.playeur == CLI ) cli_stt.som_bul_stil ++;
									if (b.playeur == SERV) serv_stt.som_bul_stil ++;
								}
								e.playeur = b.playeur;
								e.status = Bulle.UNSELECT;
								if (e.moov == Bulle.MOVE)
								{
									e.x = b.x;
									e.y = b.y;
									e.dest_x = b.dest_x;
									e.dest_y = b.dest_y;
								}
							}
						}
						else
						{
							e.res += b.res;
							map.remove(b);
						}
					}
				}
			}
		}
	}
	

	public void update_bulle()
	{
		for(Bulle b : map)
		if ((b.playeur != NEUTRE) && (b.moov == Bulle.DONT_MOVE))
		{
			int i = 1;
			if (b.clas == 1) i=1;
			if (b.clas == 2) i=2;
			if (b.clas == 3) i=3;
			if (b.clas == 4) i=4;
			int rl = b.add_res(i);
			if (rl > 0)
			{
				if (b.playeur == CLI ) cli_stt.som_lose_res += rl;
				if (b.playeur == SERV) serv_stt.som_lose_res += rl;
			}
			else
			{
				if (b.playeur == CLI ) cli_stt.som_res_collect += i;
				if (b.playeur == SERV) serv_stt.som_res_collect += i;
			}
		}
	}

	public void update_stat()
	{
		cli_stt.nb_bulle = 0;
		serv_stt.nb_bulle = 0;
		cli_stt.nb_res = 0;
		serv_stt.nb_res = 0;
		cli_stt.som_frame ++;
		serv_stt.som_frame ++;

		for(Bulle b : map)
		{
			if (b.playeur == SERV)
			{
				if (b.moov == Bulle.DONT_MOVE) serv_stt.nb_bulle ++;
				serv_stt.nb_res += b.res;
				serv_stt.som_stock += b.res;
			}
			if (b.playeur == CLI)
			{
				if (b.moov == Bulle.DONT_MOVE) cli_stt.nb_bulle ++;
				cli_stt.nb_res += b.res;
				cli_stt.som_stock += b.res;
			}
		}

		if (((cli_stt.nb_bulle == 0) || (serv_stt.nb_bulle == 0)) && (type_de_decompte != FIN))
		{
			compte_a_rebour = Main.FPS*23;
			type_de_decompte = FIN;
			if (cli_stt.nb_bulle != 0)  cli_stt.nb_win++;
			if (serv_stt.nb_bulle != 0) serv_stt.nb_win++;
		}
	}

//--------------------------------------------------------------------

	public String get_source()
	{
		String s = ""+compte_a_rebour+";"+type_de_decompte+";"+cli_stt.toString()+";"+serv_stt.toString()+";";
		for(Bulle b : map)
		{
			s += b.toString() + ";";
		}
		return s;
	}

	public void set_source(String txt)
	{
		map.clear();
		String [] tab = txt.split(";");
		
		//maj des variables
		compte_a_rebour = Integer.parseInt(tab[0]);
		type_de_decompte = Integer.parseInt(tab[1]);
		cli_stt  = new UserStat(tab[2]);
		serv_stt = new UserStat(tab[3]);

		//maj des bulles
		for(int i=4; i< tab.length;i++)
		{
			map.add(new Bulle(tab[i]));
		}
	}

	public String get_event()
	{
		String tmp = new String();
		tmp = event;
		event = "";
		return tmp;
	}

	public void exec_event(String arg)
	{
		if(playeur == SERV)
		{
			String [] tab, line;
			tab = arg.split(";");
			for(int i=0; i<tab.length; i++)
			{
				line = tab[i].split(":");
				if("sab".equals(line[0]))
				{
					Boolean s = Boolean.parseBoolean(line[1]);
					Boolean c = Boolean.parseBoolean(line[2]);
					select_all_bulle(CLI_ON_SERV, s, c);
				}
				if("sel".equals(line[0]))
				{
					Zone z = new Zone();
					z.x = Integer.parseInt(line[1]);
					z.y = Integer.parseInt(line[2]);
					z.w = Integer.parseInt(line[3]);
					z.h = Integer.parseInt(line[4]);
					Boolean s = Boolean.parseBoolean(line[5]);
					Boolean c = Boolean.parseBoolean(line[6]);
					select_bulle(CLI_ON_SERV, z, s, c);
				}
				if("snd".equals(line[0]))
				{
					int x = Integer.parseInt(line[1]);
					int y = Integer.parseInt(line[2]);
					send_bull(CLI_ON_SERV, x, y);
				}
			}
		}
		else
		{
			echo("ExecEvent : Le client ne peut executer les evenements distant");
		}
	}

//--------------------------------------------------------------------

	public void click(int joueur, int sx, int sy, Zone z, boolean s, boolean c, int tc)
	{
		if      (tc==Jeu.DROITE)	send_bull(joueur, sx, sy);
		else if (tc==Jeu.CENTRE)	select_all_bulle(joueur, s, c);
		else if (tc==Jeu.GAUCHE)	select_bulle(joueur, z, s, c);
	}

	public void select_all_bulle(int joueur, boolean s, boolean c)
	{
		if(joueur == CLI)
		{
			event += "sab:"+s+":"+c+":"+";";
		}
		else
		{
			if (joueur == CLI_ON_SERV) joueur = CLI; //SIMULATION DU CLIENT SUR LE SERVEUR
			for(Bulle b : map)
			{
				if (b.playeur == joueur) b.status = Bulle.UNSELECT;
			}
			for(Bulle b : map)
			{
				if (b.playeur == joueur)
				{
					if (s)//Si l'utilisateur appuy sur shift
					{
						if (b.moov == Bulle.MOVE) b.status = Bulle.SELECTED;
					}
					else if (c)//Si l'utilisateur appuy sur CTRL
					{
						if (b.moov == Bulle.DONT_MOVE) b.status = Bulle.SELECTED;
					}
					else
					{
						b.status = Bulle.SELECTED;
					}
				}
			}
		}
	}

	public void select_bulle(int joueur, Zone z, boolean s, boolean c)
	{
		if(joueur == CLI)
		{
			event += "sel:"+z.x+":"+z.y+":"+z.w+":"+z.h+":"+s+":"+c+":"+";";
		}
		else
		{
			if (joueur == CLI_ON_SERV) joueur = CLI; //SIMULATION DU CLIENT SUR LE SERVEUR

			//clique en un meme point
			if((z.w<5) && (z.h<5))
			{
				//Selection d'une bulle
				for(Bulle b : map) if  (b.playeur == joueur) b.status = Bulle.UNSELECT;
				for(Bulle b : map) if ((b.playeur == joueur) && b.in(z)) b.status = Bulle.SELECTED;

//				if (b_evo.in(z))
//				{
//					the_little_one.moov = Bulle.MOVE;
//					test = true;
//				}
//				else test = false;
			}
			else
			{
				//Selection d'un groupe de bulle
				for(Bulle b : map)if (b.playeur == joueur) b.status = Bulle.UNSELECT; //déséléction
				for(Bulle b : map) //selection
				{
					if ((b.playeur == joueur) && z.in(b))
					{
						if (s)//Si l'utilisateur appuy sur shift
						{
							if (b.moov == Bulle.MOVE) b.status = Bulle.SELECTED;
						}
						else if (c)//Si l'utilisateur appuy sur CTRL
						{
							if (b.moov == Bulle.DONT_MOVE) b.status = Bulle.SELECTED;
						}
						else b.status = Bulle.SELECTED;
					}
				}
			}
		}
	}

	public void send_bull(int joueur, int x, int y)
	{
		if(joueur == CLI)
		{
			event += "snd:"+x+":"+y+":"+";";
		}
		else
		{
			if (joueur == CLI_ON_SERV) joueur = CLI;
			if (joueur == CLI) cli_stt.som_event ++;
			else serv_stt.som_event ++;
			for(int i=0; i<map.size(); i++)
			{
				Bulle b = map.get(i);
				if ((b.playeur == joueur) && (b.status == Bulle.SELECTED))
				{
					int a = b.res/2;
					if ((b.moov == Bulle.DONT_MOVE) && (a > b.clas))
					{
						b.res -= a;
						Bulle sb = new Bulle(b.x, b.y, joueur, 5, a, x, y);
						map.add(sb);
						sb.moov_to_dest(b.rayon+8, mov_zone);
						if(b.playeur == CLI)       cli_stt.som_moov ++;
						else if(b.playeur == SERV) serv_stt.som_moov ++;
					}
					else
					{
						b.dest_x = x;
						b.dest_y = y;
					}
				}
			}
		}
	}
//--------------------------------------------------------------------

	public void make_fog()
	{
		int x, y;
		for(x=0; x<(width+5); x+=(FogBulle.RAYON * 2))
		for(y=40+FogBulle.RAYON; y<=(height-40); y+=(FogBulle.RAYON * 2))
		{
			fog.add(new FogBulle(x, y));
		}
	}
	public void set_fog_value(int a)
	{
		for(FogBulle fb : fog) fb.alpha = a;
	}
	public void fog_fondre(int a)
	{
		for(FogBulle fb : fog) fb.fondre(a);
	}
	public void update_fog(int start)
	{
		int stop = start + 35;
		int lr, dist, v;
		for(FogBulle fb : fog)
		{
			lr = 999;
			for(Bulle b : map)
			{
				if (b.playeur == playeur)
				{
					dist = b.dist(fb.x, fb.y);
					if (dist < lr) lr = dist;
				}
			}
			if (lr >= stop) fb.fondre(2);
			else if (lr <= start) fb.alpha = 0;
			else
			{
				v = lr - start;
				v = (v*255)/(stop-start);
				fb.fondre(2, v);
			}
		}
	}
	public void paint_fog(Graphics2D g)
	{
		for(FogBulle fb : fog)
		{
			g.setColor(new Color(0, 0, 0, fb.alpha));
			g.fillRect(fb.x-FogBulle.RAYON, fb.y-FogBulle.RAYON, FogBulle.RAYON*2, FogBulle.RAYON*2);
		}
	}

//--------------------------------------------------------------------

	void echo(String txt)
	{
		System.out.println("NanoGame."+txt);
	}

	private void paint_score(Graphics2D g)
	{
		int x, y, w=375, h=160;
		int lfm = 10, upm = 15, inl = 15;

		x = width/2 - w/2;
		y = height/2 - h/2;
		g.setColor(new Color(100, 100, 100));
		g.fillRect(x, y, w, h);
		g.setColor(Color.black);

		//ecriture du texte
		x += lfm;
		y += upm;
		g.drawString("-------------------------------------  Résultats  ------------------------------------", x, y); y+=inl;
		if (playeur == SERV) x += (w/2);
		g.drawString("Ressources collectées : "+cli_stt.som_res_collect, x, y); y+=inl;
		g.drawString("Dépense sur neutre : "+cli_stt.som_spend_neutre, x, y); y+=inl;
		g.drawString("Ressources perdues : "+cli_stt.som_lose_res, x, y); y+=inl;
		g.drawString("-------------------------", x, y); y+=inl;
		g.drawString("Bulles vollées : "+cli_stt.som_bul_stil, x, y); y+=inl;
	try{g.drawString("Nombre de moov : "+cli_stt.som_moov, x, y); y+=inl;} catch(Exception e){}
	try{g.drawString("APM : "+cli_stt.get_apm(), x, y); y+=inl;} catch(Exception e){}
		g.drawString("-------------------------", x, y); y+=inl;
	try{g.drawString("Score globale : "+cli_stt.get_toto(), x, y); y+=inl;} catch(Exception e){}
		

		y = height/2 - h/2 + inl;
		y += upm;
		if (playeur == CLI)  x += (w/2);
		if (playeur == SERV) x -= (w/2);
		g.drawString("Ressources collectées : "+serv_stt.som_res_collect, x, y); y+=inl;
		g.drawString("Dépense sur neutre : "+serv_stt.som_spend_neutre, x, y); y+=inl;
		g.drawString("Ressources perdues : "+serv_stt.som_lose_res, x, y); y+=inl;
		g.drawString("-------------------------", x, y); y+=inl;
		g.drawString("Bulles vollées : "+serv_stt.som_bul_stil, x, y); y+=inl;
	try{g.drawString("Nombre de moov : "+serv_stt.som_moov, x, y); y+=inl;} catch(Exception e){}
	try{g.drawString("APM : "+serv_stt.get_apm(), x, y); y+=inl;} catch(Exception e){}
		g.drawString("-------------------------", x, y); y+=inl;
	try{g.drawString("Score globale : "+serv_stt.get_toto(), x, y); y+=inl;} catch(Exception e){}
	}
}
