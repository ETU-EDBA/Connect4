public class Result{
  public String Winner;
  public long Time;

  public Result(String k , long l){
    this.Winner = k;
    this.Time = l;
  }

  public String toString(){
    return "winner : " + Winner + "Time : " + Time ;  
  }

}
