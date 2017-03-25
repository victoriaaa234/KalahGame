import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class KalahGameServer
{
	public static void main(String[] args) throws IOException
	{
		int numHouses = 6;
		int numSeedsPerHouse = 4;
		long timeoutInMs = 30000;
		boolean randomizeLayout = false;

		System.out.println("Kalah server is running.");

		if (args.length == 0)
		{
			System.out.println("Kalah server is running with default settings.");
			System.out.println("You can specify a settings file as an arugment to this program.");
			System.out.println("Settings are in the format:");
			System.out.println("<int for holes per side> <int for seeds per hole> <long int for timeout> <S | R>");
		}
		else
		{
			try
			{
				File file = new File(args[0]);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String settingsLine = bufferedReader.readLine();
				String[] settings = settingsLine.split(" ");
				if (settings.length >= 4)
				{
					numHouses = Integer.parseInt(settings[0]);
					numSeedsPerHouse = Integer.parseInt(settings[1]);
					timeoutInMs = Integer.parseInt(settings[2]);

					if (numHouses < 4 || numHouses > 9)
					{
						System.out.println("The number of holes per side must be between 4 and 9, inclusive.");
						System.out.println("Closing...");
						System.exit(1);
					}

					if (numSeedsPerHouse < 1 || numSeedsPerHouse > 10)
					{
						System.out.println("The number of seeds per hole must be between 1 and 10, inclusive.");
						System.out.println("Closing...");
						System.exit(1);
					}

					if (timeoutInMs < 1)
					{
						System.out.println("Timeout must be a positive real number.");
						System.out.println("Closing...");
						System.exit(1);
					}

					if (settings[3].equals("R"))
					{
						randomizeLayout = true;
					}
					// NOTE(Drew): Otherwise do standard. Even if character is invalid.
				}
				else
				{
					System.out.println("Settings file was improperly formatted.");
					System.out.println("Closing...");
					System.exit(1);
				}
				bufferedReader.close();
			}
			catch (Exception e)
			{
				System.out.println("Encountered issue reading from save file.");
				System.out.println("Closing...");
				System.exit(1);
			}

			System.out.println("Kalah server is running using specified settings file.");
		}

		System.out.println("Current settings:");
		System.out.println("\tNumber of houses per side: " + numHouses);
		System.out.println("\tNumber of seeds per house: " + numSeedsPerHouse);
		System.out.println("\tTimeout per turn in ms: " + timeoutInMs);
		System.out.println("\tRandomize seed layout: " + randomizeLayout);

		while (true)
		{
			ServerSocket listener = new ServerSocket(9090);

			System.out.println("Kalah Server is listening on port 9090.");

			try
			{
				while (true)
				{
					KalahGameServerLogic game = new KalahGameServerLogic(numHouses, numSeedsPerHouse, randomizeLayout);
					KalahGameServerLogic.KalahGamePlayer player0 = game.new KalahGamePlayer(listener.accept(), numHouses, numSeedsPerHouse, timeoutInMs, true, game.getNumSeedsPerHouseStr());
					KalahGameServerLogic.KalahGamePlayer player1 = game.new KalahGamePlayer(listener.accept(), numHouses, numSeedsPerHouse, timeoutInMs, false, game.getNumSeedsPerHouseStr());
					game.setCurrentPlayer(player0);
					game.setFirstPlayer(player0);
					player0.setOpponent(player1);
					player1.setOpponent(player0);
					player0.start();
					player1.start();
				}
			}
			finally
			{
				listener.close();
			}
		}
	}
}





class KalahGameServerLogic
{
	private KalahGamePlayer currentPlayer;
	private KalahGamePlayer firstPlayer;

	private KalahGame kalahGame;

	private int[] numSeedsPerHouse;

	public KalahGameServerLogic()
	{
		kalahGame = new KalahGame(6, 4);
	}

	public KalahGameServerLogic(int numHouses, int numSeeds)
	{
		kalahGame = new KalahGame(numHouses, numSeeds);
	}

	public KalahGameServerLogic(int numHouses, int numSeeds, boolean randomize)
	{
		if (!randomize)
		{
			numSeedsPerHouse = new int[numHouses];

			for (int i = 0; i < numHouses; ++i)
			{
				numSeedsPerHouse[i] = numSeeds;
			}

			kalahGame = new KalahGame(numHouses, numSeeds);
		}
		else
		{
			numSeedsPerHouse = randomizeSeeds(numHouses, numSeeds);
			kalahGame = new KalahGame(numHouses, numSeedsPerHouse);
		}
	}

	public void forceLoser(KalahGamePlayer loser)
	{
		int playerIdx = (loser == firstPlayer) ? 0 : 1;
		kalahGame.forceLoser(playerIdx);
	}

	public KalahGamePlayer getCurrentPlayer()
	{
		return currentPlayer;
	}

	public KalahGamePlayer getFirstPlayer()
	{
		return firstPlayer;
	}

	public String getNumSeedsPerHouseStr()
	{

		String result = "";

		if (numSeedsPerHouse.length > 0)
		{
			result += numSeedsPerHouse[0];

			for (int i = 1; i < numSeedsPerHouse.length; ++i)
			{
				result += " " + numSeedsPerHouse[i];
			}
		}

		return result;
	}

	public boolean isPlayerLoser(KalahGamePlayer player)
	{
		boolean result = false;
		int playerIdx = (player == firstPlayer) ? 0 : 1;

		if (kalahGame.isPlayerLosing(playerIdx))
		{
			result = true;
		}

		return result;
	}

	public boolean isPlayerTied()
	{
		boolean result = false;

		if (kalahGame.isTied())
		{
			result = true;
		}

		return result;
	}

	public boolean isPlayerWinner(KalahGamePlayer player)
	{
		boolean result = false;
		int playerIdx = (player == firstPlayer) ? 0 : 1;

		if (kalahGame.isPlayerWinning(playerIdx))
		{
			result = true;
		}

		return result;
	}

	public int parseMove(KalahGamePlayer player, String move)
	{
		if (move != null)
		{
			int result = Integer.MAX_VALUE;
			int playerIdx = (player == firstPlayer) ? 0 : 1;
			String[] moves = move.split(" ");
			if (moves.length > 0)
			{
				for (int i = 0; i < moves.length; ++i)
				{
					result = kalahGame.executeMove(Integer.parseInt(moves[i]), playerIdx);
				}
			}
			else
			{
				System.out.println("Got an invalid move from a player.");
				System.out.println("Closing...");
				System.exit(1);
			}

			currentPlayer = player.opponent;
			currentPlayer.otherPlayerMoved(move);

			return result;
		}
		else
		{
			System.out.println("Got a null move from a player.");
			System.out.println("Closing...");
			System.exit(1);
			return -1;
		}
	}

	public void setCurrentPlayer(KalahGamePlayer currentPlayer)
	{
		this.currentPlayer = currentPlayer;
	}

	public void setFirstPlayer(KalahGamePlayer firstPlayer)
	{
		this.firstPlayer = firstPlayer;
	}

	private int[] randomizeSeeds(int numHouses, int numSeeds)
	{
		Random random = new Random();
		int[] randomMarkers = new int[numHouses + 1];
		int[] seedsPerHouse = new int[numHouses];
		int maxVal = numHouses * numSeeds;

		for (int i = 2; i < randomMarkers.length; ++i)
		{
			randomMarkers[i] = random.nextInt(maxVal - 1) + 1;
		}
		randomMarkers[0] = 0;
		randomMarkers[1] = maxVal;

		Arrays.sort(randomMarkers);

		for (int i = 0; i < numHouses; ++i)
		{
			seedsPerHouse[i] = randomMarkers[i + 1] - randomMarkers[i];
		}

		return seedsPerHouse;
	}




	class KalahGamePlayer extends Thread
	{
		private Socket socket;
		private BufferedReader input;
		private PrintWriter output;

		private KalahGamePlayer opponent;

		private String infoString;
		private boolean needsAck;
		private long numMsPerMove;

		TimerTask timeoutTask;
		Timer timer;

		public KalahGamePlayer(Socket socket, int numHouses, int numSeeds, long numMsPerMove, boolean goesFirst)
		{
			this.socket = socket;
			this.numMsPerMove = numMsPerMove;

			try
			{
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);

				writeToClient("WELCOME");

				char goesFirstChar = goesFirst == true ? 'F' : 'S';

				infoString = "INFO " + numHouses + " " + numSeeds + " " + numMsPerMove + " " + goesFirstChar + " S";
			}
			catch (IOException e)
			{
				System.out.println("Couldn't communicate with client successfully.");
				System.out.println("Closing...");
				System.exit(1);
			}
		}

		public KalahGamePlayer(Socket socket, int numHouses, int numSeeds, long numMsPerMove, boolean goesFirst, String randomValuesAsStr)
		{
			this.socket = socket;
			this.numMsPerMove = numMsPerMove;

			try
			{
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);

				writeToClient("WELCOME");

				char goesFirstChar = goesFirst == true ? 'F' : 'S';

				infoString = "INFO " + numHouses + " " + numSeeds + " " + numMsPerMove + " " + goesFirstChar + " R " + randomValuesAsStr;
			}
			catch (IOException e)
			{
				System.out.println("Couldn't communicate with client successfully.");
				System.out.println("Closing...");
				System.exit(1);
			}
		}

		public KalahGamePlayer getOpponent()
		{
			return opponent;
		}

		public void otherPlayerMoved(String location)
		{
			writeToClient(location);
			needsAck = true;

			if (kalahGame.hasWinner())
			{
				if (isPlayerWinner(this))
				{
					writeToClient("WINNER");
					opponent.writeToClient("LOSER");
				}
				else if (isPlayerLoser(this))
				{
					writeToClient("LOSER");
					opponent.writeToClient("WINNER");
				}
				else if (isPlayerTied())
				{
					writeToClient("TIE");
					opponent.writeToClient("TIE");
				}
			}
		}

		private String readFromClient() throws IOException
		{
			String result = input.readLine();
			System.out.println("DEBUG -- Read from client: " + result);
			return result;
		}

		public void run()
		{
			try
			{
				writeToClient(infoString);

				String clientAck = readFromClient();
				if (!clientAck.equals("READY"))
				{
					System.out.println("Couldn't communicate initialization info with client successfully.");
					System.out.println("Closing...");
					System.exit(1);
				}

				while (true)
				{
					if (this == currentPlayer)
					{
						this.timer = new Timer();
						this.timeoutTask = new TimerTask()
						{
							public void run()
							{
								writeToClient("TIMEOUT");
								opponent.writeToClient("WINNER");
							}
						};
						timer.schedule(timeoutTask, numMsPerMove);
					}

					String line = readFromClient();

					if (line.startsWith("OK"))
					{
						if (timeoutTask != null && timer != null)
						{
							timeoutTask.cancel();
							timer.cancel();
						}

						if (needsAck)
						{
							needsAck = false;
						}
						else
						{
							System.out.println("Received errant ack.");
							System.out.println("Quitting...");
							System.exit(1);
						}
					}
					else if (line.startsWith("READY"))
					{
						if (timeoutTask != null && timer != null)
						{
							timeoutTask.cancel();
							timer.cancel();
						}

						System.out.println("Received errant ready.");
						System.out.println("Quitting...");
						System.exit(1);
					}
					else if (line.startsWith("P"))
					{
						if (timeoutTask != null && timer != null)
						{
							timeoutTask.cancel();
							timer.cancel();
						}

						kalahGame.swapSides();
						writeToClient("OK");
						opponent.writeToClient("P");
						opponent.needsAck = true;
					}
					else
					{
						if (timeoutTask != null && timer != null)
						{
							timeoutTask.cancel();
							timer.cancel();
						}

						if (!needsAck)
						{
							int result = parseMove(this, line);

							if (result > 0)
							{
								System.out.println("Got an invalid move.");
								System.out.println("Assigning winners and losers.");

								forceLoser(this);
								writeToClient("ILLEGAL");
								opponent.writeToClient("WINNER");
							}
						}
						else
						{
							System.out.println("Received command when needed an ack.");
							System.out.println("Quitting...");
							System.exit(1);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Closing...");
				System.exit(1);
			}
		}

		public void setOpponent(KalahGamePlayer opponent)
		{
			this.opponent = opponent;
		}

		private void writeToClient(String message)
		{
			System.out.println("DEBUG -- Write to client: " + message);
			output.println(message);
		}
	}
}
