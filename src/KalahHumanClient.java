import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class KalahHumanClient
{
	private KalahGame kalahGame;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private JFrame frame;
	private ArrayList<KalahPitButton> playerSideHouses;
	private ArrayList<KalahPitButton> opponentSideHouses;
	private KalahPitButton playerEndZone;
	private KalahPitButton opponentEndZone;
	
	private boolean needsAck;
	private boolean needsInfo;
	private boolean madeFirstMove;
	private String moveString;
	private boolean playerTurn;
	
	public static void main(String[] args) throws Exception
	{
		KalahHumanClient client = new KalahHumanClient();
		client.run();
    }
	
	public KalahHumanClient()
	{
		playerSideHouses = new ArrayList<KalahPitButton>();
		opponentSideHouses = new ArrayList<KalahPitButton>();
		
		frame = new JFrame("Kalah User Interface - Team 23");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setSize(900, 400);
		
		drawStartScreen();
		
		frame.setVisible(true);
		
		needsAck = false;
		needsInfo = false;
		playerTurn = false;
		madeFirstMove = false;
		
		moveString = "";
	}
	
	private void drawConnectingScreen()
	{
		frame.getContentPane().removeAll();
		
		JLabel connectingLabel = new JLabel("Connecting to server...", JLabel.CENTER);
		connectingLabel.setBounds(0, 100, 900, 40);
		connectingLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 24));
		
		frame.getContentPane().add(connectingLabel);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
	
	private void drawGameBoardScreen()
	{
		frame.getContentPane().removeAll();
		
		String playerTurnStr = playerTurn ? "Your Turn" : "Opponent Turn";
		JLabel playerTurnLabel = new JLabel(playerTurnStr, JLabel.CENTER);
		playerTurnLabel.setBounds(0, 80, 900, 40);
		
		for (int i = 0; i < playerSideHouses.size(); ++i)
		{
			KalahPitButton playerSideHouse = playerSideHouses.get(i);
			playerSideHouse.setText(kalahGame.getSeedCountAtMy(i) + "");
			frame.getContentPane().add(playerSideHouse);
			
			KalahPitButton opponentSideHouse = opponentSideHouses.get(i);
			opponentSideHouse.setText(kalahGame.getSeedCountAtOpponent(i) + "");
			frame.getContentPane().add(opponentSideHouse);
		}
		
		playerEndZone.setText(kalahGame.getSeedCountInMyPit() + "");
		opponentEndZone.setText(kalahGame.getSeedCountInOpponentPit() + "");
		
		frame.getContentPane().add(playerTurnLabel);
		frame.getContentPane().add(playerEndZone);
		frame.getContentPane().add(opponentEndZone);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
	
	private void drawIllegalMoveScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have made an illegal move and lost Kalah!", "Illegal Move", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void drawLoseScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have lost Kalah!", "Loser", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void drawLostConnectionScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have lost connection to the game server.\nClosing the application.", "Lost Connection", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void drawStartScreen()
	{
		frame.getContentPane().removeAll();
		
		JLabel titleLabel = new JLabel("Kalah", JLabel.CENTER);
		titleLabel.setBounds(0, 30, 900, 40);
		titleLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 24));
		
		JLabel teamLabel = new JLabel("Team 23", JLabel.CENTER);
		teamLabel.setBounds(0, 50, 900, 50);
		
		frame.getContentPane().add(titleLabel);
		frame.getContentPane().add(teamLabel);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
	
	private void drawTieScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have tied at Kalah!", "Draw", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void drawTimedOutScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You took to long to make a move and lost Kalah!", "Timeout", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void drawWaitingForConnectScreen()
	{
		frame.getContentPane().removeAll();
		
		JLabel connectingLabel = new JLabel("Waiting for opponent to connect...", JLabel.CENTER);
		connectingLabel.setBounds(0, 100, 900, 40);
		connectingLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 24));
		
		frame.getContentPane().add(connectingLabel);
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
		
	private void drawWinScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have won Kalah!", "Winner", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private int getPieRuleReponse()
	{
		Object[] options = { "Swap Sides", "Play Normally" };
	    return JOptionPane.showOptionDialog(frame, "Would you like to change sides with your opponent\nor continue play as normal?", "Pie Rule", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private String getServerAddress()
	{
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to Kalah", JOptionPane.QUESTION_MESSAGE);
	}
	
	private void gotInfoMessage(String message)
	{
		String[] infoMessageParts = message.split(" ");
		
		if (infoMessageParts[0].equals("INFO"))
		{
			try
			{
				int houseCount = Integer.parseInt(infoMessageParts[1]);
				int seedCount = Integer.parseInt(infoMessageParts[2]);
				int timeoutInMs = Integer.parseInt(infoMessageParts[3]);
				String goesFirstStr = infoMessageParts[4];
				String standardLayoutStr = infoMessageParts[5];
				
				if (goesFirstStr.equals("F"))
				{
					playerTurn = true;
					System.out.println("DEBUG -- Player Turn");
				}
				else if (goesFirstStr.equals("S"))
				{
					playerTurn = false;
					System.out.println("DEBUG -- Not Player Turn");
				}
				else
				{
					System.out.println("Invalid info message from server.");
					System.out.println("Closing...");
					System.exit(1);
				}
				
				if (standardLayoutStr.equals("S"))
				{
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
				}
				else
				{
					System.out.println("Invalid info message from server.");
					System.out.println("Closing...");
					System.exit(1);
				}
				
				int buttonSize = (frame.getWidth() - 35 - (houseCount * 5)) / (houseCount + 2);
				
				opponentEndZone = new KalahPitButton();
				opponentEndZone.setBounds(15, 110, buttonSize, 255);
				opponentEndZone.setHorizontalAlignment(SwingConstants.CENTER);
				opponentEndZone.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
				opponentEndZone.setIndex(0);
				
				playerEndZone = new KalahPitButton();
				playerEndZone.setBounds(frame.getWidth() - 15 - buttonSize, 110, buttonSize, 255);
				playerEndZone.setHorizontalAlignment(SwingConstants.CENTER);
				playerEndZone.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
				playerEndZone.setIndex(0);
				
				for (int i = 0; i < houseCount; ++i)
				{
					KalahPitButton myHouse = new KalahPitButton();
					myHouse.setBounds(buttonSize + 20 + i * 5 + i * buttonSize, 250, buttonSize, 115);
					myHouse.setHorizontalAlignment(SwingConstants.CENTER);
					myHouse.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
					myHouse.setIndex(i);
					
					myHouse.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							
							kalahButtonPressed((KalahPitButton)(e.getSource()));
						}
					});
					
					playerSideHouses.add(myHouse);
					
					KalahPitButton otherHouse = new KalahPitButton();
					otherHouse.setBounds(frame.getWidth() - 15 - (i + 1) * 5 - ((i + 2) * buttonSize), 110, buttonSize, 115);
					otherHouse.setHorizontalAlignment(SwingConstants.CENTER);
					otherHouse.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
					opponentSideHouses.add(otherHouse);
				}
			}
			catch (Exception e)
			{
				System.out.println("Invalid info message from server.");
				System.out.println("Closing...");
				System.exit(1);
			}
		}
		else
		{
			System.out.println("Invalid info message from server.");
			System.out.println("Closing...");
			System.exit(1);
		}
		
		writeToServer("READY");
	}
	
	private void gotOpponentMove(String moves)
	{
		writeToServer("OK");
		
		String[] indexes = moves.split(" ");
		
		for (int i = 0; i < indexes.length; ++i)
		{
			kalahGame.executeMove(Integer.parseInt(indexes[i]), 1);
		}
		
		playerTurn = true;
		
		drawGameBoardScreen();
		
		if (!madeFirstMove)
		{
			int response = getPieRuleReponse();
			System.out.println("DEBUG -- Pie rule response: " + response);
			
			if (response == 0)
			{
				writeToServer("P");
				playerTurn = false;
				needsAck = true;
				kalahGame.swapSides();
				drawGameBoardScreen();
				madeFirstMove = true;
			}
		}
	}
	
	private boolean gotPlayerMove(int move)
	{
		int result = kalahGame.executeMove(move, 0);
		boolean bonusMove = (result == 1);
		
		playerTurn = bonusMove;
		drawGameBoardScreen();
		
		return bonusMove;
	}
	
	private void kalahButtonPressed(KalahPitButton source)
	{
		if (playerTurn)
		{
			int idx = source.getIndex();
			
			boolean bonusMove = gotPlayerMove(idx);
			
			if (moveString.length() > 0)
			{
				moveString += " " + idx;
			}
			else
			{
				moveString += idx;
			}
			
			if (!bonusMove)
			{
				writeToServer(moveString);
				madeFirstMove = true;
				needsAck = true;
				playerTurn = false;
				moveString = "";
			}
		}
	}
	
	private String readFromServer() throws IOException
	{
		String result = input.readLine();
		System.out.println("DEBUG -- Read from server: " + result);
		return result;
	}
	
	private void run() throws IOException
	{
		String serverAddress = getServerAddress();
		if (serverAddress != null)
		{
			try
			{
				Socket socket = new Socket(serverAddress, 9090);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
			}
			catch (ConnectException e)
			{
				System.out.println("Couldn't connect to server.");
				System.out.println("Quitting...");
				System.exit(1);
			}

			drawConnectingScreen();
			
			while (true)
			{
				String line = readFromServer();
				if (line == null)
				{
					System.out.println("DEBUG -- Lost connection to server.");
					
					drawLostConnectionScreen();
					
					System.out.println("Closing...");
					System.exit(1);
				}
				else if (line.startsWith("WELCOME"))
				{
					if (needsAck)
					{
						System.out.println("Got welcome instead of an ack.");
						System.out.println("Quitting...");
						System.exit(1);
					}
					else
					{
						System.out.println("DEBUG -- Got welcome.");
						needsInfo = true;
						// NOTE(Drew): Properly connected, waiting for other client so we can get info.
						drawWaitingForConnectScreen();
					}
				}
				else if (line.startsWith("INFO"))
				{
					if (needsAck)
					{
						System.out.println("Got new information instead of an ack.");
						System.out.println("Quitting...");
						System.exit(1);
					}
					else if (needsInfo)
					{
						System.out.println("DEBUG -- Got info.");
						gotInfoMessage(line);
						needsInfo = false;
						drawGameBoardScreen();
					}
					else
					{
						System.out.println("Got new information after initialization.");
						System.out.println("Quitting...");
						System.exit(1);
					}
				}
				else if (line.startsWith("OK"))
				{
					System.out.println("DEBUG -- Got ack.");
					needsAck = false;
				}
				else if (line.startsWith("WINNER"))
				{
					System.out.println("DEBUG -- Player won.");
					
					// NOTE(Drew): This just updates the game so we can update the GUI.
					kalahGame.hasWinner();
					drawGameBoardScreen();
					
					drawWinScreen();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
				else if (line.startsWith("LOSER"))
				{
					System.out.println("DEBUG -- Player lost.");
					
					// NOTE(Drew): This just updates the game so we can update the GUI.
					kalahGame.hasWinner();
					drawGameBoardScreen();
					
					drawLoseScreen();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
				else if (line.startsWith("TIE"))
				{
					System.out.println("DEBUG -- Player tied");
					
					// NOTE(Drew): This just updates the game so we can update the GUI.
					kalahGame.hasWinner();
					drawGameBoardScreen();
					
					drawTieScreen();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
				else if (line.startsWith("ILLEGAL"))
				{
					System.out.println("DEBUG -- Illegal move.");
					
					drawIllegalMoveScreen();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
				else if (line.startsWith("TIMEOUT"))
				{
					System.out.println("DEBUG -- Player took too long and timed out.");
					
					drawTimedOutScreen();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
				else if (line.startsWith("P"))
				{
					writeToServer("OK");
					System.out.println("DEBUG -- Player chose pie rule.");
					kalahGame.swapSides();
					playerTurn = true;
					drawGameBoardScreen();
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
			System.exit(1);
		}
	}
	
	private void writeToServer(String message)
	{
		System.out.println("DEBUG -- Writing to server: " + message);
		output.println(message);
	}
}
