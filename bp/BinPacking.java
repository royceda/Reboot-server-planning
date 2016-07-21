package bp;
import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;



public class BinPacking {
	
	private int n = 80; //nb item
	private int m = 48; //nb bins
	
	
	public BinPacking(){}
	
	
	public BinPacking(int n, int m){
		this.n = n;
		this.m = m;
	}
	
	
	
	public Model makeModel(){
		
		/***** Init and Data *****/
		
		int[] c = new int[m]; //capacities
		int[] w = new int[n]; //sizes
		
		Arrays.fill(c, 2);
		Arrays.fill(w, 1);
		
		
		Model model = new Model("Bin packing");
		
		
		/***** Variables *****/
		IntVar[][] x  = model.intVarMatrix("x",n,m, 0, 1);
		IntVar[][] xt = model.intVarMatrix("x",m,n, 0, 1);
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
			IntVar W = model.intScaleView(y[j], c[j]); 
			model.scalar(xt[j], w, "<=", W).post();
		}
		
		
		//Une seul et unique affectation
		for(int i = 0; i<n; i++){
			model.sum(x[i], "=", 1).post();
		}
		
		
		//Precedences
		for(int j = 0; j<m; j++){
			model.ifThen(model.arithm(x[1][j], "=", 1), model.arithm(x[2][j],"=", 0));			
			for(int k = j; k>=0; k--){
				model.ifThen(model.arithm(x[1][j], "=", 1), model.arithm(x[2][k],"=", 0));
			}
		}
		
		
		//Creneaux
		int pref[] = {0,1,2,3};
		Constraint[] cts = new Constraint[pref.length];
		
		for(int j = 0; j<pref.length; j++){
			cts[j] = model.arithm(x[1][pref[j]], "=", 1);
		}
		model.or(cts).post();
		
		return model;
		
	}
	
	
	
	public void multiSolve(){
		
		ParallelPortfolio portfolio = new ParallelPortfolio();
		int nbModels = 5;
		for(int s=0;s<nbModels;s++){
		    portfolio.addModel(makeModel());
		}
		portfolio.solve();
		
		Model mo = portfolio.getBestModel();
		System.out.println("Number of bins: "+mo.getSolver().getBestSolutionValue());
		
		
		for(int i =0; i< mo.getNbVars(); i++){
			IntVar t = (IntVar) mo.getVar(i);
			if(t.getValue() == 1 ){
				System.out.println("var: "+t.getName()+" = "+t.getValue());
			}
		}
		
		
		if(mo.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
	}
	
	
	
	public String solve(Parser parse) throws ContradictionException{
		System.out.println("Bin packing");
	
		
		/***** Init and Data *****/
		
		this.n = parse.n;
		this.m = parse.m;
		
		int[] c = parse.c;
		int[] w = parse.w;
		
		
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
			IntVar W = model.intScaleView(y[j], c[j]); 
			model.scalar(xt[j], w, "<=", W).post();
		}
		
		
		//Une seul et unique affectation
		for(int i = 0; i<n; i++){
			model.sum(x[i], "=", 1).post();
		}
		
		
		//Precedences pour cluster
		for(int i = 1; i<parse.cluster; i++){
			for(int j = 0; j<m; j++){
				model.ifThen(model.arithm(x[i-1][j], "=", 1), model.arithm(x[i][j],"=", 0));			
				for(int k = j; k>=0; k--){
					model.ifThen(model.arithm(x[i-1][j], "=", 1), model.arithm(x[i][k],"=", 0));
				}
			}
		}
		
		
		//Creneaux
		Constraint[][] cts = new Constraint[n][];
		for(int i = 0; i<n; i++){
			int lim = parse.randInt(1,12); // number of pref
			cts[i] = new Constraint[lim];
			
			for(int j = 0; j<lim; j++){
				int pref = parse.randInt(0, m);
				cts[i][j] = model.arithm(x[i][pref], "=", 1);
			}
			model.or(cts[i]).post();
		}
		
		
		
		
		/***** Solving *****/
		model.getSolver().limitTime("30s");
		//model.getSolver().propagate();
		
		
		
		//model.getSolver().setSearch(setVarSearch(y));  // use activity-based search (classical black box search)	
		//model.getSolver().showStatistics();
		while(model.getSolver().solve()){	
			/*System.out.println(z);
			for(int j = 0; j<m; j++){
				if(y[j].getValue() == 1)
					System.out.println(y[j]);
				for(int i = 0; i<n; i++)
					if(x[i][j].getValue() == 1)
						System.out.println(x[i][j]);			
			}*/
		}
		
		
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
		model.getSolver().printStatistics();
		
		
		
		
		if(model.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
		
		
		/***** Report *****/	
		String result = n+";"+m+";"+model.getSolver().getTimeCount()+";"+model.getSolver().getNodeCount()+";"+model.getSolver().getSolutionCount()+";"+parse.cluster+";"+model.getSolver().isObjectiveOptimal()+";\n";
		return result;
	}
	
	
	
	
	public void solve(){
	System.out.println("Bin packing");
	
		
		/***** Init and Data *****/
		
		int[] c = new int[m];
		int[] w = new int[n];
		
		Arrays.fill(c, 3);
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
			IntVar W = model.intScaleView(y[j], c[j]); 
			model.scalar(xt[j], w, "<=", W).post();
		}
		
		
		//Une seul et unique affectation
		for(int i = 0; i<n; i++){
			model.sum(x[i], "=", 1).post();
		}
		
		
		//Precedences
		for(int i = 1; i<n; i+=3){
			for(int j = 0; j<m; j++){
				model.ifThen(model.arithm(x[i-1][j], "=", 1), model.arithm(x[i][j],"=", 0));			
				for(int k = j; k>=0; k--){
					model.ifThen(model.arithm(x[i-1][j], "=", 1), model.arithm(x[i][k],"=", 0));
				}
			}
		}
		
		
		//Creneaux
		int pref[] = {1,2,3,4,5};
		Constraint[] cts = new Constraint[pref.length];
		
		for(int j = 0; j<pref.length; j++){
			cts[j] = model.arithm(x[1][pref[j]], "=", 1);
		}
		model.or(cts).post();
		
		
		
		/***** Solving *****/
		model.getSolver().limitTime("30s");
		//model.getSolver().propagate();
		
		
		
		//model.getSolver().setSearch(setVarSearch(y));  // use activity-based search (classical black box search)	
		model.getSolver().showStatistics();
		while(model.getSolver().solve()){	
			System.out.println(z);
			for(int j = 0; j<m; j++){
				if(y[j].getValue() == 1)
					System.out.println(y[j]);
				for(int i = 0; i<n; i++)
					if(x[i][j].getValue() == 1)
					System.out.println(x[i][j]);			
			}
		}
		
		System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");
		if(model.getSolver().isObjectiveOptimal()){
			System.out.println("OPTIMAL !!!!");
		}else{
			System.out.println("NON OPTIMAL !!!!");
		}
	}
	
	
}
