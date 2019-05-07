package com.bn;

import java.util.*;

public class CLODUtil 
{
	final int thold=16;//阈值
	HDTData hdtd;
	int maxLevel;//最大树层数，从1开始数
	int[][] treeData;
	int[][] DHMatrix;//存储顶点dh的矩阵
	public static final int span=8;
	
	public static int cameraX = 100;
	public static int cameraZ = 100;
	public static int movePace = 2;
	
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
		
		//计算出第1层的中心点 行列号
		
	}
	
	public void genTerrain(){
		mesh.clear();
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
	public void modifyDHMatrix(){//计算dh  从最小的网格开始  向上计算dh值  保证相邻的网格拆分的程度差别不会大于1
		int edgeLength = 2;
		while(edgeLength<=hdtd.width){
			int halfEdgeLength=edgeLength>>1; 
			int halfChildEdgeLength=edgeLength>>2;
			//System.out.println("halfEdgeLength"+halfEdgeLength+" halfChildEdgeLength:"+halfChildEdgeLength);
			for(int z=halfEdgeLength;z<hdtd.height;z+=edgeLength){  
				for(int x=halfEdgeLength;x<hdtd.width;x+=edgeLength)  
					if(edgeLength==2){  		//最小的网格
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
					} else {  				//不是最小的网格时  那就需要查看相邻网格的dh情况 取最大值
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
						for(int i=0;i<14;i++){  //取最大的dh
							if(DHMax<DH14[i])  
								DHMax=DH14[i];  
						}  
						setDHMatrix(x,z,DHMax);               
				}  
			}  
			edgeLength=edgeLength<<1;  			
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
	    float fViewDistance,f;
		int halfChildEdgeLength;
		int childEdgeLength;
		boolean blend;

		//以下为考虑了与摄像机的距离作为是否拆分的计算方法
		int scale = 100;
		fViewDistance= (float)Math.abs(Math.sqrt((col*span-cameraX)*(col*span-cameraX)+(row*span-cameraZ)*(row*span-cameraZ)));//frustum.distanceOfTwoPoints(centerQuad);  //计算与摄像机的距离
		fViewDistance*=scale;
		int mfMinResolution = 4;
		int mfDetailLevel =6;
		f = fViewDistance/(edgeLength*mfMinResolution*mfDetailLevel*(Math.max(getDHMatrix(col,row),1.0f)));
		
		if(f<1.0f){
			blend=true;
			treeData[col][row]=1;
		}else{
			blend=false;
			//System.out.println("f:"+f);
			treeData[col][row]=0;
		}
		
		
		//此时判断是否拆分 只考虑了某点的粗糙度 超过阈值才需要拆分
//		if(getDHMatrix(col,row)>20){
//			blend=true;
//			treeData[col][row]=1;
//		}else{
//			blend=false;
//			treeData[col][row]=0;
//		}

		
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

		//若为无效节点则生成
		if(flag==9){
			//若为叶子节点则生成数据
			// 0-1-2
			// |\|/|
			// 3-4-5
			// |/|\|
			// 6-7-8
			
			//System.out.println("col:"+col+" row:"+row+" "+getDHMatrix(col,row)+" "+flag);
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
	    //当前层跨度的两倍
	    int up = lp<<1; 
	    
	    int neighborX;  
		int neighborZ;  
		if(flag==0)//不需要拆分
		{		
			//0-1
			mesh.add(new int[]{col-lp,row-lp,col,row-lp});
			//1-2
			mesh.add(new int[]{col,row-lp,col+lp,row-lp});
			
			// 							0-1-2
			// 							|\|/|
			// 查看这个点是否拆分 	 *- 3-4-5 
			// 							|/|\|
			// 							6-7-8
			neighborX = col-up;  
			neighborZ = row;  
			if(neighborX<=0||(treeData[neighborX][neighborZ]!=9)){  
				//3-4
				mesh.add(new int[]{col-lp,row,col,row});
			}
			
			

			// 0-1-2
			// |\|/|
			// 3-4-5-*   查看这个点是否拆分
			// |/|\|
			// 6-7-8
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
			
			
			//   *      查看这个点是否拆分
			//   |
			// 0-1-2
			// |\|/|
			// 3-4-5
			// |/|\|
			// 6-7-8
			neighborX = col;  
			neighborZ = row-up;  
			if(neighborZ<=0||(treeData[neighborX][neighborZ]!=9)){  
				//1-4
				mesh.add(new int[]{col,row-lp,col,row});
			}
			

			// 0-1-2
			// |\|/|
			// 3-4-5
			// |/|\|
			// 6-7-8
			//   |
			//   *      查看这个点是否拆分
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
		if(flag==1)//需要拆分
		{
			//先拿到四个孩子的标志  左上  右上  左下  右下
		    int ul=treeData[col-lpn][row-lpn];
		    int ur=treeData[col+lpn][row-lpn];
		    int ll=treeData[col-lpn][row+lpn];
		    int lr=treeData[col+lpn][row+lpn];
		    
		    //如果左上角孩子不是无效节点去孩子
		    if(ul!=9)
		    {
		    	traversalGenMesh(col-lpn,row-lpn, currLevel+1);
		    }
		    else
		    {//如果孩子是无效节点
		    	//0-1
				mesh.add(new int[]{col-lp,row-lp,col,row-lp});
				//0-3
				mesh.add(new int[]{col-lp,row-lp,col-lp,row});
				//3-4
				mesh.add(new int[]{col-lp,row,col,row});
				//1-4
				mesh.add(new int[]{col,row-lp,col,row});
				//0-4
				mesh.add(new int[]{col-lp,row-lp,col,row});
		    }
		    
		    //如果右上角孩子不是无效节点去孩子
		    if(ur!=9)
		    {
		    	traversalGenMesh(col+lpn,row-lpn, currLevel+1);
		    }
		    else
		    {//如果孩子是无效节点
		    	//1-2
				mesh.add(new int[]{col,row-lp,col+lp,row-lp});
				//1-4
				mesh.add(new int[]{col,row-lp,col,row});
				//4-5
				mesh.add(new int[]{col,row,col+lp,row});
				//2-5
				mesh.add(new int[]{col+lp,row-lp,col+lp,row});
				//2-4
				mesh.add(new int[]{col+lp,row-lp,col,row});
		    }
		    
		    //如果左下角孩子不是无效节点去孩子
		    if(ll!=9)
		    {
		    	traversalGenMesh(col-lpn,row+lpn, currLevel+1);
		    }
		    else
		    {//如果孩子是无效节点
		    	//3-6
				mesh.add(new int[]{col-lp,row,col-lp,row+lp});
				//3-4
				mesh.add(new int[]{col-lp,row,col,row});
				//4-7
				mesh.add(new int[]{col,row,col,row+lp});
				//6-7
				mesh.add(new int[]{col-lp,row+lp,col,row+lp});
				//4-6
				mesh.add(new int[]{col,row,col-lp,row+lp});	
		    }
		    
		    //如果右下角孩子不是无效节点去孩子
		    if(lr!=9)
		    {
		    	traversalGenMesh(col+lpn,row+lpn, currLevel+1);
		    }
		    else
		    {//如果孩子是无效节点
		    	//4-5
				mesh.add(new int[]{col,row,col+lp,row});
				//4-7
				mesh.add(new int[]{col,row,col,row+lp});
				//5-8
				mesh.add(new int[]{col+lp,row,col+lp,row+lp});
				//7-8
				mesh.add(new int[]{col,row+lp,col+lp,row+lp});
				//4-8
				mesh.add(new int[]{col,row,col+lp,row+lp});
		    }
		}		
	}
}
