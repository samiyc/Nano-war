/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_v2_component;

import game_component.Bulle;

/**
 *
 * @author samuel
 */
public class Bulle_v2 extends Bulle {

	public static final int PROD=0, BUILD=1, STOCK=2, RESEARCH=3;
	public int type = 0;

	public Bulle_v2(int t, int c, int nx, int ny, int r, int value)
	{
		type    = t;
		clas    = c;
		x       = nx;
		y       = ny;
		rayon   = r;
		res		= value;
		res_max = res;
	}
	
	public Bulle_v2(String txt)
	{
		String [] tab = txt.split(":");
		x       = Integer.parseInt(tab[1]);
		y       = Integer.parseInt(tab[2]);
		rayon   = Integer.parseInt(tab[3]);
		res     = Integer.parseInt(tab[4]);
		status  = Integer.parseInt(tab[5]);
		type    = Integer.parseInt(tab[6]);
	}

	@Override
	public String toString()
	{
		return ":"+x+":"+y+":"+rayon+":"+res+":"+status+":"+type+":";
	}

}
