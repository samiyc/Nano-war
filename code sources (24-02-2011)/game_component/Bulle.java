/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_component;

import java.util.ArrayList;


/**
 *
 * @author samuel
 */
public class Bulle {
	
	public static final int UNSELECT=0, SELECTED=1;
	public int status = 0;
	
	public static final int DONT_MOVE=0, MOVE=1;
	public int moov;

	public int clas, x, y, playeur, rayon, res, res_max;
	public int dest_x, dest_y;

	public Bulle(){}
	public Bulle(int c, int nx, int ny, int p, int r, int value)
	{
		clas    = c;
		x       = nx;
		y       = ny;
		playeur = p;
		rayon   = r;
		res		= value;
		res_max = res;
		moov    = DONT_MOVE;
	}
	public Bulle(int nx, int ny, int p, int r, int v, int cx, int cy)
	{
		x       = nx;
		y       = ny;
		playeur = p;
		rayon   = r;
		res		= v;
		res_max = res;
		moov    = MOVE;
		dest_x  = cx;
		dest_y  = cy;
	}
	public Bulle(String txt)
	{
		String [] tab = txt.split(":");
		x       = Integer.parseInt(tab[1]);
		y       = Integer.parseInt(tab[2]);
		playeur = Integer.parseInt(tab[3]);
		rayon   = Integer.parseInt(tab[4]);
		res     = Integer.parseInt(tab[5]);
		status  = Integer.parseInt(tab[6]);
		moov    = Integer.parseInt(tab[7]);
	}
	@Override
	public String toString()
	{
		return ":"+x+":"+y+":"+playeur+":"+rayon+":"+res+":"+status+":"+moov+":";
	}

//--------------------------------------------------------------------

	public Boolean in_fog(ArrayList<FogBulle> arg)
	{
		for (FogBulle fb : arg)
		{
			if (dist(fb.x, fb.y) < (rayon + FogBulle.RAYON))
			if (fb.alpha < 250)
			return false;
		}
		return true;
	}
	public Boolean in(Zone z)
	{
		return in(z.x, z.y);
	}
	public Boolean in(int nx, int ny)
	{
		if (dist(nx, ny) < rayon) return true;
		return false;
	}
	public Boolean contact(Bulle b)
	{
		if (dist(b) < (rayon+b.rayon)) return true;
		return false;
	}
	public int dist(Bulle c)
	{
		return dist(c.x, c.y);
	}
	public int dist(int nx, int ny)
	{
		return (int)Math.sqrt(Math.pow(nx - x, 2) + Math.pow(ny - y, 2));
	}

//--------------------------------------------------------------------

	/**
	 * Permet de faire evoluer les ressources de la bulle de façon periodique.
	 * Quand une moov rencotre une bulle les ressources ne sont pas transferer via cette methode.
	 * retourne le surplu if ressources loss
	 * @param r
	 * @return surplu
	 */
	public int add_res(int r)
	{
		if (res >= res_max)
		{
			return r;
		}
		else
		{
			res += r;
			if(res > res_max)
			{
				int diff = res - res_max;
				res = res_max;
				return diff;
			}
		}
		return 0;
	}
	
	public void moov_to_dest(int s, Zone z)
	{
		int xdiff, ydiff;
		for (int i=0; i<s; i++)
		{
			xdiff = dest_x - x;
			ydiff = dest_y - y;
			
			if(xdiff>0) x++;
			if(xdiff<0) x--;
			if(ydiff>0) y++;
			if(ydiff<0) y--;
		}
		if (x < z.x) x = z.x;
		if (y < z.y) y = z.y;
		if (x > z.w) x = z.w;
		if (y > z.h) y = z.h;
	}

//--------------------------------------------------------------------

	public int calc_surface()
	{
		return (int)(Math.PI*rayon*rayon);
	}

	double AngleOfView ( double ViewPt_X, double ViewPt_Y,
					 double Pt1_X, double Pt1_Y,
                     double Pt2_X, double Pt2_Y )
	{
	 double a1, b1, a2, b2, a, b, t, cosinus ;

	 a1 = Pt1_X - ViewPt_X ;
	 a2 = Pt1_Y - ViewPt_Y ;

	 b1 = Pt2_X - ViewPt_X ;
	 b2 = Pt2_Y - ViewPt_Y ;

	 a = Math.sqrt ( (a1*a1) + (a2*a2) );
	 b = Math.sqrt ( (b1*b1) + (b2*b2) );

	 if ( (a == 0.0) || (b == 0.0) )
		return (0.0) ;

	 cosinus = (a1*b1+a2*b2) / (a*b) ;

	 t = Math.acos ( cosinus );

	 //t = t * 180.0 / Math.PI ;
	 if(Pt2_Y < ViewPt_Y) t=-t;

	  return (t);
	}
}
