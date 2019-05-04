package com.bn;

import java.util.*;

public class CLODUtil 
{
	final int thold=16;//��ֵ
	HDTData hdtd;
	int maxLevel;//�������������1��ʼ��
	int[][] treeData;
	int[][] DHMatrix;
	
	List<int[]> mesh=new ArrayList<int[]>();
		
	public CLODUtil(HDTData hdtd)
	{
		this.hdtd=hdtd;
		maxLevel=(int)(Math.log(hdtd.width-1)/Math.log(2));	
		
		treeData=new int[hdtd.width][hdtd.height];
		DHMatrix = new int[hdtd.width][hdtd.height];
		for(int i=0;i<hdtd.width;i++)
		{
			for(int j=0;j<hdtd.height;j++)
			{
				treeData[i][j]=9;//9����˽ڵ�δ���� 0����Ҷ�ӽڵ㣬1�����Ҷ�ӽڵ�				
			}
		}
		
		modifyDHMatrix();
		
//		for(int i=0;i<hdtd.width;i++){
//			for(int j=0;j<hdtd.width;j++){
//				System.out.print(""+DHMatrix[i][j]+" ");
//			}
//			System.out.println();
//		}
		//�������1������ĵ� ���к�
		int center=(hdtd.width)/((int)(Math.pow(2, 1)));	
		traversal(center,center,hdtd.width);
		traversalGenMesh(center,center,1);
	}
	public int getRawHeight(int x,int z){
		return hdtd.data[x][z];
	}
	public void  setDHMatrix(int x,int z, int val){
		DHMatrix[x][z] = val;
	}
	public int getDHMatrix(int x,int z){
		return DHMatrix[x][z];
	}
	public void modifyDHMatrix(){
		int edgeLength = 2;
		while(edgeLength<=hdtd.width){
			int halfEdgeLength=edgeLength>>1; 
			int halfChildEdgeLength=edgeLength>>2;
			//System.out.println("halfEdgeLength"+halfEdgeLength+" halfChildEdgeLength:"+halfChildEdgeLength);
			for(int z=halfEdgeLength;z<hdtd.height;z+=edgeLength){  
				for(int x=halfEdgeLength;x<hdtd.width;x+=edgeLength)  
					if(edgeLength==2){  
						int[] DH6=new int[6];  
						DH6[0] = Math.abs(((getRawHeight(x-halfEdgeLength,z+halfEdgeLength)+getRawHeight(x+halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z+halfEdgeLength));  
						DH6[1] = Math.abs(((getRawHeight(x+halfEdgeLength,z+halfEdgeLength)+getRawHeight(x+halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x+halfEdgeLength,z));  
						DH6[2] = Math.abs(((getRawHeight(x-halfEdgeLength,z-halfEdgeLength)+getRawHeight(x+halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x,z-halfEdgeLength));  
						DH6[3] = Math.abs(((getRawHeight(x-halfEdgeLength,z+halfEdgeLength)+getRawHeight(x-halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x-halfEdgeLength,z));  
						DH6[4] = Math.abs(((getRawHeight(x-halfEdgeLength,z-halfEdgeLength)+getRawHeight(x+halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z));  
						DH6[5] = Math.abs(((getRawHeight(x+halfEdgeLength,z-halfEdgeLength)+getRawHeight(x-halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z));  
						int DHMax=DH6[0];  
						for(int i=1;i<6;i++){  
							if(DHMax<DH6[i])  
								DHMax=DH6[i];  
						}  
						setDHMatrix(x,z,DHMax);  
					} else {  
						int DH14[] = new int[14];  
						int numDH=0;
						
						int neighborX;  
						int neighborZ;  
						
						neighborX = x-edgeLength;  
						neighborZ = z;  
						if(neighborX>0){  
							DH14[numDH]=getDHMatrix(neighborX+halfChildEdgeLength,neighborZ-halfChildEdgeLength);  
							numDH++;  
							DH14[numDH]=getDHMatrix(neighborX+halfChildEdgeLength,neighborZ+halfChildEdgeLength);  
							numDH++;  
						}  
						
						neighborX=x;  
						neighborZ=z-edgeLength;  
						if(neighborZ>0){  
							DH14[numDH]=getDHMatrix(neighborX-halfChildEdgeLength,neighborZ+halfChildEdgeLength);  
							numDH++;  
							DH14[numDH]=getDHMatrix(neighborX+halfChildEdgeLength,neighborZ+halfChildEdgeLength);  
							numDH++;  
						}  
						
						neighborX=x+edgeLength;  
						neighborZ=z;  
						if(neighborX<hdtd.width)  {  
							DH14[numDH]=getDHMatrix(neighborX-halfChildEdgeLength,neighborZ-halfChildEdgeLength);  
							numDH++;  
							DH14[numDH]=getDHMatrix(neighborX-halfChildEdgeLength,neighborZ+halfChildEdgeLength);  
							numDH++;  
						}  
						
						neighborX=x;  
						neighborZ=z+edgeLength;  
						if(neighborZ<hdtd.width){  
							DH14[numDH]=getDHMatrix(neighborX-halfChildEdgeLength,neighborZ-halfChildEdgeLength);  
							numDH++;  
							DH14[numDH]=getDHMatrix(neighborX+halfChildEdgeLength,neighborZ-halfChildEdgeLength);  
							numDH++;  
						}  
						
						DH14[numDH]=Math.abs(((getRawHeight(x-halfEdgeLength,z+halfEdgeLength)+getRawHeight(x+halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z+halfEdgeLength));  
						numDH++;  
						DH14[numDH]=Math.abs(((getRawHeight(x+halfEdgeLength,z+halfEdgeLength)+getRawHeight(x+halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x+halfEdgeLength,z));  
						numDH++;  
						DH14[numDH]=Math.abs(((getRawHeight(x-halfEdgeLength,z-halfEdgeLength)+getRawHeight(x+halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x,z-halfEdgeLength));  
						numDH++;  
						DH14[numDH]=Math.abs(((getRawHeight(x-halfEdgeLength,z+halfEdgeLength)+getRawHeight(x-halfEdgeLength,z-halfEdgeLength))>>1)-getRawHeight(x-halfEdgeLength,z));  
						numDH++;  
						DH14[numDH]=Math.abs(((getRawHeight(x-halfEdgeLength,z-halfEdgeLength)+getRawHeight(x+halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z));  
						numDH++;  
						DH14[numDH]=Math.abs(((getRawHeight(x+halfEdgeLength,z-halfEdgeLength)+getRawHeight(x-halfEdgeLength,z+halfEdgeLength))>>1)-getRawHeight(x,z));  
						numDH++;  
						int DHMax=DH14[0];  
						for(int i=0;i<14;i++){  
							if(DHMax<DH14[i])  
								DHMax=DH14[i];  
						}  
						setDHMatrix(x,z,DHMax);               
				}  
			}  
			edgeLength=edgeLength<<1;  			
		}
	}
	
	
	//�ж�һ�������Ƿ���Ҫ�зֵķ���
	//���㵱ǰ���εĴֲڶ�
	// 0-1-2
	// |\|/|
	// 3-4-5
	// |/|\|
	// 6-7-8
	public boolean needQF(int col,int row,int lp)
	{
		int[][] offset={{-1,-1},{0,-1},{1,-1}, {-1,0},{0,0},{1,0}, {-1,1},{0,1},{1,1},};
		//ȡ��1~9�ŵ�ĻҶ�ֵ
		int[] hd=new int[9];
		for(int i=0;i<9;i++)
		{
			hd[i]=hdtd.data[col+offset[i][0]*lp][row+offset[i][1]*lp];
		}
		
		//�������ĵ㵽���������ĸ���ĻҶȲ��Լ��Խ��ߵ��ĸ���ĻҶȲ�
		int[] dh=new int[6];		
        dh[0]=Math.abs(hd[4]-hd[1]);
        dh[1]=Math.abs(hd[4]-hd[3]);
        dh[2]=Math.abs(hd[4]-hd[5]);
        dh[3]=Math.abs(hd[4]-hd[7]);
        dh[4]=Math.abs(hd[0]-hd[8]);
        dh[5]=Math.abs(hd[6]-hd[2]);
        
        //�ҵ����ĸ߶Ȳ�
        int max=Integer.MIN_VALUE;
        for(int i=0;i<6;i++)
        {
        	if(max<dh[i])
        	{
        		max=dh[i];
        	}
        }
        
        //�����߶Ȳ����ָ����ֵ
        if(max>thold)
        {
        	return true;
        }
        else
        {
        	return false;
        }	
	}
	
	//���������Ƿ���Ҫ��ֵĵݹ鷽��(����ֵΪtrue��ʾ��Ҫϸ�֣�Ϊfalse��ʾ����Ҫϸ��)
	public void traversal(int col,int row, int edgeLength)
	{		
		//��������һ��ֱ���ж��Լ��в��У���������һ��		
		if(edgeLength< 2){
			treeData[col][row]=9;
			
			return;			
		}		

	    //����Լ����ĸ����Ӷ�����Ҫ�з֣������Լ��費��Ҫ���
		//boolean flag = needQF(col,row,lp);
	    float fViewDistance=10,f;
		int halfChildEdgeLength;
		int childEdgeLength;
		boolean blend;

		//fViewDistance=frustum.distanceOfTwoPoints(centerQuad);
		int mfMinResolution = thold;
		int mfDetailLevel = 1;
		f = fViewDistance/(edgeLength*mfMinResolution*(Math.max(mfDetailLevel*getDHMatrix(col,row),1.0f)));
	
		if(f<1.0f){
			blend=true;
			treeData[col][row]=1;
		}else{
			blend=false;
			treeData[col][row]=0;
		}
		
		if(getDHMatrix(col,row)>20){
			blend=true;
			treeData[col][row]=1;
		}else{
			blend=false;
			treeData[col][row]=0;
		}
		//System.out.println(""+col+" "+row+" flag:"+treeData[col][row]+" edgeLength:"+treeData[col][row]);
		
		if(blend){
			halfChildEdgeLength 	= edgeLength>>2;
			childEdgeLength 		= edgeLength>>1;
		
        	traversal(col-halfChildEdgeLength,row-halfChildEdgeLength,childEdgeLength);
        	traversal(col+halfChildEdgeLength,row-halfChildEdgeLength,childEdgeLength);
        	traversal(col-halfChildEdgeLength,row+halfChildEdgeLength,childEdgeLength);
        	traversal(col+halfChildEdgeLength,row+halfChildEdgeLength,childEdgeLength);
		}
	}
	
	//���ݲ�ֵ���������������ݵķ���
	public void traversalGenMesh(int col,int row, int currLevel)
	{	
		if(col<0||col>hdtd.width||row<0||row>hdtd.width)return;
		//ȡ����ǰ�ڵ��־ֵ
		int flag=treeData[col][row];

		//��Ϊ��Ч�ڵ��򷵻�
		if(flag==9){
			System.out.println("col:"+col+" row:"+row+" "+getDHMatrix(col,row)+" "+flag);
			return;
		}
		//��ΪҶ�ӽڵ�����������
		// 0-1-2
		// |\|/|
		// 3-4-5
		// |/|\|
		// 6-7-8
		//�������ǰ��Ŀ��
	    int lp=(hdtd.width)/((int)(Math.pow(2, currLevel)));	
	    int up = lp<<1;
	    int neighborX;  
		int neighborZ;  
		if(flag==0)
		{		
			//0-1
			mesh.add(new int[]{col-lp,row-lp,col,row-lp});
			//1-2
			mesh.add(new int[]{col,row-lp,col+lp,row-lp});
			
			
			
			neighborX = col-up;  
			neighborZ = row;  
			if(neighborX<=0||(treeData[neighborX][neighborZ]!=9)){  
				//3-4
				mesh.add(new int[]{col-lp,row,col,row});
			}
			
			
			neighborX = col+up;  
			neighborZ = row;  
			if(neighborX>=hdtd.width||(treeData[neighborX][neighborZ]!=9)){  
				//4-5
				mesh.add(new int[]{col,row,col+lp,row});
			}
			
			
			//6-7
			mesh.add(new int[]{col-lp,row+lp,col,row+lp});
			//7-8
			mesh.add(new int[]{col,row+lp,col+lp,row+lp});
			//0-3
			mesh.add(new int[]{col-lp,row-lp,col-lp,row});
			//3-6
			mesh.add(new int[]{col-lp,row,col-lp,row+lp});
			
			neighborX = col;  
			neighborZ = row-up;  
			if(neighborZ<=0||(treeData[neighborX][neighborZ]!=9)){  
				//1-4
				mesh.add(new int[]{col,row-lp,col,row});
			}
			
			neighborX = col;  
			neighborZ = row+up;  
			if(neighborZ>=hdtd.width||(treeData[neighborX][neighborZ]!=9)){  
				//4-7
				mesh.add(new int[]{col,row,col,row+lp});
			}
			
			
			//2-5
			mesh.add(new int[]{col+lp,row-lp,col+lp,row});
			//5-8
			mesh.add(new int[]{col+lp,row,col+lp,row+lp});
			//0-4
			mesh.add(new int[]{col-lp,row-lp,col,row});
			//4-8
			mesh.add(new int[]{col,row,col+lp,row+lp});
			//2-4
			mesh.add(new int[]{col+lp,row-lp,col,row});
			//4-6
			mesh.add(new int[]{col,row,col-lp,row+lp});	
			return;
		}
		
		//��Ϊ��Ҷ�ӽڵ�
		//������һ��Ŀ��
		int lpn=(hdtd.width-1)/((int)(Math.pow(2, currLevel+1)));	
		if(flag==1)
		{
			//���õ��ĸ����ӵı�־  ����  ����  ����  ����
		    int ul=treeData[col-lpn][row-lpn];
		    int ur=treeData[col+lpn][row-lpn];
		    int ll=treeData[col-lpn][row+lpn];
		    int lr=treeData[col+lpn][row+lpn];
		    
		    
		    //������ϽǺ��Ӳ�����Ч�ڵ�ȥ����
//		    if(ul!=9)
//		    {
		    	traversalGenMesh(col-lpn,row-lpn, currLevel+1);
//		    }
//		    else
//		    {//�����������Ч�ڵ�
//		    	//0-1
//				mesh.add(new int[]{col-lp,row-lp,col,row-lp});
//				//0-3
//				mesh.add(new int[]{col-lp,row-lp,col-lp,row});
//				//3-4
//				mesh.add(new int[]{col-lp,row,col,row});
//				//1-4
//				mesh.add(new int[]{col,row-lp,col,row});
//				//0-4
//				mesh.add(new int[]{col-lp,row-lp,col,row});
//		    }
//		    
//		    //������ϽǺ��Ӳ�����Ч�ڵ�ȥ����
//		    if(ur!=9)
//		    {
		    	traversalGenMesh(col+lpn,row-lpn, currLevel+1);
//		    }
//		    else
//		    {//�����������Ч�ڵ�
//		    	//1-2
//				mesh.add(new int[]{col,row-lp,col+lp,row-lp});
//				//1-4
//				mesh.add(new int[]{col,row-lp,col,row});
//				//4-5
//				mesh.add(new int[]{col,row,col+lp,row});
//				//2-5
//				mesh.add(new int[]{col+lp,row-lp,col+lp,row});
//				//2-4
//				mesh.add(new int[]{col+lp,row-lp,col,row});
//		    }
//		    
//		    //������½Ǻ��Ӳ�����Ч�ڵ�ȥ����
//		    if(ll!=9)
//		    {
		    	traversalGenMesh(col-lpn,row+lpn, currLevel+1);
//		    }
//		    else
//		    {//�����������Ч�ڵ�
//		    	//3-6
//				mesh.add(new int[]{col-lp,row,col-lp,row+lp});
//				//3-4
//				mesh.add(new int[]{col-lp,row,col,row});
//				//4-7
//				mesh.add(new int[]{col,row,col,row+lp});
//				//6-7
//				mesh.add(new int[]{col-lp,row+lp,col,row+lp});
//				//4-6
//				mesh.add(new int[]{col,row,col-lp,row+lp});	
//		    }
//		    
//		    //������½Ǻ��Ӳ�����Ч�ڵ�ȥ����
//		    if(lr!=9)
//		    {
		    	traversalGenMesh(col+lpn,row+lpn, currLevel+1);
//		    }
//		    else
//		    {//�����������Ч�ڵ�
//		    	//4-5
//				mesh.add(new int[]{col,row,col+lp,row});
//				//4-7
//				mesh.add(new int[]{col,row,col,row+lp});
//				//5-8
//				mesh.add(new int[]{col+lp,row,col+lp,row+lp});
//				//7-8
//				mesh.add(new int[]{col,row+lp,col+lp,row+lp});
//				//4-8
//				mesh.add(new int[]{col,row,col+lp,row+lp});
//		    }
		}		
	}
}
