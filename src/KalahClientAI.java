import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class KalahClientAI extends KalahClient
{
	protected String serverAddress;
	protected boolean needsAck;
	protected boolean needsBegin;
	protected boolean needsInfo;
	protected long timeoutInMs;
	protected boolean turnAI;
	
	public static GameState gameAI;
	public static GameState currentAI;
	protected boolean madeFirstMove;
	private boolean endGame;
	//For pie rule
	public static GameState opponentMove;
	public static GameState opponentPreviousState;

    public void setNewGameState(GameState game)
    {
        gameAI = new GameState(game);
    }
    public void setOpponentMoveState(GameState game) {
    	opponentMove = new GameState(game);
    }
    public void setOpponentPreviousState(GameState game) {
    	opponentPreviousState = new GameState(game);
    }
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
			if(gameAI.next.isEmpty()) {
				System.out.println("EMPTY");
			}
			else {
				for(GameState game : gameAI.next)
				{
					System.out.println("w"+moves+"y");
					System.out.println("z"+Minimax.parseTurnSequence(game.getTurnSequence()) + "y");
					if(Minimax.parseTurnSequence(game.getTurnSequence()).equals(moves)) {
						if(!madeFirstMove) {
							setOpponentMoveState(game);
							setOpponentPreviousState(gameAI.getNextChoice());
						}
						gameAI = game;
						break;
					}
				}
			}
		}
		turnAI = true;
		
		AIMove();
		turnAI = false;
	}
	
	public void firstMoveAI() {
		int choice;
		if(gameAI.next.isEmpty()) {
			createTree();
		}
		
		ArrayList<Integer> value = new ArrayList<Integer>();
		for(GameState g : gameAI.next) {
			value.add(g.getV());
		}
		Collections.sort(value);
		if(value.size() % 2 == 0) {
			//even number of possible moves
			//AI gets the value that is less optimal than average so that if
			//player chooses pie rule, they will be at less optimal state
			choice = value.get((value.size()/2) - 1);
		}
		else {
			//odd number of possible moves
			//AI chooses the average
			choice = value.get(value.size()/2);
		}
		for(GameState g : gameAI.next) {
			//first occurrence of this move
			if(g.getV() == choice) {
				writeToServer(Minimax.parseTurnSequence(g.getTurnSequence()));
				gameAI = g;
				break;
			}
		}
	}
	
	public void AIMove() {
		
		if(gameAI.next.isEmpty() && !Minimax.getGameOverState()) {
			System.out.println("DEBUG: Creating new tree.");
			createTree();
		}

		//Pie rule
//		if (!madeFirstMove && endGame)
//		{
//			if(Minimax.pieRuleSteal(opponentMove, opponentPreviousState, !endGame)) {
//				turnAI = false;
//				needsAck = true;
//				madeFirstMove = true;
//				endGame = !endGame;
//				writeToServer("P");
//			}
//		}
		//else {
			//System.out.println("all possible moves");
			//int iter = 0;
//			for(GameState g : gameAI.next) {
//				System.out.println(iter + Minimax.parseTurnSequence(g.getTurnSequence()));
//				System.out.println(iter+ " get score: " + g.getV());
//				iter++;
//			}
			writeToServer(Minimax.parseTurnSequence(gameAI.getNextChoice().getTurnSequence()));
			setNewGameState(gameAI.getNextChoice());
//			System.out.println("best move: " + Minimax.parseTurnSequence(gameAI.getTurnSequence()));
//			System.out.println("whole best move: " + gameAI.getTurnSequence());
		//}
	}
	
	public void createTree()
	{
		Minimax.treeHelper(gameAI, 6, endGame);
		Minimax.calcMinMax(gameAI, endGame);
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
					System.out.println("once");
					System.out.println("Invalid info message from server.");
					System.out.println("Returning to the launch screen.");
					return false;
				}
				
				if (goesFirstStr.equals("F"))
				{
					turnAI = true;
					endGame = true;
					createTree();
					System.out.println("DEBUG -- AI Turn");
				}
				else if (goesFirstStr.equals("S"))
				{
					turnAI = false;
					endGame = true;
					createTree();
					System.out.println("DEBUG -- Not AI Turn");
				}
				else
				{
					System.out.println("twice");
					System.out.println("Invalid info message from server.");
					System.out.println("Returning to the launch screen.");
					return false;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Invalid info message from server.");
				System.out.println("Returning to the launch screen.");
				return false;
			}
		}
		else
		{
			System.out.println("trice");
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
					if(turnAI) {
						//firstMoveAI();
						AIMove();
						madeFirstMove = true;
						turnAI = false;
					}
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
					else if (needsInfo && needsBegin)
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
					endGame = !endGame;
					
					turnAI = true;
					AIMove();
					turnAI = false;
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
