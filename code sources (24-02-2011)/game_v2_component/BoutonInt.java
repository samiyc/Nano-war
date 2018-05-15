/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_v2_component;

import game_component.*;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author samuel
 */
public class BoutonInt extends Zone {

	public String texte;
	public Color c_back = new Color(255,255,255);
	public Color c_border = new Color(0,0,0);
	public Color c_font = new Color(0,0,0);

	public BoutonInt()
	{
		super();
		w = 60;
		h = 15;
	}

	public void paint(Graphics2D g)
	{
		g.setColor(c_back);
		g.fillRect(x, y, w, h);
		g.setColor(c_border);
		g.drawRect(x, y, w, h);
		g.setColor(c_font);
		g.drawString(texte, x+3, y+h-2);
	}
}
