import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.SwingConstants;

public class KalahClientAI extends KalahClient
{
	protected String serverAddress;
	protected boolean needsAck;
	protected boolean needsBegin;
	protected boolean needsInfo;
	protected long timeoutInMs;
	protected boolean turnAI;
	protected GameState gameAI;
	protected boolean madeFirstMove;
	boolean endGame;


	public static void main(String[] args) throws Exception
	{
		while (true)
		{
			KalahClientAI client;
			if (args.length > 0)
			{
				client = new KalahClientAI(args[0]);
			}
			else
			{
				client = new KalahClientAI("localhost");
			}
			client.run();
		}
    }

	public KalahClientAI(String serverAddress)
	{
		this.serverAddress = serverAddress;
		needsAck = false;
		needsBegin = false;
		needsInfo = false;
		turnAI = false;
		madeFirstMove = false;
	}
	
	protected void gotOpponentMove(String moves) {
		if(!turnAI) {
			super.gotOpponentMove(moves);
			for(GameState game : gameAI.next)
			{
				if(Minimax.parseTurnSequence(game.getTurnSequence()).equals(moves)) {
					gameAI = game;
					break;
				}
			}
		}
		turnAI = true;

		//Pie rule
//		if (!madeFirstMove)
//		{
//			gotPieMove() {
//			writeToServer("P");
//			turnAI = false;
//			needsAck = true;
//			kalahGame.swapSides();
//			madeFirstMove = true;
			// change endGame too
//		}
		
		if(gameAI.next.isEmpty() && !Minimax.getGameOverState()) {
			createTree(gameAI);
		}
		writeToServer(Minimax.parseTurnSequence(gameAI.getNextChoice().getTurnSequence()));
		gameAI = gameAI.getNextChoice();
		System.out.println("best move: " + Minimax.parseTurnSequence(gameAI.getTurnSequence()));
		turnAI = false;
	}
	
	public void createTree(GameState game)
	{
		Minimax.treeHelper(game, 6, endGame);
		Minimax.calcMinMax(game, endGame);
	}
	
	protected boolean gotInfoMessage(String message)
	{
		String[] infoMessageParts = message.split(" ");

		if (infoMessageParts[0].equals("INFO"))
		{
			try
			{
				int houseCount = Integer.parseInt(infoMessageParts[1]);
				int seedCount = Integer.parseInt(infoMessageParts[2]);
				timeoutInMs = Integer.parseInt(infoMessageParts[3]);
				String goesFirstStr = infoMessageParts[4];
				String standardLayoutStr = infoMessageParts[5];

				if (standardLayoutStr.equals("S"))
				{
					gameAI = new GameState(houseCount, seedCount);
					kalahGame = new KalahGame(houseCount, seedCount);
				}
				else if (standardLayoutStr.equals("R"))
				{
					int[] rawValues = new int[houseCount];
					for (int i = 0; i < houseCount; ++i)
					{
						rawValues[i] = Integer.parseInt(infoMessageParts[i + 6]);
					}
					kalahGame = new KalahGame(houseCount, rawValues);
					gameAI = new GameState(houseCount, rawValues);
				}
				else
				{
					System.out.println("Invalid info message from server.");
					System.out.println("Returning to the launch screen.");
					return false;
				}
				
				if (goesFirstStr.equals("F"))
				{
					turnAI = true;
					endGame = true;
					createTree(gameAI);
					System.out.println("DEBUG -- AI Turn");
				}
				else if (goesFirstStr.equals("S"))
				{
					turnAI = false;
					endGame = false;
					createTree(gameAI);
					System.out.println("DEBUG -- Not AI Turn");
				}
				else
				{
					System.out.println("Invalid info message from server.");
					System.out.println("Returning to the launch screen.");
					return false;
				}
			}
			catch (Exception e)
			{
				System.out.println("Invalid info message from server.");
				System.out.println("Returning to the launch screen.");
				return false;
			}
		}
		else
		{
			System.out.println("Invalid info message from server.");
			System.out.println("Returning to the launch screen.");
			return false;
		}

		writeToServer("READY");
		
		return true;
	}
	
//	public void getTurn() {
//		String turnSequence = game.getNextChoice().getTurnSequence();
//		String turn = Minimax.parseTurnSequence(turnSequence);
//	}
	
	@Override
	protected void run()
	{
		if (serverAddress != null)
		{
			try
			{
				super.connectToServer(serverAddress);
			}
			catch (ConnectException e)
			{
				// TODO(): Deal with connection failure
				System.out.println("DEBUG -- Connection failure.");
				System.out.println("Restarting the AI.");
				return;
			}
			catch (UnknownHostException e)
			{
				// TODO(): Deal with incorrect hostname, ie mistyped "localhost"
				System.out.println("DEBUG -- Invalid hostname.");
				System.out.println("Restarting the AI.");
				return;
			}
			catch (IOException e)
			{
				// TODO(): Deal with other various connection issues
				System.out.println("DEBUG -- Failed to initialize socket and connections.");
				System.out.println("Restarting the AI.");
				return;
			}

			System.out.println("Connecting to server...");

			while (true)
			{
				String line = "";
				try
				{
					line = readFromServer();
				}
				catch (IOException e)
				{
					// TODO(): Deal with lost connections
					System.out.println("DEBUG -- Lost connection to server.");
					System.out.println("Restarting the AI.");
					return;
				}

				if (line == null || line == "")
				{
					// TODO(): Deal with lost connections
					System.out.println("DEBUG -- Lost connection to server.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("BEGIN"))		
				{		
					if (needsAck)
					{
						System.out.println("Got begin instead of an ack.");
						System.out.println("Returning to launch screen.");
						return;
					}
					else if (needsInfo)
					{
						System.out.println("Got begin before got info.");
						System.out.println("Returning to launch screen.");
						return;
					}
					else if (needsBegin)
					{
						needsBegin = false;
					}
					else
					{
						System.out.println("Received duplicate begin.");
						System.out.println("Returning to launch screen.");
						return;
					}
					System.out.println("DEBUG -- Got begin message.");		
				}
				else if (line.startsWith("WELCOME"))
				{
					if (needsAck)
					{
						System.out.println("Got welcome instead of an ack.");
						System.out.println("Returning to launch screen.");
						return;
					}
					else
					{
						System.out.println("DEBUG -- Got welcome.");
						needsInfo = true;
						needsBegin = true;
					}
					System.out.println("Waiting for other player to connect.");
				}
				else if (line.startsWith("INFO"))
				{
					// TODO(): Run all game operations through "kalahGame" so server/client stay in sync
					if (needsAck)
					{
						System.out.println("Got new information instead of an ack.");
						System.out.println("Returning to launch screen.");
						return;
					}
					else if (needsInfo)
					{
						System.out.println("DEBUG -- Got info.");
						if (gotInfoMessage(line))
						{
							needsInfo = false;
						}
						else
						{
							System.out.println("Received an errant info packet.");
							System.out.println("Returning to launch screen.");
							return;
						}
					}
					else
					{
						System.out.println("Got new information after initialization.");
						System.out.println("Returning to launch screen.");
						return;
					}
					System.out.println("DEBUG -- Got info message.");
				}
				else if (line.startsWith("OK"))
				{
					// TODO(): A missing ack is probably an issue that warrants a "disconnected" message

					needsAck = false;
					System.out.println("DEBUG -- Got ack.");
				}
				else if (line.startsWith("WINNER"))
				{
					// TODO(): AI doesn't care if it won/lost/tied, just that the game is over
					// TODO(): We may want to merge or keep separate for custom printouts
					
					System.out.println("AI won the Kalah game.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("LOSER"))
				{
					// TODO(): AI doesn't care if it won/lost/tied, just that the game is over
					// TODO(): We may want to merge or keep separate for custom printouts
					System.out.println("AI lost the Kalah game.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("TIE"))
				{
					// TODO(): AI doesn't care if it won/lost/tied, just that the game is over
					// TODO(): We may want to merge or keep separate for custom printouts
					System.out.println("AI tied the Kalah game.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("ILLEGAL"))
				{
					// TODO(): This shouldn't ever happen, but find out why if it does
					// NOTE(): Our server only sends illegal on lose, doesn't follow up with "LOSE"
					System.out.println("AI made an illegal move and lost.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("TIMEOUT"))
				{
					// TODO(): Make sure to not run over time!
					System.out.println("AI ran out of time and lost.");
					System.out.println("Restarting the AI.");
					return;
				}
				else if (line.startsWith("P"))
				{
					// TODO(): Opponent made a pie move, it is now the AI's turn to move
					System.out.println("DEBUG -- Player chose pie rule.");
					gotPieMove();
				}
				else
				{
					System.out.println("DEBUG -- Got move: " + line);
					gotOpponentMove(line);
				}
			}
		}
		else
		{
			System.out.println("Invalid IP Address.");
			System.out.println("Quitting...");
			System.exit(0);
		}
	}
}
