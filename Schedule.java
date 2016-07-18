import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.objective.ParetoOptimizer;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;
import org.chocosolver.util.tools.ArrayUtils;

import static org.chocosolver.solver.search.strategy.Search.*;

public class Schedule {

public static void planning(Parser parse) throws ContradictionException{
		System.out.println("Planning");
		Model model = new Model("Planning");
		
	
		/********* Data **********/
		int n             = parse.n;
		int T             = parse.T;
		int phi           = parse.phi;
		int pred[]        = parse.pred;	
		int[][] domains   = parse.domains;
		int night[][]     = parse.nights;
		int morning[][]   = parse.mornings;
		int afternoon[][] = parse.afternoons;
		int pref[][]      = parse.pref;
		
		
		/********* Variables **********/
		IntVar[] x     = model.intVarArray("x",n, 0, T);
		IntVar   z     = model.intVar("z",0,n*n*T*100, false);
		IntVar   z1    = model.intVar("z1",0,n*n*T*100, false);
		IntVar[][] y   = model.intVarMatrix("y",T,n, 0, 1);  
		
		
		
		/********* Domains **********/
		for(int i = 0; i<n; i++){
			x[i] = model.intVar("x["+i+"]", domains[i]);
		}
		
		
		/********* Preferences **********/
		/*for(int i = 0; i<n; i++){
			x[i] = model.intVar("x["+i+"]", parse.domains[i]);
		}*/
		
		
		/********* Objectives **********/
		
	
		//obj: makespan
		model.sum(x, "=", z1).post();
		model.times(z1,3,z).post();
		//model.setObjective(Model.MINIMIZE, z);
		
		
		//obj: distances
		IntVar[]   dist    = model.intVarArray("dist",n*n,0,100, false);
		int l = 0;
		
		for(int i =0; i<n; i++){
			for(int j=0; j<n; j++){
				if(i != j){
					model.distance(x[i], x[j], "=", dist[l]).post();
					l++;
				}
			}
		}
		
		
		IntVar sumDist = model.intVar("sumDist",0,n*n*T, false);
		IntVar sumDistNeg = model.intVar("sumDistNeg",-n*n*T,0, false);
		model.sum(dist, "=", sumDist).post();
		model.times(sumDist, -1, sumDistNeg).post();
		//model.setObjective(Model.MINIMIZE, sumDistNeg);
		
		
		//ParetoOptimizer po = new ParetoOptimizer(Model.MINIMIZE,new IntVar[]{z, sumDistNeg});
		//Solver solver = model.getSolver();
		//solver.plugMonitor(po);
		
		
		//obj: preferences
	/*	IntVar[] p     = model.intVarArray("p",n*pref.length, 0, 10000000);
		
		//x[i] - p[i][j]
		for(int i =0; i<n; i++){
			for(int j=0; j<pref.length; j++){
				model.arithm(x[i], "-", p[i+j], "=", pref[i][j]).post();
			}
		}
		
		
		IntVar   z2    = model.intVar("z2",0,n*n*T*100, false);
		model.sum(p, "=", z2).post();
		model.setObjective(Model.MINIMIZE, z2);*/
		
		
		//objective total
		IntVar obj1 = model.intVar("obj1",-n*n*T,n*n*T*100, false);
		IntVar obj2 = model.intVar("obj1",-n*n*T,n*n*T*100, false);
		model.arithm(sumDistNeg, "+", z, "=", obj1).post();
		//model.arithm(obj2, "+", z2, "=", obj1).post();
		model.setObjective(Model.MINIMIZE, obj2);
		
		
		
		
		/********* Constraintes **********/
		model.allDifferent(x, "DEFAULT").post(); //All different
		
		/*for(int i = 0; i< pred.length; i++){ //Precedences
			int p = pred[i];
			model.arithm(x[i],">",x[p]).post();
		}*/
		
		
		//2 morning, 2 afternoon and 2 night
		for(int i = 0; i<n; i++){ 
			for(int j =0; j<T; j++){
				model.ifThenElse(model.arithm(x[i], "=", j), model.arithm(y[j][i],"=", 1),  model.arithm(y[j][i],"=", 0));
			}
		}
		
	
		for(int i = 1; i<T/24; i++){
			int k = 0;
			
			//Morning
			IntVar[] sumMorning = model.intVarArray("sumMorning",morning[i].length, 0, 100000);
			for(int j : morning[i]){
				model.sum(y[j], "=", sumMorning[k]).post();
				k++;
			}
			model.sum(sumMorning,"<=", 2).post();
			
			
			//Afternoon
			IntVar[] sumAfternoon = model.intVarArray("sumAfternoon",afternoon[i].length, 0, 100000);
			k = 0;
			for(int j : afternoon[i]){
				model.sum(y[j], "=", sumAfternoon[k]).post();
				k++;
			}
			model.sum(sumAfternoon,"<=", 2).post();
			
			//night	
			IntVar[] sumNight = model.intVarArray("sumNight",night[i].length, 0, 100000);
			k = 0;
			for(int j : night[i]){
				model.sum(y[j], "=", sumNight[k]).post();
				k++;
			}
			model.sum(sumNight,"<=", 2).post();
		}
		
		
		//Solving
		//model.getSolver().plugMonitor(po);
		//model.getSolver().propagate();
		//System.out.println("Solution found by propagation (objective = "+model.getSolver().getBestSolutionValue()+")");
		
		
		model.getSolver().limitTime("30s");
		//model.getSolver().setSearch(activityBasedSearch(x));
		//model.getSolver().setSearch(minDomLBSearch(x));
		
		
		model.getSolver().showStatistics();
		while(model.getSolver().solve()){	
			System.out.println(z1);
			//System.out.println(z2);
			System.out.println(sumDist);
			
			for(int i = 0; i<n; i++)
				System.out.println(x[i]);
			
			
			/*for(int i = 0; i<n; i++){
				for(int j =0; j<T; j++){
					System.out.println(y[j][i]);
				}
			}*/
		}
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
	}
	
	
	
	
	public static void main(String[] args) throws ContradictionException, IOException {
		Parser p = new Parser();
		
		p.generateTask();
		p.parser();
		
		planning(p);
	}






}
