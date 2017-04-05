/*Obsolete*/

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Computer
{
	private int[] board;
	Map<Integer, int[]> validMoves;
	Node minimaxTree;

	public Computer(Node minimax)
	{
		validMoves = new HashMap<Integer, int[]>();
		minimaxTree = minimax;
		board = minimax.getBoard();
		insertBoard(minimax);
	}

	public Node setBoard(int[] currentBoard, Node parent)
	{
		board = currentBoard;
		Node nextNode;
		for(int i = 0; i < parent.getChildren().size(); ++i)
		{
			if(parent.getChildren().get(i).getBoard() == currentBoard)
			{
				int[] selectBoard  = parent.getChildren().get(i).getBoard();
				int selectMove = parent.getChildren().get(i).getMove();
				nextNode = new Node(selectBoard, selectMove);
				insertBoard(nextNode);
				return nextNode;
			}
		}
		return new Node();
	}

	public Map<Integer, int[]> getAllValidMoves()
	{
		// Get all possible moves from current board state for AI
		// TODO
		return validMoves;
	}

	public void insertBoard(Node parent)
	{
		Map<Integer, int[]> moves = getAllValidMoves();
		ArrayList<Node> children = parent.getChildren();
		Iterator it = moves.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			int[] stateBoard = (int[]) pair.getValue();
			int move = (int) pair.getKey();
			Node node = new Node(stateBoard, move);
			children.add(node);
		}
		parent.setChildren(children);
	}

	public int min()
	{
		ArrayList<Integer> moveLocations = new ArrayList<Integer>();

		for (Integer i = 7; i < 13; ++i)
		{
			Node node = new Node(board, i);
			moveLocations.add(node.utility());
			int newI = i + 1;
			System.out.println("For: " + newI + " " + node.utility());
		}
		int minimum = moveLocations.indexOf(Collections.min(moveLocations));
		if (moveLocations.get(minimum) == 0)
		{
			for (int i = 0; i < moveLocations.size(); ++i)
			{
				if (moveLocations.get(i) > 0)
				{
					minimum = i;
					break;
				}
			}
		}
		minimum = minimum + 8;
		System.out.println("Computer chooses: " + minimum);
		moveLocations.clear();
		return minimum;
	}

	public int randomMoveSelect()
	{
		Random randGen = new Random();
		int randVal = randGen.nextInt(9) + 1;
		return randVal;
	}
}
