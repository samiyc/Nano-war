/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import game_component.Zone;
import game.*;
import main.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketTimeoutException;


public class Jeu extends JPanel implements MouseListener, ActionListener {

	public static final int GAUCHE=0, CENTRE=1, DROITE=2;
	
	Main papa;
	NanoGame ng = new NanoGame();
	Reseau con; String info_serveur;
	Image img, fnd, trois, deux, un;
	Zone selection = new Zone();

	int osx, osy, sx, sy, etat_souris = 0, type_click;
	int frame_count;

	public static final int MENU=0, VAI=1, HELP=2, INFO=3, SERVEUR=4, CLIENT=5, SERV_WAITING=6, SERV_IN_GAME=7, CLI_IN_GAME=8;
	int game_status;
	int width=800, height=600, fog_range, speed=5, dif=5;
	

//--------------------------------------------------------------------

	//menu
	JButton b_vai = new JButton("Jouer contre l'ordinateur");
	JButton b_mks = new JButton("Créer un partie en réseau");
	JButton b_con = new JButton("Rejoindre une partie en réseau");
	JButton b_aid = new JButton("Apprendre à jouer");
	JButton b_inf = new JButton("Informations");

	//opotion vs AI
	JButton b_go_vai = new JButton("Lancer");

	//création de la partie
	JLabel l_wid = new JLabel("Width :");
	JLabel l_heg = new JLabel("Height :");
	JLabel l_fog = new JLabel("Brouillard :");
	JLabel l_spd = new JLabel("Speed :");
	JLabel l_dif = new JLabel("Population :");
	JSpinner sp_wid = new JSpinner();
	JSpinner sp_heg = new JSpinner();
	JSlider  sl_fog = new JSlider();
	JSlider  sl_spd = new JSlider();
	JSlider  sl_dif = new JSlider();
	JButton b_retour = new JButton("Retour");
	JButton b_lancer = new JButton("Lancer");

	//connection du client
	JLabel     l_ip      = new JLabel("IP du serveur :");
	JTextField tf_ip     = new JTextField("127.0.0.1");
	JButton    b_connect = new JButton("Connection");

	//waiting client
	JButton b_close_com = new JButton("Retour");

//--------------------------------------------------------------------

	public Jeu (Main p)
	{
		super();
		this.setDoubleBuffered(true);
		img = Toolkit.getDefaultToolkit().getImage("image/sablier.jpg");
		fnd = Toolkit.getDefaultToolkit().getImage("image/fond.jpg");
		frame_count=0;  papa = p;
		addMouseListener(this);
		game_status = MENU;
		//DEBUT DES INITIALISATIONS

		//affichage du menu
		setLayout(new GridLayout(5,1));
		this.add(b_vai);
		this.add(b_mks);
		this.add(b_con);
		this.add(b_aid);
		this.add(b_inf);
		b_vai.setName("B_VAI");
		b_mks.setName("B_MKS");
		b_con.setName("B_CON");
		b_aid.setName("B_AID");
		b_inf.setName("B_INF");
		b_vai.addActionListener(this);
		b_mks.addActionListener(this);
		b_con.addActionListener(this);
		b_aid.addActionListener(this);
		b_inf.addActionListener(this);

		//configuration des bouton pour le lancement d'une partie contre l'ordinateur
		b_go_vai.setName("B_GO_VAI");
		b_go_vai.addActionListener(this);

		//configuration des spinner et slider pour le lancement de la partie
		sp_wid.setValue(800);
		sp_heg.setValue(600);
		sl_fog.setMinimum(1);
		sl_fog.setMaximum(9);
		sl_fog.setValue(5);
		sl_fog.setMinorTickSpacing(1);
		sl_fog.setMajorTickSpacing(4);
		sl_fog.setSnapToTicks(true);
		sl_fog.setPaintTicks(true);
		sl_spd.setMinimum(1);
		sl_spd.setMaximum(9);
		sl_spd.setValue(5);
		sl_spd.setMinorTickSpacing(1);
		sl_spd.setMajorTickSpacing(4);
		sl_spd.setSnapToTicks(true);
		sl_spd.setPaintTicks(true);
		sl_dif.setMinimum(1);
		sl_dif.setMaximum(9);
		sl_dif.setValue(5);
		sl_dif.setMinorTickSpacing(1);
		sl_dif.setMajorTickSpacing(4);
		sl_dif.setSnapToTicks(true);
		sl_dif.setPaintTicks(true);
		b_retour.setName("B_RETOUR");
		b_lancer.setName("B_LANCER");
		b_retour.addActionListener(this);
		b_lancer.addActionListener(this);

		//connexion du client au serveur
		b_connect.setName("B_CONNECT");
		b_connect.addActionListener(this);

		//gestion du bouton de retour de la page WAITING_connection
		b_close_com.setName("B_CLOSE_COM");
		b_close_com.addActionListener(this);

		//FIN DES INITIALISATIONS
	}
	
//--------------------------------------------------------------------

	@Override
    public void paintComponent(Graphics go)
	{
		super.paintComponent(go);
		Graphics2D g = (Graphics2D) go;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(230, 230, 230));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		frame_count++;
		//DEBUT DE L'AFFICHAGE

		//affichage de la bubule
		if ((game_status == SERVEUR) || (game_status == CLIENT))
		{
			g.setColor(new Color(200, 200, 200));
			g.drawLine(0, 0, 100, 100);
			int a = (int)(Math.sin((double)frame_count/5.0)*10.0);
			g.fillOval(sx, sy, 50+a, 50+a);
		}
		if (game_status == SERV_WAITING)                              //////   SERVEUR  WAITING  CLIENT   //////
		{
			//affichage de la barre de progression
			g.drawImage(img, 145, 0, null);
			int time = (frame_count%100+1);
			g.setColor(new Color(130, 200, 130));
			g.fillRect(0, 40, time, 22);
			//attente du client
			try
			{
				con.wait_client();
				String s = con.receive();
				if ("Hello i am Nano WAR v0.2".equals(s))
				{
					send_infos_to_client();
					game_status = SERV_IN_GAME;
					switch_in_game();
					create_nano_game(NanoGame.SERV);
					ng.start();
					con.send(ng.get_source());
					frame_count=0;
				}
			}
			//le client ne c'est pas encore connecté
			catch (SocketTimeoutException r){}
			//erreur de connection
			catch (Exception e)
			{
				echo("PainCompo : "+e);
				JOptionPane.showMessageDialog(null, "Erreur provoqué par la fonction wait_client()", "Impossible de lancer le serveur", JOptionPane.WARNING_MESSAGE);
				con.deconnection();
				back_to_the_menu();
			}
		}
		//--------------------------------------------------------------------

		if (game_status == HELP) ng.update(frame_count);
		if (game_status == VAI)  ng.update(frame_count);
		if ((game_status == SERV_IN_GAME) || (game_status == CLI_IN_GAME) || (game_status == HELP) || (game_status == VAI))
		{
			//fond
			g.drawImage(fnd, 0, 0, null);
			g.drawImage(fnd, 1000, 0, null);

			//entete
			g.setColor(new Color(180, 110, 90));
			g.fillRect(0, 0, width, 40);

			//barre d'avancement
			int time = (frame_count%100+1);
			g.setColor(new Color(190, 120, 100));
			g.fillRect(width-120, 10, time, 15);

			//affichage des bulles
			ng.paint(g);

			//cadre de selection
			if (etat_souris > 0)
			{
				etat_souris--;
				if ((selection.w >5) && (selection.h >5))
				g.drawRect(selection.x, selection.y, selection.w, selection.h);
			}
		}

		//************************************************************
		//      WARNING BRAIN RUPTURE
		//************************************************************

		if (game_status == SERV_IN_GAME)
		{
			ng.update(frame_count);

			//gestion de la connexion
			try {
				con.send(ng.get_source());
			} catch (Exception ex) {
				game_status = MENU;
				echo("PainComp : "+ex);
				con.deconnection();
				back_to_the_menu();
			}
			try {
				ng.exec_event(con.receive());
			} catch (Exception ex) {
				game_status = MENU;
				echo("PainComp : "+ex);
				con.deconnection();
				back_to_the_menu();
			}
		}
		if (game_status == CLI_IN_GAME)
		{
			//transfert des données entre le serveur et le client
			try {
				ng.set_source(con.receive());
			} catch (Exception ex) {
				game_status = MENU;
				echo("PainComp : "+ex);
				con.deconnection();
				back_to_the_menu();
			}
			try {
				con.send(ng.get_event());
			} catch (Exception ex) {
				game_status = MENU;
				echo("PainComp : "+ex);
				con.deconnection();
				back_to_the_menu();
			}
		}

		//************************************************************
		//      END WARNING BRAIN RUPTURE
		//************************************************************

		//affichage des informations
		if (game_status == INFO)
		{
			g.setColor(new Color(110, 110, 110));
			g.fillRect(0, 0, width, 40);

			int ty = 70;
			g.setColor(Color.black);
			g.drawString("Editeur  : Samuel YANEZ-CARBONELL", 20, ty+=20);
			g.drawString("Site        : Samuel-art.luminésens.com", 20, ty+=20);
			g.drawString("Source  : www.SourceForge.com/nanowar", 20, ty+=20);
			g.drawString("Licence : Créative Commons", 20, ty+=20);
			g.drawString("                 Paternité 2.0 France (CC BY 2.0)", 20, ty+=20);
		}

		//FIN DE L'AFFICHAGE
    }

//--------------------------------------------------------------------

	public void mousePressed(MouseEvent e){
		etat_souris = 0;
		osx = e.getX();
		osy = e.getY();
	}
	public void mouseReleased(MouseEvent e){
		if (!ng.isLunching())
		{
			etat_souris = 20;
			sx = e.getX();
			sy = e.getY();

			//Droite / gauche
			int a = e.getButton();
			if (a==MouseEvent.BUTTON1) type_click = GAUCHE;
			if (a==MouseEvent.BUTTON2) type_click = CENTRE;
			if (a==MouseEvent.BUTTON3) type_click = DROITE;


			if ((game_status == SERV_IN_GAME) || (game_status == CLI_IN_GAME) || (game_status == HELP) || (game_status == VAI))
			{
				int ox=0, oy=0, dx=0, dy=0;
				if (osx>sx) {ox=sx; dx=osx;} else {ox=osx; dx=sx;}
				if (osy>sy) {oy=sy; dy=osy;} else {oy=osy; dy=sy;}

				selection.x = ox;
				selection.y = oy;
				selection.w = dx-ox;
				selection.h = dy-oy;

				if (game_status == SERV_IN_GAME) a = NanoGame.SERV;
				if (game_status == CLI_IN_GAME)  a = NanoGame.CLI;
				if (game_status == HELP)         a = NanoGame.SERV;
				if (game_status == VAI)          a = NanoGame.SERV;

				ng.click(a, sx, sy, selection, e.isShiftDown(), e.isControlDown(), type_click);
			}
		}
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e) {}

//--------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		JButton x = (JButton)e.getSource();
		String t = x.getName();
		//echo("ActPer : "+t);

		if("B_VAI".equals(t))
		{
			papa.resize(300, 250);
			game_status = SERVEUR;
			this.removeAll();
			setLayout(new GridLayout(6,2));
			add(l_wid);    add(sp_wid);
			add(l_heg);    add(sp_heg);
			add(l_fog);    add(sl_fog);
			add(l_spd);    add(sl_spd);
			add(l_dif);    add(sl_dif);
			add(b_retour); add(b_go_vai);
			this.revalidate();
		}

		if("B_MKS".equals(t))
		{
			papa.resize(300, 250);
			game_status = SERVEUR;
			this.removeAll();
			setLayout(new GridLayout(6,2));
			add(l_wid);    add(sp_wid);
			add(l_heg);    add(sp_heg);
			add(l_fog);    add(sl_fog);
			add(l_spd);    add(sl_spd);
			add(l_dif);    add(sl_dif);
			add(b_retour); add(b_lancer);
			this.revalidate();
		}

		if("B_CON".equals(t))
		{
			papa.resize(300, 200);
			game_status = CLIENT;
			this.removeAll();
			setLayout(new GridLayout(5,2));
			add(new JLabel(""));add(new JLabel(""));
			add(new JLabel(""));add(new JLabel(""));
			add(l_ip);          add(tf_ip);
			add(new JLabel(""));add(new JLabel(""));
			add(b_retour);      add(b_connect);
			this.revalidate();
		}

		if("B_AID".equals(t))
		{
			width  = 480;
			height = 320;
			game_status = HELP;
			papa.resize(width, height);
			papa.setLocationRelativeTo(null);
			removeAll();
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(b_retour);
			revalidate();
			ng = new FirstStepGame(width, height);
			ng.start();
		}

		if("B_INF".equals(t))
		{
			width  = 480;
			height = 320;
			game_status = INFO;
			removeAll();
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(b_retour);
			revalidate();
		}

		if("B_RETOUR".equals(t))
		{
			back_to_the_menu();
		}

		// menu
		//************************************************************************************************
		//************************************************************************************************
		// sous menu

		if("B_GO_VAI".equals(t))
		{
			width      = (Integer)sp_wid.getValue();
			height     = (Integer)sp_heg.getValue();
			fog_range  = (Integer)sl_fog.getValue();
			speed      = (Integer)sl_spd.getValue();
			dif        = (Integer)sl_dif.getValue();
			if (width < 480) width = 480;
			if (height < 320) height = 320;
			if (width > 1600) width = 1600;
			if (height > 1000) height = 1000;
			game_status = VAI;
			papa.resize(width, height);
			papa.setLocationRelativeTo(null);
			removeAll();
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(b_retour);
			revalidate();
			ng = new PerfectBot(width, height, sl_fog.getValue(), sl_spd.getValue(), sl_dif.getValue());
			ng.start();
		}

		if("B_CONNECT".equals(t))
		{
			con = new Reseau(Reseau.CLIENT);
			try
			{
				con.connect(tf_ip.getText());
				con.send("Hello i am Nano WAR v0.2");
				info_serveur = con.receive();
				parse_info_serveur();
				game_status = CLI_IN_GAME;
				switch_in_game();
				create_nano_game(NanoGame.CLI);
				ng.set_source(con.receive());
				frame_count=0;
			}
			catch (Exception ex)
			{
				echo("ActPer : "+e);
				JOptionPane.showMessageDialog(null, "Erreur provoqué par la fonction connect()", "Impossible de se connecter au serveur", JOptionPane.WARNING_MESSAGE);
				con = null;
			}
		}

		if("B_LANCER".equals(t))
		{
			papa.resize(300, 200);
			this.removeAll();
			setLayout(new GridLayout(5,3));
			this.add(new JLabel(""));                  this.add(new JLabel(""));this.add(new JLabel(""));
			this.add(new JLabel("   Waiting client")); this.add(new JLabel(""));this.add(new JLabel(""));
			this.add(new JLabel(""));                  this.add(new JLabel(""));this.add(new JLabel(""));
			this.add(new JLabel(""));                  this.add(new JLabel(""));this.add(new JLabel(""));
			this.add(b_close_com);                     this.add(new JLabel(""));this.add(new JLabel(""));
			this.revalidate();
			con = new Reseau(Reseau.SERVEUR);
			try {
				con.lancer();
				game_status = SERV_WAITING;
				frame_count=0;//positionne la barre vert a 0
			}
			catch (Exception ex) {
				echo("ActPer : "+ex);
				JOptionPane.showMessageDialog(null, "Erreur provoqué par la fonction lancer()", "Impossible de lancer le serveur", JOptionPane.WARNING_MESSAGE);
				con.deconnection();
				back_to_the_menu();
			}
		}

		if("B_CLOSE_COM".equals(t))
		{
			con.deconnection();
			back_to_the_menu();
		}
	
	}//fin action performed

//--------------------------------------------------------------------

	void back_to_the_menu()
	{
		game_status = MENU;
		papa.resize(300, 250);
		this.removeAll();
		setLayout(new GridLayout(5,1));
		this.add(b_vai);
		this.add(b_mks);
		this.add(b_con);
		this.add(b_aid);
		this.add(b_inf);
		this.revalidate();
		sx = 0; sy = 0;//repositionnement de la balle dans l'angle
	}

	void switch_in_game()
	{
		papa.resize(width, height);
		papa.setLocationRelativeTo(null);
		removeAll();
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(b_close_com);
		revalidate();
	}

	void create_nano_game(int arg)
	{
		ng = new NanoGame(arg, width, height, fog_range, speed, dif);
	}

//--------------------------------------------------------------------

	void send_infos_to_client()
	{
		width      = (Integer)sp_wid.getValue();
		height     = (Integer)sp_heg.getValue();
		fog_range  = (Integer)sl_fog.getValue();
		speed      = (Integer)sl_spd.getValue();
		dif        = (Integer)sl_dif.getValue();
		if (width < 480) width = 480;
		if (height < 320) height = 320;
		if (width > 1600) width = 1600;
		if (height > 1000) height = 1000;
		try
		{
			con.send(""+width+";"+height+";"+fog_range+";"+speed+";"+dif+";");
		} catch (Exception ex)
		{
			echo("send_inf : "+ex);
			JOptionPane.showMessageDialog(null, "Erreur provoqué par la fonction send()", "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
			con = null;
			back_to_the_menu();
		}
	}

	void parse_info_serveur()
	{
		String [] tab;
		tab = info_serveur.split(";");
		width     = Integer.parseInt(tab[0]);
		height    = Integer.parseInt(tab[1]);
		fog_range = Integer.parseInt(tab[2]);
		speed     = Integer.parseInt(tab[3]);
		dif       = Integer.parseInt(tab[4]);
	}

//--------------------------------------------------------------------

	void echo(String txt)
	{
		System.out.println("Jeu."+txt);
	}
}