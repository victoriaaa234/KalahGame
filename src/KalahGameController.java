import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class KalahGameController
{
	private int[] kalahBoard;
	private Boolean isGameOver;
	private Computer AI;
	
	public KalahGameController()
	{		
		kalahBoard = new int[14];
		
		kalahBoard[0] = 4;
		kalahBoard[1] = 4;
		kalahBoard[2] = 4;
		kalahBoard[3] = 4;
		kalahBoard[4] = 4;
		kalahBoard[5] = 4;
		
		kalahBoard[6] = 0;
		
		kalahBoard[7] = 4;
		kalahBoard[8] = 4;
		kalahBoard[9] = 4;
		kalahBoard[10] = 4;
		kalahBoard[11] = 4;
		kalahBoard[12] = 4;
		
		kalahBoard[13] = 0;
		
		isGameOver = false;
		AI = new Computer();

	}
	
	// If human wins, return 0. If computer wins, return 1. If a tie, return 2.
	private int execCalcWinner()
	{
		int winnerIdx;
		
		for (int i = 0; i < 6; ++i)
		{
			kalahBoard[6] += kalahBoard[i];
			kalahBoard[i] = 0;
		}
		for (int i = 7; i < 13; ++i)
		{
			kalahBoard[13] += kalahBoard[i];
			kalahBoard[i] = 0;
		}
		
		if (kalahBoard[6] > kalahBoard[13])
		{
			winnerIdx = 0;
		}
		else if (kalahBoard[13] > kalahBoard[6])
		{
			winnerIdx = 1;
		}
		else
		{
			winnerIdx = 2;
		}
		
		printKalahBoard();
		
		return winnerIdx;
	}
	
	private void execCheckIfGameOver()
	{
		Boolean gameOver = true;
		
		for (int i = 0; i < 6; ++i)
		{
			if (kalahBoard[i] > 0)
			{
				gameOver = false;
				break;
			}
		}
		
		if (!gameOver)
		{
			for (int i = 7; i < 13; ++i)
			{
				if (kalahBoard[i] > 0)
				{
					gameOver = false;
					break;
				}
			}
		}
		
		isGameOver = gameOver;
	}
	
	// Returns true if player goes first
	// Returns false if computer goes first
	private Boolean execFindFirstPlayer(Scanner scanner, int computerChoice)
	{
		Random randGen = new Random();
		int randVal = randGen.nextInt(9) + 1;
		
		Boolean result;

		System.out.print("Give me an answer between 1 and 10: ");
		int userChoice = scanner.nextInt();
		
		while ((userChoice < 1) || (userChoice > 10))
		{
			System.out.println("Only numbers between 1 and 10 are accepted.  Please try again.");
			System.out.println("Give me an answer between 1 and 10: ");
			userChoice = scanner.nextInt();
		}
		
		System.out.println("The opponent chose the number: " + computerChoice);
		System.out.println("The random number was: " + randVal);
		
		if ((computerChoice > randVal) && (userChoice > randVal))
		{
			System.out.print("Both players went over. Therefore ");
			if ((computerChoice - randVal) < (userChoice - randVal))
			{
				System.out.println(" your opponent gets the first turn for being closest.");
				result = false;
			}
			else
			{
				System.out.print(" you get the first for being closest.");
				result = true;
			}
		}
		else if (computerChoice > randVal)
		{
			System.out.println("The computer went over the value. Therefore you get the first turn.");
			result = true;
		}
		else if (userChoice > randVal)
		{
			System.out.println("You went over the value. Therefore your opponent gets the first turn.");
			result = false;
		}
		else
		{
			if ((randVal - computerChoice) < (randVal - userChoice))
			{
				System.out.println("Your opponent was closest. Therefore your opponent gets the first turn.");
				result = false;
			}
			else
			{
				System.out.println("You were the closest. Therefore you get the first turn");
				result = true;
			}
		}
		
		return result;
	}
	
	// Returns board index of play
	private int execGetHumanPlay(Scanner scanner)
	{
		int userInput;
		Boolean validMove = false;
		
		do
		{
			System.out.println("Enter the house number you would like to move from.");
			System.out.print("Houses are numbered from left to right, 1-6: ");
			
			userInput = scanner.nextInt();
			
			while ((userInput < 1) || (userInput > 6))
			{
				System.out.println("Only numbers between 1 and 6 are accepted. Please try again.");
				System.out.println("Enter the house number you would like to move from.");
				System.out.print("Houses are numbered from left to right, 1-6: ");
				userInput = scanner.nextInt();
			}
			
			if (kalahBoard[userInput - 1] > 0)
			{
				validMove = true;
			}
			else
			{
				System.out.println("The selected house is empty. You must select a house that has seeds in it.");
				validMove = false;
			}
		} while (!validMove);
		
		return userInput - 1;
	}
	
	private int execGetComputerPlay()
	{
		// Computer needs current state of the board to decide next move
		AI.setBoard(kalahBoard);
		
		// TODO(Drew): Should we check the validity of the computer move?
		
		// TODO(Drew): Implement this
		
		return AI.getBestMove();
	}
	
	private void execGameOver(Scanner scanner, int winnerIdx)
	{
		switch (winnerIdx)
		{
			case 0:
			{
				printWinScreen();
			} break;
			case 1:
			{
				printLoseScreen();
			} break;
			case 2:
			{
				printTieScreen();
			}
		}
		scanner.nextLine(); // NOTE(Drew): Hack for capturing newline after nextInt
		scanner.nextLine();
	}
	
	private void execLaunchScreen(Scanner scanner)
	{
		System.out.println("CSCE 315 - Team 25");
		System.out.println("Press enter to begin Kalah");
		scanner.nextLine();
	}
	
	private Boolean execMakePlay(int moveLocation, String user) throws IOException
	{
		Boolean bonusTurn = false;
		
		if (kalahBoard[moveLocation] > 0)
		{
			int startIdx = moveLocation + 1;
			int finishIdx = startIdx;

			while (kalahBoard[moveLocation] > 0)
			{
				finishIdx = startIdx;
				if(user == "Human" && finishIdx == 13 || user == "Computer" && finishIdx == 6) {
					startIdx = 0;
					continue;
				}
				kalahBoard[moveLocation]--;
				kalahBoard[startIdx]++;
				startIdx++;
			}
			
			if ((finishIdx == 6 && moveLocation < 6) || (finishIdx == 13 && moveLocation > 6))
			{
				System.out.println("Player earned a second turn!");
				bonusTurn = true;
			}
		}
		else
		{
			System.out.println("Invalid move given to KalahGameController.makePlay");
			throw new IOException();
		}
		
		return bonusTurn;
	}
	
	// Computer houses from left to right from computer perspective
	public int[] getHousesComputer()
	{
		int[] computerHouses = new int[6];
		
		computerHouses[0] = kalahBoard[7];
		computerHouses[1] = kalahBoard[8];
		computerHouses[2] = kalahBoard[9];
		computerHouses[3] = kalahBoard[10];
		computerHouses[4] = kalahBoard[11];
		computerHouses[5] = kalahBoard[12];
		
		return computerHouses;
	}
	
	// Human houses from left to right from player perspective
	public int[] getHousesHuman()
	{
		int[] humanHouses = new int[6];
		
		humanHouses[0] = kalahBoard[0];
		humanHouses[1] = kalahBoard[1];
		humanHouses[2] = kalahBoard[2];
		humanHouses[3] = kalahBoard[3];
		humanHouses[4] = kalahBoard[4];
		humanHouses[5] = kalahBoard[5];
		
		return humanHouses;
	}
	
	public int[] getKalahBoard()
	{
		return kalahBoard;
	}
	
	public int getNumSeedsInComputerEnd()
	{
		int result = kalahBoard[13];
		
		return result;
	}
	
	public int getNumSeedsInHumanEnd()
	{
		int result = kalahBoard[6];
		
		return result;
	}
	
	public void printKalahBoard()
	{
		System.out.println("\t\t\tOpponent");
		System.out.println("\t" + kalahBoard[12] + "\t" + kalahBoard[11] + "\t" + kalahBoard[10] + "\t" + kalahBoard[9] + "\t" + kalahBoard[8] + "\t" + kalahBoard[7]);
		System.out.println(kalahBoard[13] + "\t\t\t\t\t\t\t" + kalahBoard[6]);
		System.out.println("\t" + kalahBoard[0] + "\t" + kalahBoard[1] + "\t" + kalahBoard[2] + "\t" + kalahBoard[3] + "\t" + kalahBoard[4] + "\t" + kalahBoard[5]);
		System.out.println("\t\t\t Player");
	}
	
	public void printTieScreen()
	{
		System.out.print("You tied. Press enter to return to the launch screen.");
	}
	
	public void printLoseScreen()
	{
		System.out.print("You lose. Press enter to return to the launch screen.");
	}
	
	public void printWinScreen()
	{
		System.out.print("You won. Press enter to return to the launch screen.");
	}
	
	public void runGame() throws IOException
	{
		Scanner scanner = new Scanner(System.in);
		
		execLaunchScreen(scanner);
		
		Boolean humanGoesFirst = execFindFirstPlayer(scanner, AI.randomMoveSelect());
		Boolean gameRunning = true;
		Boolean humanTurn = false;
		
		if (humanGoesFirst)
		{
			humanTurn = true;
		}
		
		while (gameRunning)
		{
			Boolean bonusTurn = false;
			if (humanTurn)
			{
				do
				{
					printKalahBoard();
					
					int moveLocation = execGetHumanPlay(scanner);
					bonusTurn = execMakePlay(moveLocation, "Human");
				} while (bonusTurn);
				humanTurn = false;
			}
			else
			{
				do
				{
					printKalahBoard();
					
					// TODO(Drew): Get move from the AI
					int moveLocation = execGetComputerPlay();
					// bonusTurn = execMakePlay(moveLocation, "Computer");
					// NOTE(Drew): Remove this!
					System.out.println("The computer skips its turn.");
				} while (bonusTurn);
				humanTurn = true;
			}
			
			execCheckIfGameOver();
			if (isGameOver)
			{
				int winnerIdx = execCalcWinner();
				execGameOver(scanner, winnerIdx);
				gameRunning = false;
			}
		}
		
		scanner.close();
	}
}
