/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_component;

/**
 *
 * @author samuel
 */
public class Zone {
	public int x, y, w, h;

	public int calc_surface()
	{
		return w*h;
	}

	@Override
	public String toString()
	{
		return "[x="+x+" y="+y+" w="+w+" h="+h+"]";
	}

	public boolean in(Zone z)
	{
		return in(z.x, z.y);
	}
	public boolean in(int nx, int ny)
	{
		if ((nx > x) && (nx < (x+w)))
		if ((ny > y) && (ny < (y+h)))
			return true;
		return false;
	}

	public boolean in(Bulle b)
	{
		return in(b.x, b.y);
	}
}
