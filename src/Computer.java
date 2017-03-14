import java.util.Random;
import java.util.ArrayList;

public class Computer {
	private int[] board;
	ArrayList<Integer> validMoves;
	
	public Computer() {
		validMoves = new ArrayList<Integer>();
	}
	
	public void setBoard(int[] stateBoard) {
		board = stateBoard;
	}
	
	public int[] getBoard() {
		return board;
	}
	
	public ArrayList<Integer> getAllValidMoves() {
		// TODO(Victoria): Computer identifies all valid moves from board
		return validMoves;
	}
	
	public int getBestMove() {
		// TODO(Victoria): Implement this
		return 0;
	}
	
	public int randomMoveSelect() {
		Random randGen = new Random();
		int randVal = randGen.nextInt(9) + 1;
		return randVal;
	}
}
