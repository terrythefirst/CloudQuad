package com.bn;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ConvertUtil 
{
	public static byte fromIntToUnsignedByte(int data)
	{
		 return (byte)(data&0x0FF);
	}
	
	public static int fromUnsignedByteToInt(byte signedByte)
	{
	    return 0xFF&signedByte;
	}
	
	//将字符串转换为字节数组，按照UTF-8格式
	public static byte[] fromStringToBytes(String s)
	{
		byte[] ba=s.getBytes(Charset.forName("UTF-8"));
		return ba;
	}
	
	//将浮点数转换为字节数组
	public static byte[] fromFloatToBytes(float f)
	{
		int ti=Float.floatToIntBits(f);
		return fromIntToBytes(ti);
	}
	
	//将整数变为四字节数组，索引大的字节为高位
	public static byte[] fromIntToBytes(int k)
	{
		byte[] buff = new byte[4];
		buff[0]=(byte)(k&0x000000FF);
		buff[1]=(byte)((k&0x0000FF00)>>>8);
		buff[2]=(byte)((k&0x00FF0000)>>>16);
		buff[3]=(byte)((k&0xFF000000)>>>24);
		
		return buff;
	}
	
	public static byte[] fromIntToBytesForColor(int k)
	{
		byte[] buff = new byte[4];
		buff[2]=(byte)(k&0x000000FF);
		buff[1]=(byte)((k&0x0000FF00)>>>8);
		buff[0]=(byte)((k&0x00FF0000)>>>16);
		buff[3]=(byte)((k&0xFF000000)>>>24);
		
		return buff;
	}
	
	//将字节数组转化为字符串
	public static String fromBytesToString(byte[] bufId)
	{
		String s=null;		
		try 
		{
			s=new String(bufId,"UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	
	//将字节数组转四字节整数
	public static int fromBytesToInt(byte[] buff)
	{
		return (buff[3] << 24) 
			+ ((buff[2] << 24) >>> 8) 
			+ ((buff[1] << 24) >>> 16) 
			+ ((buff[0] << 24) >>> 24);
	}
	
	public static float fromBytesToFloat(byte[] buf)
	{
		int k= fromBytesToInt(buf);		
		return Float.intBitsToFloat(k);
	}
}
