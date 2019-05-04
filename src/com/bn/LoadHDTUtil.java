package com.bn;

import java.io.*;

public class LoadHDTUtil 
{
	
	public static HDTData loadFromFile(String fname)
	{
		try
		{
			FileInputStream fin=new FileInputStream("hdt/"+fname+".bnhdt");
			byte[] buf=new byte[4];
			fin.read(buf, 0, 4);
			int width=ConvertUtil.fromBytesToInt(buf);
			fin.read(buf, 0, 4);
			int height=ConvertUtil.fromBytesToInt(buf);				
			int[][] data=new int[width][height];
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					fin.read(buf, 0, 1);
					data[i][j]=buf[0]&0xff;
				}
			}	
			fin.close();
			return new HDTData(width,height,data);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
