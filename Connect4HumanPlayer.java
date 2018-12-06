public class Connect4HumanPlayer extends Player {

	/**
	 * Verilen isimli human player olusturur
	 * @param name player ismi
	 */
	public Connect4HumanPlayer(String name ) {
		super(name);
	}

	@Override
	public int getMove(Connect4State state, Connect4View view) {
		return view.getUserMove(state);
	}

}
