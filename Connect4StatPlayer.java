public class Connect4StatPlayer extends Player {

	/**
	 * Stat player olusturur, stat player verilmis plays string degerini tahta uzerinde oynatir.
	 * @param name player ismi
	 * @param plays plays listesi, or liste: 2234561 
	 */
	 String plays;
	 int counter;
	public Connect4StatPlayer(String name, String plays) {
		super(name);
		this.plays=plays;
		counter=0;
	}

	@Override
	public int getMove(Connect4State state, Connect4View view) {
		if(plays.length()<=counter){
			return -1;
		}
		else{
			int move=Character.getNumericValue(plays.charAt(counter))-1;
			counter++;
			return move;
		}
	}

}
