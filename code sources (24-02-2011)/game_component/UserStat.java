/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_component;

import main.Main;

/**
 *
 * @author samuel
 */
public class UserStat {
	public int nb_win;           //nombre de parties gagnées
	public int nb_bulle;         //nombre de bulle actuellement
	public int nb_res;           //nombre de ressource actuellement

	public int som_bul_stil;     //bulle volé a l'adversaire
	public int som_res_collect;  //ressources collectés
	public int som_spend_neutre; //ressources depenssés sur les bulles neutre
	public int som_lose_res;     //ressources perdu

	public int som_frame;        //Permet de calculer les moyennes en fin de partie
	public int som_event;        //pour le calcul de l'APM
	public int som_moov;         //pour le calcul de la moyenne du nombre de moov unit
	public int som_stock;        //pour le calcul de la moyenne des stock

	public UserStat()
	{
		nb_win           = 0;
		nb_bulle         = 0;
		nb_res           = 0;
		som_bul_stil     = 0;
		som_res_collect  = 0;
		som_spend_neutre = 0;
		som_lose_res     = 0;
		som_frame        = 0;
		som_event        = 0;
		som_moov         = 0;
		som_stock        = 0;
	}

	//----------------------------------------------------------------

	public UserStat(String txt)
	{
		String [] tab = txt.split(":");
		nb_win           = Integer.parseInt(tab[1]);
		nb_bulle         = Integer.parseInt(tab[2]);
		nb_res           = Integer.parseInt(tab[3]);
		som_bul_stil     = Integer.parseInt(tab[4]);
		som_res_collect  = Integer.parseInt(tab[5]);
		som_spend_neutre = Integer.parseInt(tab[6]);
		som_lose_res     = Integer.parseInt(tab[7]);
		som_frame        = Integer.parseInt(tab[8]);
		som_event        = Integer.parseInt(tab[9]);
		som_moov         = Integer.parseInt(tab[10]);
		som_stock        = Integer.parseInt(tab[11]);
	}
	@Override
	public String toString()
	{
		return ":"+nb_win+":"+nb_bulle+":"+nb_res+":"+som_bul_stil+":"+som_res_collect+":"+som_spend_neutre+":"+som_lose_res+":"+som_frame+":"+som_event+":"+som_moov+":"+som_stock+":";
	}

	//----------------------------------------------------------------

	public void reset()
	{
		nb_bulle         = 0;
		nb_res           = 0;
		som_bul_stil     = 0;
		som_res_collect  = 0;
		som_spend_neutre = 0;
		som_lose_res     = 0;
		som_frame        = 0;
		som_event        = 0;
		som_moov         = 0;
		som_stock        = 0;
	}

	public int get_apm()
	{
		return ((som_event * Main.FPS * 60)/som_frame);
	}

	public int get_toto()
	{
		int score = som_res_collect-som_spend_neutre/2-som_lose_res*2 + som_bul_stil*2+som_moov/20+(get_apm()/20);
		if (score < 0) return 0;
		return (score);
	}
}
