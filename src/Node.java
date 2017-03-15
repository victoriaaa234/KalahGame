import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Node 
{
	private int[] currentBoard;
	private int currentMove;
	private Map<Integer, int[]> possibleMoves;
	private ArrayList<Node> children;
	
	public Node(int[] stateBoard, int move) 
	{
		currentBoard = stateBoard;
		currentMove = move;
		children = new ArrayList<Node>();
		possibleMoves = new HashMap<Integer, int[]>();
	}
	
	public ArrayList<Node> getChildren() 
	{
		return children;
	}
	
	public Map<Integer, int[]> getPossibleMoves() 
	{
		return possibleMoves;
	}
	
	public int utility() 
	{
		int score = 0;
		if(possibleMoves.isEmpty()) 
		{
			// Terminal node
			int numberOfMoves = currentBoard[currentMove];
			if (currentMove + numberOfMoves == 13) 
			{
				score = score - 15;
				score = score - (numberOfMoves - 1);
			}
			else if (currentMove + numberOfMoves > 13) 
			{
				score = score - 10;
				score = score + ((currentMove + numberOfMoves) - 13);
				score = score - (13 - currentMove);
			}
			else 
			{
				score = score - numberOfMoves;
			}
		}
		return score;
	}
}
