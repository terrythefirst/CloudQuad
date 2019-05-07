package com.bn;

import java.awt.*;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class DrawPanel extends JPanel
{
	private Image ibuffer ;
	private static final long serialVersionUID = 1L;
	
	HDTData hdtd;
	int pwidth;
	int pheight;

	CLODUtil cu;
	
	public DrawPanel(HDTData hdtd)
	{
		this.hdtd=hdtd;
		pwidth=CLODUtil.span*hdtd.width+20;
		pheight=CLODUtil.span*hdtd.height+20;
		this.setPreferredSize(new Dimension(pwidth,pheight));

		cu=new CLODUtil(hdtd);
	}
	
	public void update(Graphics graphics) {
        repaint();
    }
	@Override
    public void paint(Graphics g) {
    	//super.paint(g);
		
    	ibuffer = createImage(this.getWidth(),this.getHeight());
    	Graphics gg = ibuffer.getGraphics();
    	myPaint(gg);
    	g.drawImage(ibuffer,0,0,this);
    }
    
    public void myPaint(Graphics graphics) {
    	//¿¹¾â³Ý(¼Óµ½»æ»­Ç°)
		Graphics2D g2d=(Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                      RenderingHints.VALUE_ANTIALIAS_ON);
		
		cu.genTerrain();
		
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, pwidth,pheight);
		
		graphics.setColor(Color.green);
//		for(int i=0;i<hdtd.width;i++)
//		{
//			for(int j=0;j<hdtd.height;j++)
//			{
//				int xStart=i*span+10;
//				int yStart=j*span+10;
//				g.drawLine(xStart, yStart, xStart+span, yStart);
//				g.drawLine(xStart, yStart+span, xStart+span, yStart+span);
//				g.drawLine(xStart, yStart, xStart, yStart+span);
//				g.drawLine(xStart+span, yStart, xStart+span, yStart+span);
//			}
//		}
		
		List<int[]> mesh=cu.mesh;
		for(int[] line:mesh)
		{
			graphics.drawLine(line[0]*CLODUtil.span+10, line[1]*CLODUtil.span+10, line[2]*CLODUtil.span+10, line[3]*CLODUtil.span+10);
		}
		
		graphics.setColor(Color.orange);
		graphics.fillOval(CLODUtil.cameraX-5, CLODUtil.cameraZ-5, 10, 10);
		graphics.drawOval(CLODUtil.cameraX-10, CLODUtil.cameraZ-10, 20, 20);
		graphics.drawOval(CLODUtil.cameraX-11, CLODUtil.cameraZ-11, 22, 22);
	}
}
