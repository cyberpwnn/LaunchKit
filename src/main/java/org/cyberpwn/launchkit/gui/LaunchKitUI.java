package org.cyberpwn.launchkit.gui;

import java.awt.EventQueue;
import java.awt.TextField;
import java.awt.Window.Type;

import javax.swing.JFrame;

public class LaunchKitUI
{
	public LaunchKitUI()
	{
		
	}

	public void start()
	{
		EventQueue.invokeLater(this::build);
	}
	
	private void build()
	{
		LKUI frame = new LKUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
