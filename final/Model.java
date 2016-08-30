package finale;

import java.util.Arrays;
import java.util.HashMap;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

public class Model {

	
	private int n;
	private int m;
	private int cap;
	
		
		
		
		
	public  String solve(int n, int m, int cap, int[] pred, HashMap<Integer, int[]> mapPref){	
		
		System.out.println("Bin packing");
	
		
		/***** Init and Data *****/
		
		this.n = n;
		this.m = m;
		this.cap = cap;
		
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
		for(int i = 0; i<pred.length; i++){
			if(pred[i] != -1)
				model.arithm(itemBin[i], ">=", itemBin[pred[i]]).post();
		}
		
		
		/***Objectifs ***/
		IntVar z = model.intVar("z", 0, m*m);
		IntVar obj = model.intVar("obj", 0, m*m);
		IntVar obj1 = model.intVar("obj1", 0, m*m);
		IntVar sumD = model.intVar("sumD", 0, m*m);
		IntVar sumP = model.intVar("sumP", 0, m*m);
		IntVar dist[] = model.intVarArray(n, 0, m*m);
		
		
		//min bins
		/*int[] coeffs = new int[m];
		for(int j = 0; j<m; j++){
			coeffs[j] = m-j;
		}
		model.scalar(binLoad, coeffs , "=", z).post();*/	
		//model.sum(itemBin, "=", z).post();
		
		
		//precedences dist
		for(int i = 1; i<n; i++){
			model.distance(itemBin[i], itemBin[i-1], "=", dist[i]).post();
		}
		model.sum(dist,"=",sumD).post();
		
		
		IntVar abs[] = model.intVarArray(n, 0, m*m);
		
		
			
		//Preferences multiples
		/*IntVar distPref[][] = model.intVarMatrix(n, m, 0, m*m);
		IntVar min[] = model.intVarArray(n, 0, m*m);	
		for(int i = 0; i<n; i++){ //item
			if(mapPref.get(i) != null){
				IntVar pref[] = model.intVarArray(n,  mapPref.get(i)); //pref de i			
				IntVar distP[] = model.intVarArray(pref.length, 0, m*m);
				for(int k = 0; k<pref.length; k++){		
					int a = pref[k].getValue();
					int b = distP[k].getValue();
					model.distance(itemBin[i], pref[k], "=", distP[k]).post();
				}
				model.min(min[i], distP).post();			
			}
		}
		model.sum(min,"=", sumP).post();*/
	
		
		
		//preferences simples
		IntVar distPref[] = model.intVarArray( n, 0, m*m);
		for(int i = 0; i<n; i++){
			if(mapPref.get(i) != null){
				IntVar pref = model.intVar(  mapPref.get(i)[0]); //pref de i	
				model.distance(itemBin[i], pref, "=", distPref[i]).post();
			}
		}
		model.sum(distPref,"=", sumP).post();
		
	
		//Portfolio
		model.arithm(sumD, "+", sumP, "=", obj1).post();
		//model.arithm(obj1, "+", Z, "=", obj).post();
		model.setObjective(Model.MINIMIZE, obj1);
		
		
		
		/***** Solving *****/
		model.getSolver().limitTime("10s");
		//model.getSolver().propagate();
		
		//model.getSolver().setSearch(setVarSearch(y));  // use activity-based search (classical black box search)	
		model.getSolver().showStatistics();
		model.getSolver().solve();
		while(model.getSolver().solve()){	
		/*	for(int i = 0; i<n; i++){
				System.out.println(itemBin[i]);	
			}	
			for(int j = 0; j<m; j++){
				if(binLoad[j].getValue() > 0)
					System.out.println(binLoad[j]);
			}*/
		}
		
		
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
		model.getSolver().printStatistics();
		
		/*for(int i = 0; i<n; i++){
			System.out.println(itemBin[i]);	
		}	
		for(int j = 0; j<m; j++){
			if(binLoad[j].getValue() > 0)
				System.out.println(binLoad[j]);
		}*/
		
		if(model.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
		
		
		String result = n+","+m+","+model.getSolver().getBestSolutionValue()+","+model.getSolver().getTimeCount()+","+model.getSolver().getNodeCount()+","+model.getSolver().getSolutionCount()+","+pred.length+","+model.getSolver().isObjectiveOptimal()+",\n";
		return result;
	}
	
}

