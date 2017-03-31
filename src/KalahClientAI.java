import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class KalahClientAI extends KalahClient
{
	protected String serverAddress;

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
	}

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
					// TODO(): We've connected and we're talking to server - we need to wait for info
					// TODO(): Ensure we don't get any random data between now and INFO message
					System.out.println("Waiting for other player to connect.");
				}
				else if (line.startsWith("INFO"))
				{
					// TODO(): Parse info and initialize the "kalahGame" object.
					// TODO(): Run all game operations through "kalahGame" so server/client stay in sync
					System.out.println("DEBUG -- Got info message.");
				}
				else if (line.startsWith("OK"))
				{
					// TODO(): Make sure you get ack
					// TODO(): A missing ack is probably an issue that warrants a "disconnected" message
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
					// TODO(): Opponent made a move, update the AI (override getOpponentMove)
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
