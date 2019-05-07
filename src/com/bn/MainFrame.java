package com.bn;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	public static DrawPanel dp;
	public MainFrame()
	{
		this.setTitle("CLOD Quard Tree");
		this.setLayout(null);
		
		HDTData hdtd=LoadHDTUtil.loadFromFile("big257x257");
		dp=new DrawPanel(hdtd);
		JScrollPane jsp=new JScrollPane(dp);
		jsp.setBounds(10,10,1060,900);		
		this.add(jsp);
		
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				//System.out.println("keyCode:"+e.getKeyCode());
				if(e.getKeyCode()==37){//вС
					CLODUtil.cameraX-=CLODUtil.movePace;
				}else if(e.getKeyCode()==38){//ио
					CLODUtil.cameraZ-=CLODUtil.movePace;
				}else if(e.getKeyCode()==39){//ср
					CLODUtil.cameraX+=CLODUtil.movePace;
				}else if(e.getKeyCode()==40){//об
					CLODUtil.cameraZ+=CLODUtil.movePace;
				}
			}
		 
			public void keyReleased(KeyEvent e) {
				
			}
		 
			public void keyTyped(KeyEvent e) {
				
			}
		});
		
		this.setBounds(100,0,1120,1000);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
	
	public static void main(String args[])
	{
		new MainFrame();

		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				while(true) {
				
					
					dp.repaint();
			    	try{
						Thread.sleep(100);
					}catch(Exception e){
						e.printStackTrace();
					}
		    	}
			}
		});
        t.start();
	}
}
