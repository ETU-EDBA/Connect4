import java.util.InputMismatchException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Textual view of the Connect 4 board with interaction from keyboard
 * @author Delos Chang
 *
 */

public class Connect4Graph  implements Connect4View {
	private JFrame frame;
	private JPanel panel;
	private JButton[] buttons;
	//private volatile boolean buttonActive=false;
	private volatile int buttonValue=-1;
	private JLabel[][] grid;

	public Connect4Graph(){
		super();
		frame = new JFrame("Connect4GUI");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Set up the content pane.
		GridLayout gridLay = new GridLayout(0,7, 15, 15);
		panel=new JPanel();
  	panel.setLayout(gridLay);
		grid=new JLabel[Connect4State.ROWS][];
		for (int row = Connect4State.ROWS - 1; row > -1; row--){
			grid[row]=new JLabel[Connect4State.COLS];
			for (int column = 0; column < Connect4State.COLS; column++){
				grid[row][column] = new JLabel(String.valueOf(""), SwingConstants.CENTER);
				grid[row][column].setBorder(BorderFactory.createLineBorder(Color.black));
				grid[row][column].setBackground(Color.WHITE);
				grid[row][column].setOpaque(true);

				panel.add(grid[row][column] , BorderLayout.CENTER);
			}
		}

		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(gridLay);
		buttons=new JButton[7];
		for(int i=0;i<7;i++){
			buttons[i]=new JButton(""+i);

			buttonPanel.add(buttons[i]);
		}
		frame.add(panel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		//Display the window.
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Displays the current board
	 * @param state current state of the game
	 */
	public void display(Connect4State state){
		char [][] board = state.getBoard();
		for (int row = Connect4State.ROWS - 1; row > -1; row--){
			for (int column = 0; column < Connect4State.COLS; column++){
				//grid[row][column].setText(String.valueOf(board[row][column]));
				if(board[row][column]=='X'){
					grid[row][column].setBackground(Color.blue);
					grid[row][column].setOpaque(true);
				}
				else if(board[row][column]=='O'){
					grid[row][column].setBackground(Color.red);
					grid[row][column].setOpaque(true);
				}
				else{
					grid[row][column].setBackground(Color.WHITE);
					grid[row][column].setOpaque(true);
				}
			}
		}
	}

	/**
	 * Asks the user for a move
	 * The move will be in the range 0 to Connect4State.COLS-1.
	 * @param state current state of the game
	 * @return the number of the move that player chose
	 */
	public int getUserMove(Connect4State state){
		for(int i=0;i<7;i++){
			final int buttonid=i;
			buttons[i].addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
					if(state.isValidMove(buttonid)){
						buttonValue=buttonid;
					}
			  }
			});
		}
		while(buttonValue==-1){
			try{
				Thread.sleep(200);
				//System.out.println("buttonvalue= "+buttonValue);
				if(buttonValue!=-1){
					break;
				}
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		int choice=buttonValue;
		buttonValue=-1;
		for(int i=0;i<7;i++){
			for( ActionListener al : buttons[i].getActionListeners() ) {
				 buttons[i].removeActionListener( al );
		 	}
		}
		return choice;

	}

	/**
	 * Reports the move that a player has made.
	 * The move should be in the range 0 to Connect4State.COLS-1.
	 * @param chosenMove the move to be reported
	 * @param name the player's name
	 */
	public void reportMove (int chosenMove, String name){
		System.out.println("\n" + name + " chooses the column " + chosenMove);
	}

	/**
	 * Ask the user the question and return the answer as an int
	 * @param question the question to ask
	 * @return The depth the player chose
	 */
	public int getIntAnswer (String question){


		boolean valid = false;
		int answer=0;
		while (!valid){
			String s = (String)JOptionPane.showInputDialog(
																			frame,
																			question,
																			null,
																			JOptionPane.PLAIN_MESSAGE,
																			null,
																			null,
																			"");

			try{
				answer = Integer.parseInt(s);
				valid=true;
			}
			catch(NumberFormatException e){
				reportToUser("Error: "+ e + " Please enter an integer");
			}
		}

		return answer;

	}

	/**
	 * Convey a message to user
	 * @param message the message to be reported
	 */
	public void reportToUser(String message){
		JOptionPane.showMessageDialog(frame,
    message);

	}

	/**
	 * Ask the question and return the answer
	 * @param question the question to ask
	 * @return the answer to the question
	 */
	public String getAnswer(String question){
		return (String)JOptionPane.showInputDialog(
										frame,
										question,
										null,
										JOptionPane.PLAIN_MESSAGE,
										null,
										null,
										"");

	}

}
