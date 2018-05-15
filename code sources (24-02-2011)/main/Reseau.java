/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author samuel
 */
public class Reseau {

	int type;
	public static final int CLIENT=0, SERVEUR=1;
	int mode;
	public static final int DECO=0, CONNECT=1;

	Integer       port = 2320;
	ServerSocket  serveur;

	Socket         socket;
	BufferedReader bufferIn;
	PrintWriter    bufferOut;
	
//--------------------------------------------------------------------

	public Reseau(int t)
	{
		type = t;
		mode = DECO;
	}
//--------------------------------------------------------------------

	public void lancer() throws Exception
	{
		if (type == SERVEUR)
		{
			try
			{
				serveur = new ServerSocket(port);
				serveur.setSoTimeout(100);
			}
			catch(IOException e)
			{
				mode = DECO;
				throw e;
			}
		}
		else
		{
			mode = DECO;
			throw new Exception("Le CLIENT n'as pas accé a la fonction lancer()");
		}
	}
//--------------------------------------------------------------------

	public void wait_client() throws Exception
	{
		if (type == SERVEUR)
		{
			try
			{
				socket = serveur.accept();
				bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				bufferIn  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch(IOException e)
			{
				mode = DECO;
				throw e;
			}
			mode = CONNECT;
		}
		else
		{
			mode = DECO;
			throw new Exception("Le CLIENT n'as pas accé a la fonction wait_client()");
		}
	}
//--------------------------------------------------------------------

	public void connect(String ip) throws Exception
	{
		if (type == CLIENT)
		{
			try
			{
				socket = new Socket(ip, port);
				bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				bufferIn  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch(IOException e)
			{
				mode = DECO;
				throw e;
			}
			mode = CONNECT;
		}
		else
		{
			mode = DECO;
			throw new Exception("Le SERVEUR n'as pas accé a la fonction connect()");
		}
	}
//--------------------------------------------------------------------

	public void send(String txt) throws Exception
	{
		if (mode == CONNECT)
		{
			bufferOut.println(txt);
		}
		else
		{
			throw new Exception("impossible d'envoyer des données en mode déconnécté");
		}
	}
//--------------------------------------------------------------------

	public String receive() throws Exception
	{
		if (mode == CONNECT)
		{
			return bufferIn.readLine();
		}
		else
		{
			throw new Exception("impossible d'envoyer des données en mode déconnécté");
		}
	}
//--------------------------------------------------------------------

	public void deconnection()
	{
		if (mode == CONNECT)
		{
			mode = DECO;
			try
			{
				if(type == SERVEUR)
				{
					serveur.close();
				}
				socket.close();
			}
			catch (IOException ex)
			{
				echo("deco : erreur de deconnexion");
			}
		}
	}
//--------------------------------------------------------------------

	void echo(String txt)
	{
		System.out.println("Main."+txt);
	}
}
