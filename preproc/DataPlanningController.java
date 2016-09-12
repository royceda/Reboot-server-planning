

public class DataPlanningController{


  /* Every period cosiderate got an id in the problem to solve (ex: start = 20/05/2016, morning of 20/05/2016 got 0 as id etc....)
  * Already implemented without freezePeriods, freezes is the list of all freezePeriod between start and end
  */
  private HashMap<Integer, Periode> managePeriodBin(Date start, Date end, ArrayList<Date> freezes){
    Date current = start;
    while(current.compareTo(end) != 1){
      if(!freezes.contains(current) ){ // This condition is new
        mapPeriod.put(key1, new Period(key1, current));
        key1++;
      }
      current = current.plusHours(8);
    }
    return MapPeriod;
  }




  /*  Given a jobId, mapPref will be completed
  * prefList is the list of all preference between start and end ( precedently described)
  *
  */
  private void managePref(int jobId, HashMap<Integer, Period> mapPeriod, ArrayList<Preference> prefList, HashMap<Integer, int> mapPref){
    ArrayList<Integer> prefListTmp = new ArrayList<Integer>();
    for(Integer j: mapPeriod.keySet()){
      Period period = mapPeriod.get(j);

      for(Preference pref : prefList){
        if(DateUtils.isSameDay(period.getDate(), pref.getDate())){ //in JodaTime API
          prefListTmp.add(j);
        }
      }
    }

    mapPref.put(jobId, prefListTmp.toArray());
  }


  /* For information to show the prototype
  * This method belongs to Solver class
  *
  */
  public String solve(int n, int m, int cap, int[] pred, HashMap<Integer, int[]> mapPref){
    return null;
  }



}
