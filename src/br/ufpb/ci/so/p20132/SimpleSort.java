package br.ufpb.ci.so.p20132;


/**
 * Classe que ordena um vetor de 100 elementos utilizando o BubbleSort.
 */
public class SimpleSort {
	
	public static void BubbleSort( int [ ] num ) {
	     int j;
	     boolean flag = true;  
	     int temp;  

	     while ( flag ) {
	            flag= false; 
	            for( j=0;  j < num.length -1;  j++ ) {
	                   if ( num[ j ] < num[j+1] ) {
	                           temp = num[ j ];   
	                           num[ j ] = num[ j+1 ];
	                           num[ j+1 ] = temp;
	                          flag = true;    
	                  } 
	            } 
	      } 
	}

	
	
	public static void main(String args[]) {
		
		int num[] = new int[20];
		
		for( int i = 0; i < 20; i++){
			num[i] = (int) (20 * Math.random());
			System.out.print( num[i] + " ");
		}
		System.out.println();
		
		BubbleSort(num);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for( int i = 0; i < 20; i++)
			System.out.print( num[i] + " ");
		System.out.println();
	
	}	

}
