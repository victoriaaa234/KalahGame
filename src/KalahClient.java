import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class KalahClient
{
	protected KalahGame kalahGame;

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;

	protected void connectToServer(String serverAddress) throws UnknownHostException, ConnectException, IOException
	{
		socket = new Socket(serverAddress, 9090);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
	}

	protected void gotOpponentMove(String moves)
	{
		writeToServer("OK");

		String[] indexes = moves.split(" ");

		for (int i = 0; i < indexes.length; ++i)
		{
			kalahGame.executeMove(Integer.parseInt(indexes[i]), 1);
		}
	}

	protected void gotPieMove()
	{
		writeToServer("OK");
		kalahGame.swapSides();
	}

	protected boolean gotPlayerMove(int move)
	{
		int result = kalahGame.executeMove(move, 0);
		boolean bonusMove = (result == 1);

		return bonusMove;
	}

	protected String readFromServer() throws IOException
	{
		String line = input.readLine();
		System.out.println("DEBUG -- Read from server: " + line);
		return line;
	}

	abstract protected void run();

	protected void writeToServer(String line)
	{
		System.out.println("DEBUG -- Writing to server: " + line);
		output.println(line);
	}
}
