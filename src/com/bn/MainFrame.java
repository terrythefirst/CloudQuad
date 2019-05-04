package com.bn;

import javax.swing.*;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public MainFrame()
	{
		this.setTitle("CLOD Quard Tree");
		this.setLayout(null);
		
		HDTData hdtd=LoadHDTUtil.loadFromFile("big257x257");
		DrawPanel dp=new DrawPanel(hdtd);
		JScrollPane jsp=new JScrollPane(dp);
		jsp.setBounds(10,10,1060,900);		
		this.add(jsp);
		
		this.setBounds(100,0,1120,1000);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String args[])
	{
		new MainFrame();
	}
}
