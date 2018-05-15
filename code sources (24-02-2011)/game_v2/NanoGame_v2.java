/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game_v2;

import main.*;
import game.*;
import game_component.*;
import game_v2_component.*;
import java.awt.Color;

/**
 *
 * @author samuel
 */
public class NanoGame_v2 extends NanoGame{

	boolean test = false;
	Bulle the_little_one;
	BoutonInt b_evo = new BoutonInt();

	//--------------------------------------------------------------

	public NanoGame_v2()
	{
		super();
	}

	public NanoGame_v2(int p, int w, int h, int f, int s, int u)
	{
		super(p, w, h, f, s, u);

		b_evo.texte = "Evolution";
		b_evo.x = width/2 - b_evo.w/2;
		b_evo.y = height-45;
		b_evo.c_back = new Color(250,250,250,128);
	}

	public boolean one_bulle_selected(int joueur, int type)
	{
		int retour = 0;
		for(int i=0; (i<map.size()) && (retour <= 1); i++)
		{
			Bulle b = map.get(i);
			if ((b.playeur == joueur) && (b.status == Bulle.SELECTED))
			{
				if (b.moov != type) return false;
				the_little_one = b;
				retour ++;
			}
		}
		if (retour == 1) return true;
		return false;
	}
}

