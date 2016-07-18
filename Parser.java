import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Parser {
	
	int n;
	int T;
	int phi;
	
	int[][] mornings;
	int[][] nights;
	int[][] afternoons;
	int[][] alls;
	
	int[]   pred;
	int[][] pref;
	int[][] domains;
	
	
	public static void generateTask() throws FileNotFoundException, UnsupportedEncodingException{
		
		int n   = 40;
		int T   = 168;
		int phi = 2;
		
		//first line
		String str = n+" "+T+" "+phi+"\n";
		
		
		
		//precedences
		int rand1 = 0 + (int)(Math.random() * n); 
		for(int i = 0; i<rand1; i++){
			int random1 = 0 + (int)(Math.random() * n); 
			str += random1+" ";
		}
		str += "\n";
		
		
		
		//preferences
		for(int i = 0; i<n; i++){
			int random1 = 0 + (int)(Math.random() * T); 
			for(int j = 0+(int)(Math.random()*random1); j<random1; j++){
				int random = 0 + (int)(Math.random() * T); 
				str += random+" ";
			}
			str += "\n";
		}
		
		
		//domains
		for(int i = 0; i<n; i++){	
			for(int k=0; k<T; k++ ){
				str += k+" ";
			}
			str += "\n";
		}
		
		PrintWriter writer = new PrintWriter("input/test.in", "UTF-8");
		writer.println(str);
		writer.close();
		
		//System.out.println(str);
	}
	
	
	public void parser() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader("input/test.in"));		
		//BufferedReader br = new BufferedReader(new FileReader("input/toto.in"));		
		String line;
		
		//first line
		line          = br.readLine();
		String[] vars = line.split(" ");
		this.n        = Integer.parseInt(vars[0]);
		this.T        = Integer.parseInt(vars[1]);
		this.phi      = Integer.parseInt(vars[2]);
		
		
		this.pred      = new int[n];
		this.pref      = new int[n][T];
		this.domains   = new int[n][T];
		
		
		int k = 0;
		ArrayList<Integer> nightL     = new ArrayList<Integer>();
		ArrayList<Integer> morningL   = new ArrayList<Integer>();
		ArrayList<Integer> afternoonL = new ArrayList<Integer>();
		
		
		int size = T/24;
		this.mornings   = new int[size][];
		this.afternoons = new int[size][];
		this.nights     = new int[size][];
		
		
		for(int i =0; i<T; i += 24){
			while(k < i){	
				for(int j = 0; j<7; j++){
					nightL.add(k);
					k++;
					if(k>=i){
						break;
					}
				}
				
				if(k>=i){
					break;
				}
				
				for(int j = 0; j<6; j++){
					morningL.add(k);
					k++;
					if(k>=i){
						break;
					}
				}
			
				if(k>=i){
					break;
				}
			
				for(int j = 0; j<6; j++){
					afternoonL.add(k);
					k++;
					if(k>=i){
						break;
					}
				}
			
				if(k>=i){
					break;
				}
			
				for(int j = 0; j<5; j++){
					nightL.add(k);
					k++;
					if(k>=i){
					break;
					}
				}	
			}
			
			
			this.mornings[i/24]   = new int[morningL.size()];
			this.nights[i/24]     = new int[nightL.size()];
			this.afternoons[i/24] = new int[afternoonL.size()];
			
			
			for(int j =0; j<nightL.size(); j++){
				nights[i/24][j] = nightL.get(j);
			}
			
			for(int j =0; j<morningL.size(); j++){
				mornings[i/24][j] = morningL.get(j);
			}
			
			for(int j =0; j<afternoonL.size(); j++){
				afternoons[i/24][j] = afternoonL.get(j);
			}	
			
			nightL.clear();
			morningL.clear();
			afternoonL.clear();
			
		}
		
		
		

		
		//second line
		line = br.readLine();
		vars = line.split(" ");	
		for(int i =0; i<vars.length; i++){
			this.pred[i] = Integer.parseInt(vars[i]);
		}
		
		for(int i = vars.length; i <this.n; i++){
			pred[i] = 0;
		}
		
		
		//preferences
		vars = new String[T];	
		for(int i = 0; i<this.n; i++){
			vars = new String[T];
			line = br.readLine();
			vars = line.split(" ");
			
			for(int j = 0; j<vars.length; j++){
				this.pref[i][j] = Integer.parseInt(vars[j]);
			}	
		}
		
		
		//domains	
		for(int i = 0; i<this.n; i++){
			line = br.readLine();
			vars = line.split(" ");
			for(int j = 0; j<vars.length; j++){
				if(vars[j] != "")
					this.domains[i][j] = Integer.parseInt(vars[j]);
			}
		}
		
		br.close();
		System.out.println("Parse OK");
	}
}
