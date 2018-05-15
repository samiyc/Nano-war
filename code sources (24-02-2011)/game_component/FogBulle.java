/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_component;

/**
 *
 * @author samuel
 */
public class FogBulle {

	public static int RAYON = 10;
	public int x, y;
	public int alpha;

	public FogBulle(int nx, int ny)
	{
		x = nx;
		y = ny;
		alpha = 0;
	}

	public void fondre(int v)
	{
		fondre(v, 255);
	}

	public void fondre(int v, int m)
	{
		alpha += v;
		if (alpha < 0)   alpha = 0;
		if (alpha > m)   alpha = m;
	}
}
