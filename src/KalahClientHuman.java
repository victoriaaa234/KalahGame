import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class KalahClientHuman extends KalahClient
{
	protected JFrame frame;
	protected JLabel timerLabel;
	protected ArrayList<KalahPitButton> playerSideHouses;
	protected ArrayList<KalahPitButton> opponentSideHouses;
	protected KalahPitButton playerEndZone;
	protected KalahPitButton opponentEndZone;

	protected Timer timer;
	
	protected long currentTime;
	protected boolean illegalMoveMade;
	protected boolean needsAck;
	protected boolean needsBegin;
	protected boolean needsInfo;
	protected boolean madeFirstMove;
	protected String moveString;
	protected boolean playerTurn;
	protected boolean resetTimer;
	protected boolean timeoutHappened;
	protected long timeoutInMs;
	
	protected boolean potentialBonusMove;
	protected KalahGame potentialGame;

	public static void main(String[] args) throws Exception
	{
		while (true)
		{
			KalahClientHuman client = new KalahClientHuman();
			client.run();
		}
    }

	public KalahClientHuman()
	{
		playerSideHouses = new ArrayList<KalahPitButton>();
		opponentSideHouses = new ArrayList<KalahPitButton>();

		frame = new JFrame("Kalah User Interface - Team 23");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setSize(900, 400);
		
		timerLabel = new JLabel();
		timerLabel.setBounds(800, 15, 85, 30);
		timerLabel.setHorizontalAlignment(JLabel.LEFT);
		timerLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));

		drawStartScreen();

		frame.setVisible(true);

		illegalMoveMade = false;
		needsAck = false;
		needsBegin = false;
		needsInfo = false;
		madeFirstMove = false;
		playerTurn = false;
		resetTimer = true;
		timeoutHappened = false;
		
		potentialBonusMove = false;
		potentialGame = null;

		moveString = "";
	}

	protected void drawConnectingScreen()
	{
		frame.getContentPane().removeAll();

		JLabel connectingLabel = new JLabel("Connecting to server...", JLabel.CENTER);
		connectingLabel.setBounds(0, 100, 900, 40);
		connectingLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 24));

		frame.getContentPane().add(connectingLabel);

		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	protected void drawConnectionFailedScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "Couldn't find a server at the given address.\nReturning to the launch screen.", "Lost Connection", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawGameBoardScreen()
	{
		frame.getContentPane().removeAll();

		String playerTurnStr = playerTurn ? "Your Turn" : "Opponent Turn";
		JLabel playerTurnLabel = new JLabel(playerTurnStr, JLabel.CENTER);
		playerTurnLabel.setBounds(0, 80, 900, 40);

		for (int i = 0; i < playerSideHouses.size(); ++i)
		{
			// NOTE(Drew): Set up player side houses
			KalahPitButton playerSideHouse = playerSideHouses.get(i);
			if (potentialGame != null && KalahGame.isDifferentAtMy(kalahGame, potentialGame, i))
			{
				// NOTE(Drew): On hover, show potential changes
				playerSideHouse.setText(potentialGame.getSeedCountAtMy(i) + "");
				playerSideHouse.setForeground(Color.RED);
			}
			else
			{
				playerSideHouse.setText(kalahGame.getSeedCountAtMy(i) + "");
				playerSideHouse.setForeground(Color.BLACK);
			}
			frame.getContentPane().add(playerSideHouse);

			// NOTE(Drew): Set up opponent side houses
			KalahPitButton opponentSideHouse = opponentSideHouses.get(i);
			if (potentialGame != null && KalahGame.isDifferentAtOpponent(kalahGame, potentialGame, i))
			{
				// NOTE(Drew): On hover, show potential changes
				opponentSideHouse.setText(potentialGame.getSeedCountAtOpponent(i) + "");
				opponentSideHouse.setForeground(Color.RED);
			}
			else
			{
				opponentSideHouse.setText(kalahGame.getSeedCountAtOpponent(i) + "");
				opponentSideHouse.setForeground(Color.BLACK);
			}
			frame.getContentPane().add(opponentSideHouse);
		}

		
		if (potentialGame != null && KalahGame.isDifferentInMyPit(kalahGame, potentialGame))
		{
			// NOTE(Drew): On hover, show potential changes
			playerEndZone.setText(potentialGame.getSeedCountInMyPit() + "");
			if (potentialBonusMove)
			{
				// NOTE(Drew): Bonus move shows up in green
				playerEndZone.setForeground(Color.GREEN);
			}
			else
			{
				playerEndZone.setForeground(Color.RED);
			}
		}
		else
		{
			playerEndZone.setText(kalahGame.getSeedCountInMyPit() + "");
			playerEndZone.setForeground(Color.BLACK);
		}
		
		if (potentialGame != null && KalahGame.isDifferentInOpponentPit(kalahGame, potentialGame))
		{
			// NOTE(Drew): On hover, show potential changes
			opponentEndZone.setText(potentialGame.getSeedCountInOpponentPit() + "");
			opponentEndZone.setForeground(Color.RED);
		}
		else
		{
			opponentEndZone.setText(kalahGame.getSeedCountInOpponentPit() + "");
			opponentEndZone.setForeground(Color.BLACK);
		}
		
		if (playerTurn)
		{
			if (resetTimer)
			{
				// NOTE(Drew): Only reset the timer once per move
				resetTimer = false;
				
				currentTime = timeoutInMs / 1000;
				
				String numSeconds = String.format("%02d", timeoutInMs / 1000);
				timerLabel.setText(timeoutInMs / 60000 + ":" + numSeconds);
				timer = new Timer(1000, new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (currentTime > 0)
						{
							--currentTime;
							String numSeconds = String.format("%02d", currentTime);
							timerLabel.setText(currentTime / 60 + ":" + numSeconds);
						}
						else
						{
							timer.stop();
						}
					}
				});
				timer.start();
			}
			frame.getContentPane().add(timerLabel);
		}
		else
		{
			resetTimer = true;
			if (timer != null)
			{
				timer.stop();
			}
		}

		frame.getContentPane().add(playerTurnLabel);
		frame.getContentPane().add(playerEndZone);
		frame.getContentPane().add(opponentEndZone);

		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	protected void drawIllegalMoveScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have made an illegal move and lost Kalah!", "Illegal Move", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawLoseScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have lost Kalah!", "Loser", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawLostConnectionScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have lost connection to the game server.\nReturning to the launch screen.", "Lost Connection", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawStartScreen()
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

	protected void drawTieScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have tied at Kalah!", "Draw", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawTimedOutScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You took to long to make a move and lost Kalah!", "Timeout", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected void drawWaitingForConnectScreen()
	{
		frame.getContentPane().removeAll();

		JLabel connectingLabel = new JLabel("Waiting for opponent to connect...", JLabel.CENTER);
		connectingLabel.setBounds(0, 100, 900, 40);
		connectingLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 24));

		frame.getContentPane().add(connectingLabel);

		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	protected void drawWinScreen()
	{
		Object[] options = { "OK" };
	    JOptionPane.showOptionDialog(frame, "You have won Kalah!", "Winner", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected int getPieRuleResponse()
	{
		Object[] options = { "Swap Sides", "Play Normally" };
	    return JOptionPane.showOptionDialog(frame, "Would you like to change sides with your opponent\nor continue play as normal?", "Pie Rule", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	protected String getServerAddress()
	{
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to Kalah", JOptionPane.QUESTION_MESSAGE);
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
					System.out.println("Returning to the launch screen.");
					return false;
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
					System.out.println("Returning to the launch screen.");
					return false;
				}

				// NOTE(Drew): Initialize buttons and size them to fit the available space
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

					// NOTE(Drew): Click action listeners handle moves
					myHouse.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							kalahButtonPressed((KalahPitButton)(e.getSource()));
						}
					});
					
					// NOTE(Drew): Hover action listeners handle move prediction
					myHouse.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseEntered(MouseEvent e)
						{
							kalahButtonHoverStart((KalahPitButton)(e.getSource()));
						}
						
						@Override
						public void mouseExited(MouseEvent e)
						{
							kalahButtonHoverEnd((KalahPitButton)(e.getSource()));
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

	protected void gotOpponentMove(String moves)
	{
		super.gotOpponentMove(moves);

		playerTurn = true;

		drawGameBoardScreen();

		if (!madeFirstMove)
		{
			int response = getPieRuleResponse();
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

	protected boolean gotPlayerMove(int move)
	{
		boolean bonusMove = super.gotPlayerMove(move);

		playerTurn = bonusMove;
		drawGameBoardScreen();

		return bonusMove;
	}

	private void kalahButtonHoverEnd(KalahPitButton source)
	{
		potentialGame = null;
		potentialBonusMove = false;
		drawGameBoardScreen();
	}
	
	private void kalahButtonHoverStart(KalahPitButton source)
	{
		if (playerTurn)
		{
			int idx = source.getIndex();
			
			potentialGame = new KalahGame(kalahGame);
			int result = potentialGame.executeMove(idx, 0);
			if (result == 2)
			{
				potentialGame = null;
				potentialBonusMove = false;
			}
			else if (result == 1)
			{
				potentialBonusMove = true;
			}
			else
			{
				potentialBonusMove = false;
			}
			
			drawGameBoardScreen();
		}
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

	protected void quit()
	{
		frame.setVisible(false);
		frame.dispose();
	}
	
	@Override
	protected void run()
	{
		String serverAddress = getServerAddress();
		if (serverAddress != null)
		{
			try
			{
				super.connectToServer(serverAddress);
			}
			catch (ConnectException e)
			{
				drawConnectionFailedScreen();
				System.out.println("Returning to launch screen.");
				quit();
				return;
			}
			catch (UnknownHostException e)
			{
				drawConnectionFailedScreen();
				System.out.println("Returning to launch screen.");
				quit();
				return;
			}
			catch (IOException e)
			{
				drawConnectionFailedScreen();
				System.out.println("Returning to launch screen.");
				quit();
				return;
			}

			drawConnectingScreen();

			while (true)
			{
				String line = "";
				try
				{
					line = readFromServer();
				}
				catch (IOException e)
				{
					drawLostConnectionScreen();
					
					System.out.println("Returning to launch screen.");
					quit();
					return;
				}

				if (line == null || line == "")
				{
					System.out.println("DEBUG -- Lost connection to server.");

					drawLostConnectionScreen();

					System.out.println("Returning to launch screen.");
					quit();
					return;
				}
				else if (line.startsWith("BEGIN"))
				{
					if (needsAck)
					{
						System.out.println("Got begin instead of an ack.");
						System.out.println("Returning to launch screen.");
						quit();
						return;
					}
					else if (needsInfo)
					{
						System.out.println("Got begin before got info.");
						System.out.println("Returning to launch screen.");
						quit();
						return;
					}
					else if (needsBegin)
					{
						drawGameBoardScreen();
						needsBegin = false;
					}
					else
					{
						System.out.println("Received duplicate begin.");
						System.out.println("Returning to launch screen.");
						quit();
						return;
					}
				}
				else if (line.startsWith("WELCOME"))
				{
					if (needsAck)
					{
						System.out.println("Got welcome instead of an ack.");
						System.out.println("Returning to launch screen.");
						quit();
						return;
					}
					else
					{
						System.out.println("DEBUG -- Got welcome.");
						needsInfo = true;
						needsBegin = true;
						// NOTE(Drew): Properly connected, waiting for other client so we can begin.
						drawWaitingForConnectScreen();
					}
				}
				else if (line.startsWith("INFO"))
				{
					if (needsAck)
					{
						System.out.println("Got new information instead of an ack.");
						System.out.println("Returning to launch screen.");
						quit();
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
							quit();
							return;
						}
					}
					else
					{
						System.out.println("Got new information after initialization.");
						System.out.println("Returning to launch screen.");
						quit();
						return;
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
					System.out.println("Returning to launch screen.");
					quit();
					return;
				}
				else if (line.startsWith("LOSER"))
				{
					System.out.println("DEBUG -- Player lost.");

					// NOTE(Drew): This just updates the game so we can update the GUI.
					kalahGame.hasWinner();
					drawGameBoardScreen();

					if (illegalMoveMade)
					{
						drawIllegalMoveScreen();
					}
					else if (timeoutHappened)
					{
						drawTimedOutScreen();
					}
					else
					{
						drawLoseScreen();
					}
					System.out.println("Returning to launch screen.");
					quit();
					return;
				}
				else if (line.startsWith("TIE"))
				{
					System.out.println("DEBUG -- Player tied");

					// NOTE(Drew): This just updates the game so we can update the GUI.
					kalahGame.hasWinner();
					drawGameBoardScreen();

					drawTieScreen();
					System.out.println("Returning to launch screen.");
					quit();
					return;
				}
				else if (line.startsWith("ILLEGAL"))
				{
					System.out.println("DEBUG -- Illegal move.");
					illegalMoveMade = true;
				}
				else if (line.startsWith("TIMEOUT"))
				{
					System.out.println("DEBUG -- Player took too long and timed out.");
					timeoutHappened = true;
				}
				else if (line.startsWith("P"))
				{
					System.out.println("DEBUG -- Player chose pie rule.");
					gotPieMove();
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
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
}
