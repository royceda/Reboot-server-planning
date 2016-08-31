package kp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Random;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		knapsack kp = new knapsack();
	
		
		HashMap<Integer, int[]> pref1 = new 	HashMap<Integer, int[]>();
		pref1.put(0, new int[]{1,2,3});
		pref1.put(1, new int[]{0});
		pref1.put(2, new int[]{5,4,0});
		pref1.put(9, new int[]{5,2,1});
		
		int pred1[] = {1,2,3,-1};
		
		String test = kp.binPacking(100,72, 2, pred1, pref1);
		
		System.out.println(test);
		
		PrintWriter writer = null;
		String result = "";
		
		
		for(int n = 10; n<=180; n+=60){ //item
			for(int m = 12; m<=72; m+=12){ //bin
				for(int p = 1; p <= Math.floor(0.5*m); p+=5 ){ //pref
					for(int s = 2; s<=3; s++){
						for(int c = 0; c<= Math.floor(0.5*n); c+=5){ //cluster
									
							if(n <= m*s){	
								
								//create preferencies
								HashMap<Integer, int[]> pref = new 	HashMap<Integer, int[]>();
								for(int i =0; i<n; i++){
									int[] tab = new int[n];
									for(int k = 0; k< tab.length; k++){
										Random rand = new Random();
										int randomNum = rand.nextInt((m-1 - 0) + 1) + 0;
										tab[k] = randomNum;
									}
									pref.put(i, tab);
								}
								
								//create precedencies
								int[]  pred = new int[c];
								for(int i = 1; i< c; i++){
									if(c%5 != 0)
										pred[i] = i-1;
									else
										pred[i] = -1;
								}
								System.out.println("\nTest: n = "+n+", m = "+m+", cluster = "+c+", nb preference max = "+p+", capacity: "+s);
						
								result += kp.binPacking(n,m, s, pred, pref);
								
								writer = new PrintWriter("output/bp/test2.out", "UTF-8");
								
								writer.print(result);
								writer.close();
							}
							
						}
					}
				}
			}
		}

	}

}
