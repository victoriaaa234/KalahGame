import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Computer
{
	private int[] board;
	ArrayList<Integer> validMoves;
	ArrayList<Integer> moveLocations;
	
	public Computer()
	{
		validMoves = new ArrayList<Integer>();
	}
	
	public void setBoard(int[] currentBoard) {
		board = currentBoard;
	}
	
	public ArrayList<Integer> getAllValidMoves()
	{
		// TODO(Victoria): Computer identifies all valid moves from board
		return validMoves;
	}
	
	public int getBestMove()
	{
		for (Integer i = 7; i < 13; ++i) 
		{
			Node node = new Node(board, i);
			validMoves.add(node.utility());
			int newI = i + 1;
			System.out.println("For: " + newI + " " + node.utility());
		}
		int minimum = validMoves.indexOf(Collections.min(validMoves));
		if (validMoves.get(minimum) == 0) 
		{
			for (int i = 0; i < validMoves.size(); ++i) 
			{
				if (validMoves.get(i) > 0) 
				{
					minimum = i;
					break;
				}
			}
		}
		minimum = minimum + 8;
		System.out.println("Computer chooses: " + minimum);
		validMoves.clear();
		return minimum;
	}
	
	public int randomMoveSelect()
	{
		Random randGen = new Random();
		int randVal = randGen.nextInt(9) + 1;
		return randVal;
	}
}
