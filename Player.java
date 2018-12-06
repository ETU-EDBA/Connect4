public abstract class Player {
	private String playerName;
	public boolean canPrint = false;

	/**
	 * @param name Player ismi
	 */
	public Player (String name) {
		playerName = name;
	}

	/**
	 * @return player ismi
	 */
	public String getName() {
		return playerName;
	}

	/**
	 * Kullanicidan hamle istenir.
	 * @param state oyun durumu
	 * @param view oyun view'i
	 * @return yapilmasi istenilen hamle
	 */
	public abstract int getMove(Connect4State state, Connect4View view);
}
