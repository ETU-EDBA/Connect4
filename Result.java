public class Result{
  public String Winner;
  public long Time;
  public int Expected;

  public Result(){

  }
  public Result(String k , long l){
    this.Winner = k;
    this.Time = l;
    this.Expected=0;
  }

  public String toString(){
    return "winner : " + Winner + ", Time : " + Time ;
  }

}
