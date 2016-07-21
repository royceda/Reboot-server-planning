package bp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Parser {

	int n ;
	int m ;
	
	int cluster;
	
	int w[] ;
	int c[] ;
	
	int pred[];
	int pref[][];
	
	public void generate(int n, int m) throws FileNotFoundException, UnsupportedEncodingException{
		
		this.n = n;
		this.m = m;
		
		//first line
		String str = n+" "+m+"\n";
		
		//second line: capacity
		for(int i = 0; i<m; i++){
			str += 2+" ";
		}
		str  += "\n";
		
		
		//third line: size
		for(int i = 0; i<n; i++){
			str += 1+" ";
		}
		str  += "\n";
		
		
		//Precedencies
		for(int i = 0; i<n/2; i++){
			int p = randInt(0,n);
			str += p+" ";
		}
		str += "\n";
		
		
		//Creneaux
		for(int i = 0; i<n; i++){
			int lim = n/2;
			
			for(int j = 0; j<lim; j++){
				int pref = randInt(0, m); 
				str += pref+" ";
			}
			str += "\n";
		}
		
		PrintWriter writer = new PrintWriter("input/bp/test.in", "UTF-8");
		writer.println(str);
		writer.close();
	}

	
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max-1 - min) + 1) + min;

	    return randomNum;
	}
	
	
	
	public void create(int n, int m, int cluster, int p){
		this.n = n;
		this.m = m;
		
		//capacity
		c = new int[m];
		Arrays.fill(c, 2);
		
		
		//size
		w = new int[n];
		Arrays.fill(w, 1);
		
		
		
		//precedences
		cluster = cluster;
		
		
		//creneaux
		pref = new int[n][];
		ArrayList<Integer> witness = new ArrayList<Integer>();
		for(int i = 0; i<n; i++){
			int lim = randInt(0, p);
			pref[i] = new int[lim];
			for(int j =0; j<lim; j++){
				int a =  randInt(0, m);
				if(!witness.contains(a)){
					pref[i][j] = a;
					witness.add(a);
				}else{
					pref[i][j] = a;
					witness.add(a);
				}
					
			}
		}
		
	}
	
	
	
	public void parse() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader("input/bp/test.in"));		
		//BufferedReader br = new BufferedReader(new FileReader("input/test-20-96-2.in"));		
		String line;
		
		//first
		line = br.readLine();
		String[] vars = line.split(" ");
		
		this.n = Integer.parseInt(vars[0]);
		this.m = Integer.parseInt(vars[1]);
		
		
		
		//capacity
		c = new int[m];
		line = br.readLine();
		vars = line.split(" ");
		for(int i = 0; i<vars.length; i++){
			c[i] = Integer.parseInt(vars[i]);
		}
		
		
		//size
		w = new int[n];
		line = br.readLine();
		vars = line.split(" ");
		for(int i = 0; i<vars.length; i++){
			w[i] = Integer.parseInt(vars[i]);
		}
		
		
		//Precedences
		pred = new int[n/2];
		for(int i = 0; i<n/2; i++){
			line = br.readLine();
			vars = line.split(" ");
			pred[i] = Integer.parseInt(vars[0]);
		}
		
		
		//creneaux
		pref = new int[n][];
		ArrayList<Integer> witness = new ArrayList<Integer>();
		for(int i = 0; i<n; i++){
			line = br.readLine();
			
			if(line == null)
				break;
		
			vars = line.split(" ");
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			for(int j = 0; j<vars.length; j++){
				if(!vars[j].contentEquals("")){
					int a = Integer.parseInt(vars[j]);
					if(!witness.contains(a)){
						list.add(a);
						witness.add(a);
					}
				}
			}
				
			pref[i] = new int[list.size()];
			for(int j =0; j< list.size(); j++){
				pref[i][j] = list.get(j);
			}
		}
		
	}
	
}
