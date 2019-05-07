package com.bn;

import java.util.*;

public class CLODUtil 
{
	final int thold=16;//��ֵ
	HDTData hdtd;
	int maxLevel;//�������������1��ʼ��
	int[][] treeData;
	int[][] DHMatrix;//�洢����dh�ľ���
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
				treeData[i][j]=9;//9����˽ڵ�δ���� 0����Ҷ�ӽڵ㣬1�����Ҷ�ӽڵ�				
			}
		}
		
		modifyDHMatrix();
		
		//�������1������ĵ� ���к�
		
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
	public void modifyDHMatrix(){//����dh  ����С������ʼ  ���ϼ���dhֵ  ��֤���ڵ������ֵĳ̶Ȳ�𲻻����1
		int edgeLength = 2;
		while(edgeLength<=hdtd.width){
			int halfEdgeLength=edgeLength>>1; 
			int halfChildEdgeLength=edgeLength>>2;
			//System.out.println("halfEdgeLength"+halfEdgeLength+" halfChildEdgeLength:"+halfChildEdgeLength);
			for(int z=halfEdgeLength;z<hdtd.height;z+=edgeLength){  
				for(int x=halfEdgeLength;x<hdtd.width;x+=edgeLength)  
					if(edgeLength==2){  		//��С������
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
					} else {  				//������С������ʱ  �Ǿ���Ҫ�鿴���������dh��� ȡ���ֵ
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
						for(int i=0;i<14;i++){  //ȡ����dh
							if(DHMax<DH14[i])  
								DHMax=DH14[i];  
						}  
						setDHMatrix(x,z,DHMax);               
				}  
			}  
			edgeLength=edgeLength<<1;  			
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
	    float fViewDistance,f;
		int halfChildEdgeLength;
		int childEdgeLength;
		boolean blend;

		//����Ϊ��������������ľ�����Ϊ�Ƿ��ֵļ��㷽��
		int scale = 100;
		fViewDistance= (float)Math.abs(Math.sqrt((col*span-cameraX)*(col*span-cameraX)+(row*span-cameraZ)*(row*span-cameraZ)));//frustum.distanceOfTwoPoints(centerQuad);  //������������ľ���
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
		
		
		//��ʱ�ж��Ƿ��� ֻ������ĳ��Ĵֲڶ� ������ֵ����Ҫ���
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
	
	//���ݲ�ֵ���������������ݵķ���
	public void traversalGenMesh(int col,int row, int currLevel)
	{	
		if(col<0||col>hdtd.width||row<0||row>hdtd.width)return;
		//ȡ����ǰ�ڵ��־ֵ
		int flag=treeData[col][row];

		//��Ϊ��Ч�ڵ�������
		if(flag==9){
			//��ΪҶ�ӽڵ�����������
			// 0-1-2
			// |\|/|
			// 3-4-5
			// |/|\|
			// 6-7-8
			
			//System.out.println("col:"+col+" row:"+row+" "+getDHMatrix(col,row)+" "+flag);
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
	    //��ǰ���ȵ�����
	    int up = lp<<1; 
	    
	    int neighborX;  
		int neighborZ;  
		if(flag==0)//����Ҫ���
		{		
			//0-1
			mesh.add(new int[]{col-lp,row-lp,col,row-lp});
			//1-2
			mesh.add(new int[]{col,row-lp,col+lp,row-lp});
			
			// 							0-1-2
			// 							|\|/|
			// �鿴������Ƿ��� 	 *- 3-4-5 
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
			// 3-4-5-*   �鿴������Ƿ���
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
			
			
			//   *      �鿴������Ƿ���
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
			//   *      �鿴������Ƿ���
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
		if(flag==1)//��Ҫ���
		{
			//���õ��ĸ����ӵı�־  ����  ����  ����  ����
		    int ul=treeData[col-lpn][row-lpn];
		    int ur=treeData[col+lpn][row-lpn];
		    int ll=treeData[col-lpn][row+lpn];
		    int lr=treeData[col+lpn][row+lpn];
		    
		    //������ϽǺ��Ӳ�����Ч�ڵ�ȥ����
		    if(ul!=9)
		    {
		    	traversalGenMesh(col-lpn,row-lpn, currLevel+1);
		    }
		    else
		    {//�����������Ч�ڵ�
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
		    
		    //������ϽǺ��Ӳ�����Ч�ڵ�ȥ����
		    if(ur!=9)
		    {
		    	traversalGenMesh(col+lpn,row-lpn, currLevel+1);
		    }
		    else
		    {//�����������Ч�ڵ�
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
		    
		    //������½Ǻ��Ӳ�����Ч�ڵ�ȥ����
		    if(ll!=9)
		    {
		    	traversalGenMesh(col-lpn,row+lpn, currLevel+1);
		    }
		    else
		    {//�����������Ч�ڵ�
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
		    
		    //������½Ǻ��Ӳ�����Ч�ڵ�ȥ����
		    if(lr!=9)
		    {
		    	traversalGenMesh(col+lpn,row+lpn, currLevel+1);
		    }
		    else
		    {//�����������Ч�ڵ�
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
