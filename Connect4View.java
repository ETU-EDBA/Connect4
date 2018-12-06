public interface Connect4View {

	/**
	 * Mevcut gridi goruntuler
	 * @param state oyun state'i
	 */
	public void display (Connect4State state);

	/**
	 * Kullanicidan hamle ister
	 * @param state oyun state'i
	 * @return kullanicinin sectigi hamleyi dondurur.
	 */
	public int getUserMove(Connect4State state);

	/**
	 * Kullanicinin yaptigi hamleyi report eder.
	 * @param chosenMove yapilan hamle
	 * @param name kullanici ismi
	 */
	public void reportMove (int chosenMove, String name);

	/**
	 * Kullaniciya soru sorar ve int tipinde cevap alir.
	 * @param question sorulacak soru
	 * @return int cevabi
	 */
	public int getIntAnswer (String question);

	/**
	 * Usera mesaj report eder
	 * @param message gonderilecek mesaj
	 */
	public void reportToUser(String message);

	/**
	 * Kullaniciya soru sorar ve string tipinde cevap alir.
	 * @param question sorulacak soru
	 * @return string cevabi
	 */
	public String getAnswer(String question);
}
