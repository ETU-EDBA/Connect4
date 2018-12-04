public class Result{
  public String Winner;
  public long Time;
  public String Expected;

  public Result(){

  }
  public Result(String k , long l){
    this.Winner = k;
    this.Time = l;
    this.Expected="";
  }

  public String toString(){
    return "winner : " + Winner + ", Time : " + Time ;
  }

}
