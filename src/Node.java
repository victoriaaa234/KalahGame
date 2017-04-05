/*Obsolete*/

import java.util.ArrayList;

public class Node
{
	private int[] currentBoard;
	private int currentMove;
	private ArrayList<Node> children;

	public Node()
	{
		children = new ArrayList<Node>();
	}

	public Node(int[] stateBoard, int move)
	{
		currentBoard = stateBoard;
		currentMove = move;
		children = new ArrayList<Node>();
	}

	public int[] getBoard()
	{
		return currentBoard;
	}

	public int getMove()
	{
		return currentMove;
	}

	public ArrayList<Node> getChildren()
	{
		return children;
	}

	public void setChildren(ArrayList<Node> child) {
		children = child;
	}

	public int utility()
	{
		int score = 0;
		if(children.isEmpty())
		{
			// Terminal node
			int numberOfMoves = currentBoard[currentMove];
			int iteratorMove = currentMove;
			Boolean steal = false;
			int stealSeeds = 0;
			int iteratorMoves = numberOfMoves;
			while(iteratorMoves != 0)
			{
				int nextMove = ++iteratorMove;
				if(nextMove == 13 && ((currentMove + numberOfMoves)%13 != 0))
				{
					score = score - 7;
					iteratorMove = -1;
				}
				else if(nextMove == 6)
				{
					iteratorMove = 6;
					continue;
				}
				else if(nextMove > 6 && nextMove < 13)
				{
					score--;
					if(iteratorMoves == 1 && currentBoard[nextMove] == 0)
					{
						steal = true;
						stealSeeds = currentBoard[13 - (nextMove + 1)];
						if(stealSeeds != 0)
						{
							stealSeeds ++;
						}
					}
				}
				else if(nextMove < 6)
				{
					score++;
				}
				iteratorMoves--;
			}
			if(((currentMove + numberOfMoves)%13 == 0))
			{
				score = score - 15;
			}
			else if(steal && stealSeeds != 0)
			{
				score = score - 10;
			}
		}
		return score;
	}
}
