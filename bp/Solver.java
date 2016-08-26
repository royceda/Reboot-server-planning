package com.afklm.cockpit.planning;

import static org.chocosolver.solver.search.strategy.Search.activityBasedSearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Solver {

	
	private int n;
	private int m;


	public void preprocessing(){}

	public int[][] firstModel(int n, int m, int cap, int cluster, HashMap<Integer, Integer> pred){
		System.out.println("Bin packing");
	
		
		/***** Init and Data *****/
		
		this.n = n;
		this.m = m;
		
		int[] c = new int[m];
		Arrays.fill(c, cap);
		
		int[] w = new int[n];
		Arrays.fill(w, 1);
		
		
		Model model = new Model("Bin packing");
		
		
		/***** Variables *****/
		IntVar[][] x  = model.intVarMatrix("x",n,m, 0, 1);
		IntVar[][] xt = model.intVarMatrix("xt",m,n, 0, 1);
		IntVar[]   y  = model.intVarArray("y", m, 0,1);
		

		/***** Objectives *****/
		IntVar   z  = model.intVar("z", 0, m);
		model.sum(y, "=", z).post();
		model.setObjective(Model.MINIMIZE, z);
		
		
		
		/***** Constraints *****/
			
		// pre-traitements: m x n cts: transpose
		for(int i = 0; i<n; i++){
			for(int j = 0; j<m; j++){
				model.arithm(x[i][j], "=", xt[j][i]).post();			
			}
		}
		
		
		//contraintes de KP
		for(int j = 0; j<m; j++){
			//IntVar W = model.intVar("W", 0, m);
			//model.times(y[j], c[j], W).post();		
			IntVar W = model.intScaleView(y[j], c[j]); 
			
			model.scalar(xt[j], w, "<=", W).post();
		}
		
		
		//Une seul et unique affectation
		for(int i = 0; i<n; i++){
			model.sum(x[i], "=", 1).post();
		}
		
		
		//Precedences pour cluster avec tableau
		for(int i = 1; i<pred.size(); i+=2){
			for(int j = 0; j<m; j++){
				if(pred.get(i) != null){
					int predId = pred.get(i);	
					//model.ifThen(model.arithm(x[predId][j], "=", 1), model.arithm(x[i][j],"=", 0));			
					for(int k = j-1; k>=0; k--){
						model.ifThen(model.arithm(x[i][j], "=", 1), model.arithm(x[predId][k],"=", 0));
						//model.arithm(x[i][j], ">=", x[predId][k]).post();
					}
				}
			}
		}
		
		
		
		/*for(int i = 1; i<pred.size(); i+=2){
			for(int j = 0; j<m; j++){
				if(pred.get(i) != null){
					int predId = pred.get(i);	
					//model.ifThen(model.arithm(x[predId][j], "=", 1), model.arithm(x[i][j],"=", 0));			
					for(int k = j-1; k>=0; k--){
						model.arithm(x[i][j], ">=", x[predId][k]).post();
						//model.ifThen(model.arithm(x[i][j], "=", 1), model.arithm(x[predId][k],"=", 0));
					}
				}
			}
		}*/
		
		
		
		//Temporaire
		  Random rand = new Random();

		  // nextInt is normally exclusive of the top value,
		  // so add 1 to make it inclusive
		  //  int randomNum = rand.nextInt((max-1 - min) + 1) + min;
		
		//Creneaux et preference
		Constraint[][] cts = new Constraint[n][];
		for(int i = 0; i<n; i++){
			int lim = rand.nextInt((12-1 - 1) + 1) + 1;// number of pref
			cts[i] = new Constraint[lim];
			
			for(int j = 0; j<lim; j++){
				int pref = rand.nextInt((m-1 - 0) + 1) + 0;
				cts[i][j] = model.arithm(x[i][pref], "=", 1);
			}
			model.or(cts[i]).post();
		}
		
		
		
		
		/***** Solving *****/
		model.getSolver().limitTime("5s");
		//model.getSolver().propagate();
		
		
	

		//model.getSolver().setSearch(setVarSearch(y));  // use activity-based search (classical black box search)	
		model.getSolver().showStatistics();
		while(model.getSolver().solve()){	
			System.out.println(z);
			for(int j = 0; j<m; j++){
				if(y[j].getValue() == 1)
					System.out.println(y[j]);
				for(int i = 0; i<n; i++)
					if(x[i][j].getValue() == 1){

						System.out.println(x[i][j]);
					}
			}
		}
		
		
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
		model.getSolver().printStatistics();
		
		
		if(model.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
		
		
		int test[] = new int[m];
		int X[][] = new int[n][m];
		Arrays.fill(test, 0);
		for(int i = 0; i<n; i++){
			for(int j = 0; j<m; j++){
				if(x[i][j].getValue() == 1){
					X[i][j] = 1;
					test[j]++;
				}
				
			}
		}
		
		
		/***** Report *****/	
		String result = n+","+m+","+model.getSolver().getBestSolutionValue()+","+model.getSolver().getTimeCount()+","+model.getSolver().getNodeCount()+","+model.getSolver().getSolutionCount()+","+cluster+","+model.getSolver().isObjectiveOptimal()+",\n";
		return X;

	}
	
	public int[][] secondModel(int n, int m, int cap, int cluster, HashMap<Integer, Integer> pred){
		
		System.out.println("Bin packing");
		
		
		/***** Init and Data *****/
		this.n = n;
		this.m = m;
		//int cap = 1;
		
		int[] prefI = new int[n];
		Arrays.fill(prefI, 0);
		int[] c = new int[m];
		Arrays.fill(c, cap);
		int[] w = new int[n];
		Arrays.fill(w, 1);
		
		
		Model model = new Model("Bin packing");
			
		
		/*** Constraints **/
		IntVar[] binLoad = model.intVarArray("binLoad", m, 0, cap);//la charge de chaque bin
		int[] itemSize = new int[n];
		Arrays.fill(itemSize, 1);
		IntVar[] itemBin = model.intVarArray("itemBin", n, 0, m); //le bin de chaque item
		
		//BinPacking
		model.binPacking(itemBin, itemSize , binLoad , 0).post();
		

		
		//Precedences
		for(int i = 0; i<n; i++){
			if(pred.get(i) != null){
				int predId = pred.get(i);	
				model.arithm(itemBin[i], ">=", itemBin[predId]).post();
			}
		}
		
		
		/***Objectifs ***/
		IntVar z = model.intVar("z", 0, m*m);
		IntVar obj = model.intVar("obj", 0, m*m);
		IntVar obj1 = model.intVar("obj1", 0, m*m);
		IntVar sumD = model.intVar("sumD", 0, m*m);
		IntVar sumP = model.intVar("sumP", 0, m*m);
		IntVar dist[] = model.intVarArray(n, 0, m*m);
		
		
		
		//Distance
		for(int i = 0; i<n; i++){
			if(pred.get(i) != null){
				int predId = pred.get(i);
				model.distance(itemBin[i], itemBin[predId], "=", dist[i]).post();
			}
		}
		model.sum(dist,"=",sumD).post();	
		
		IntVar distPref[] = model.intVarArray(n, 0, m*m);
		IntVar abs[] = model.intVarArray(n, 0, m*m);
		IntVar pref[] = model.intVarArray(n, prefI);
		
		//Preferences
		for(int i = 0; i<n; i++){
			model.distance(itemBin[i], pref[i], "=", distPref[i]).post();
		}
		model.sum(distPref,"=", sumP).post();
		
		
		//first bin 
		int[] coeffs = new int[m];
		for(int j = 0; j<m; j++){
			coeffs[j] = j;
		}
		model.scalar(binLoad, coeffs , "=", z).post();
		//model.sum(itemBin, "=", z).post();
		
		//portfolio
		model.arithm(sumD, "+", z, "=", obj).post();
		model.arithm(obj, "-", sumP, "=", obj1).post();
		model.setObjective(Model.MINIMIZE, obj1);
		
			
			
		/***** Solving *****/
		model.getSolver().limitTime("10s");
		//model.getSolver().propagate();
		
		//model.getSolver().setSearch(setVarSearch(y));  // use activity-based search (classical black box search)	
		model.getSolver().showStatistics();
		model.getSolver().solve();
		while(model.getSolver().solve()){	
			for(int i = 0; i<n; i++){
				System.out.println(itemBin[i]);	
			}	
			for(int j = 0; j<m; j++){
				if(binLoad[j].getValue() != 0)
					System.out.println(binLoad[j]);
			}
		}
		
			
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
		model.getSolver().printStatistics();
		
		for(int i = 0; i<n; i++){
			System.out.println(itemBin[i]);	
		}	
		
		for(int j = 0; j<m; j++){
			if(binLoad[j].getValue() != 0)
				System.out.println(binLoad[j]);
		}
		
		if(model.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
		
		
		int X[][] = new int[n][m];
		for(int i = 0; i<n; i++){
			X[i][itemBin[i].getValue()] = 1;
		}
		return X;
	}
	public void postprocessing(){}	

}
