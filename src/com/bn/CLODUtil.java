package com.bn;

import java.util.*;

public class CLODUtil 
{
	final int thold=16;//阈值
	HDTData hdtd;
	int maxLevel;//最大树层数，从1开始数
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
				treeData[i][j]=9;//9代表此节点未处理 0代表叶子节点，1代表非叶子节点				
			}
		}
		
		modifyDHMatrix();
		
//		for(int i=0;i<hdtd.width;i++){
//			for(int j=0;j<hdtd.width;j++){
//				System.out.print(""+DHMatrix[i][j]+" ");
//			}
//			System.out.println();
//		}
		//计算出第1层的中心点 行列号
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
	
	
	//判断一个矩形是否需要切分的方法
	//计算当前矩形的粗糙度
	// 0-1-2
	// |\|/|
	// 3-4-5
	// |/|\|
	// 6-7-8
	public boolean needQF(int col,int row,int lp)
	{
		int[][] offset={{-1,-1},{0,-1},{1,-1}, {-1,0},{0,0},{1,0}, {-1,1},{0,1},{1,1},};
		//取出1~9号点的灰度值
		int[] hd=new int[9];
		for(int i=0;i<9;i++)
		{
			hd[i]=hdtd.data[col+offset[i][0]*lp][row+offset[i][1]*lp];
		}
		
		//计算中心点到上下左右四个点的灰度差以及对角线的四个点的灰度差
		int[] dh=new int[6];		
        dh[0]=Math.abs(hd[4]-hd[1]);
        dh[1]=Math.abs(hd[4]-hd[3]);
        dh[2]=Math.abs(hd[4]-hd[5]);
        dh[3]=Math.abs(hd[4]-hd[7]);
        dh[4]=Math.abs(hd[0]-hd[8]);
        dh[5]=Math.abs(hd[6]-hd[2]);
        
        //找到最大的高度差
        int max=Integer.MIN_VALUE;
        for(int i=0;i<6;i++)
        {
        	if(max<dh[i])
        	{
        		max=dh[i];
        	}
        }
        
        //若最大高度差大于指定阈值
        if(max>thold)
        {
        	return true;
        }
        else
        {
        	return false;
        }	
	}
	
	//遍历计算是否需要拆分的递归方法(返回值为true表示需要细分，为false表示不需要细分)
	public void traversal(int col,int row, int edgeLength)
	{		
		//如果是最后一层直接判断自己切不切，并返回上一层		
		if(edgeLength< 2){
			treeData[col][row]=9;
			
			return;			
		}		

	    //如果自己的四个孩子都不需要切分，看看自己需不需要拆分
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
	
	//根据拆分的情况生成网格数据的方法
	public void traversalGenMesh(int col,int row, int currLevel)
	{	
		if(col<0||col>hdtd.width||row<0||row>hdtd.width)return;
		//取出当前节点标志值
		int flag=treeData[col][row];

		//若为无效节点则返回
		if(flag==9){
			System.out.println("col:"+col+" row:"+row+" "+getDHMatrix(col,row)+" "+flag);
			return;
		}
		//若为叶子节点则生成数据
		// 0-1-2
		// |\|/|
		// 3-4-5
		// |/|\|
		// 6-7-8
		//计算出当前层的跨度
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
		
		//若为非叶子节点
		//计算下一层的跨度
		int lpn=(hdtd.width-1)/((int)(Math.pow(2, currLevel+1)));	
		if(flag==1)
		{
			//先拿到四个孩子的标志  左上  右上  左下  右下
		    int ul=treeData[col-lpn][row-lpn];
		    int ur=treeData[col+lpn][row-lpn];
		    int ll=treeData[col-lpn][row+lpn];
		    int lr=treeData[col+lpn][row+lpn];
		    
		    
		    //如果左上角孩子不是无效节点去孩子
//		    if(ul!=9)
//		    {
		    	traversalGenMesh(col-lpn,row-lpn, currLevel+1);
//		    }
//		    else
//		    {//如果孩子是无效节点
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
//		    //如果右上角孩子不是无效节点去孩子
//		    if(ur!=9)
//		    {
		    	traversalGenMesh(col+lpn,row-lpn, currLevel+1);
//		    }
//		    else
//		    {//如果孩子是无效节点
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
//		    //如果左下角孩子不是无效节点去孩子
//		    if(ll!=9)
//		    {
		    	traversalGenMesh(col-lpn,row+lpn, currLevel+1);
//		    }
//		    else
//		    {//如果孩子是无效节点
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
//		    //如果右下角孩子不是无效节点去孩子
//		    if(lr!=9)
//		    {
		    	traversalGenMesh(col+lpn,row+lpn, currLevel+1);
//		    }
//		    else
//		    {//如果孩子是无效节点
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
