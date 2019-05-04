package com.bn;

import java.awt.*;
import java.util.List;

import javax.swing.*;

public class DrawPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	static final int span=8;
	HDTData hdtd;
	int pwidth;
	int pheight;
	
	CLODUtil cu;
	
	public DrawPanel(HDTData hdtd)
	{
		this.hdtd=hdtd;
		pwidth=span*hdtd.width+20;
		pheight=span*hdtd.height+20;
		this.setPreferredSize(new Dimension(pwidth,pheight));
		
		cu=new CLODUtil(hdtd);
	}

	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, pwidth,pheight);
		
		g.setColor(Color.green);
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
			g.drawLine(line[0]*span+10, line[1]*span+10, line[2]*span+10, line[3]*span+10);
		}
	}
}
