
public class KalahGame
{
	private int[] kalahBoard;
	private int[] endZoneIdx;
	
	private KalahGame(int numHouses)
	{
		kalahBoard = new int[numHouses * 2 + 2];
		endZoneIdx = new int[2];
		endZoneIdx[0] = numHouses;
		endZoneIdx[1] = numHouses * 2 + 1;
		
		kalahBoard[endZoneIdx[0]] = 0;
		kalahBoard[endZoneIdx[1]] = 0;
	}
	
	public KalahGame(int numHouses, int numSeedsPerHouse)
	{
		this(numHouses);
		
		for (int i = 0; i < numHouses; ++i)
		{
			kalahBoard[i] = numSeedsPerHouse;
			kalahBoard[i + endZoneIdx[0] + 1] = numSeedsPerHouse;
		}
		
		
	}
	
	public KalahGame(int numHouses, int[] numSeeds)
	{
		this(numHouses);
		
		for (int i = 0; i < numHouses; ++i)
		{
			kalahBoard[i] = numSeeds[i];
			kalahBoard[i + endZoneIdx[0] + 1] = numSeeds[i];
		}
	}
	
	// NOTE(Drew): Uses return codes
	// 0 - Completed successfully, no bonus move
	// 1 - Player earned a bonus move
	// 2 - Given an invalid move
	public int executeMove(int idx, int playerIdx)
	{
		int result = 0;
		boolean bonusMove = false;
		
		if (idx < endZoneIdx[0])
		{
			if (playerIdx == 1)
			{
				idx += endZoneIdx[0] + 1;
			}
			
			int numSeeds = kalahBoard[idx];
			
			if (numSeeds > 0)
			{
				kalahBoard[idx] = 0;
				int boardIdx = idx + 1;
				
				for (int i = 0; i < numSeeds; boardIdx = (boardIdx + 1) % (endZoneIdx[1] + 1), ++i)
				{
					kalahBoard[boardIdx]++;
				}
				
				int finishIdx = boardIdx - 1;
				if (finishIdx == -1)
				{
					finishIdx = endZoneIdx[0];
				}
				
				if (playerIdx == 0)
				{
					if (finishIdx == endZoneIdx[0])
					{
						if (!isSideEmpty(0))
						{
							bonusMove = true;
						}
					}
				}
				else if (playerIdx == 1)
				{
					if (finishIdx == endZoneIdx[1])
					{
						if (!isSideEmpty(1))
						{
							bonusMove = true;
						}
					}
				}
				
				if (kalahBoard[finishIdx] == 1)
				{
					System.out.println("DEBUG -- Potential War!");
					if (playerIdx == 0)
					{
						if (finishIdx < endZoneIdx[0])
						{
							System.out.println("DEBUG -- Executing war!");
							int opponentIdx = (endZoneIdx[0] - finishIdx) * 2 + finishIdx;
							kalahBoard[endZoneIdx[0]] += kalahBoard[opponentIdx];
							kalahBoard[opponentIdx] = 0;
						}
					}
					else
					{
						if (finishIdx > endZoneIdx[0] && finishIdx != endZoneIdx[1])
						{
							System.out.println("DEBUG -- Executing war!");
							int opponentIdx = finishIdx - (finishIdx - endZoneIdx[0]) * 2;
							kalahBoard[endZoneIdx[1]] += kalahBoard[opponentIdx];
							kalahBoard[opponentIdx] = 0;
						}
					}
				}
				
				if (result == 0 && bonusMove)
				{
					result = 1;
				}
			}
			else
			{
				result = 2;
			}
		}
		else
		{
			result = 2;
		}
		
		return result;
	}
	
	public void forceLoser(int playerIdx)
	{
		if (playerIdx == 0)
		{
			forceMyLoss();
		}
		else
		{
			forceOpponentLoss();
		}
	}
	
	public void forceMyLoss()
	{
		for (int i = 0; i < endZoneIdx[0]; ++i)
		{
			kalahBoard[i] = 0;
			kalahBoard[i + endZoneIdx[0] + 1] = 0;
		}
		
		kalahBoard[endZoneIdx[0]] = -1;
		kalahBoard[endZoneIdx[1]] = 1;
	}
	
	public void forceOpponentLoss()
	{
		for (int i = 0; i < endZoneIdx[0]; ++i)
		{
			kalahBoard[i] = 0;
			kalahBoard[i + endZoneIdx[0] + 1] = 0;
		}
		
		kalahBoard[endZoneIdx[0]] = 1;
		kalahBoard[endZoneIdx[1]] = -1;
	}
	
	public int getSeedCountAt(int idx)
	{
		return kalahBoard[idx];
	}
	
	public int getSeedCountAt(int idx, int playerIdx)
	{
		if (playerIdx == 0)
		{
			return getSeedCountAtMy(idx);
		}
		else
		{
			return getSeedCountAtOpponent(idx);
		}
	}
	
	public int getSeedCountAtMy(int idx)
	{
		return kalahBoard[idx];
	}
	
	public int getSeedCountAtOpponent(int idx)
	{
		return kalahBoard[idx + endZoneIdx[0] + 1];
	}
	
	public int getSeedCountInMyPit()
	{
		return kalahBoard[endZoneIdx[0]];
	}
	
	public int getSeedCountInOpponentPit()
	{
		return kalahBoard[endZoneIdx[1]];
	}

	public int getSeedCountInPit(int playerIdx)
	{
		return kalahBoard[endZoneIdx[playerIdx]];
	}
	
	public boolean hasWinner()
	{
		boolean result = true;

		boolean playerSideEmpty = isSideEmpty(0);
		boolean opponentSideEmpty = isSideEmpty(1);
		
		result = playerSideEmpty || opponentSideEmpty;
		
		if (playerSideEmpty && !opponentSideEmpty)
		{
			for (int i = endZoneIdx[0] + 1; i < endZoneIdx[1]; ++i)
			{
				kalahBoard[endZoneIdx[1]] += kalahBoard[i];
				kalahBoard[i] = 0;
			}
		}
		
		if (opponentSideEmpty && !playerSideEmpty)
		{
			for (int i = 0; i < endZoneIdx[0]; ++i)
			{
				kalahBoard[endZoneIdx[0]] += kalahBoard[i];
				kalahBoard[i] = 0;
				
			}
		}

		return result;
	}

	public boolean isPlayerLosing(int playerIdx)
	{
		int opponentIdx = (playerIdx + 1) % 2;
		
		boolean result = endZoneIdx[playerIdx] < endZoneIdx[opponentIdx];
		
		return result;
	}
	
	public boolean isPlayerWinning(int playerIdx)
	{
		int opponentIdx = (playerIdx + 1) % 2;
		
		boolean result = endZoneIdx[playerIdx] > endZoneIdx[opponentIdx];
		
		return result;
	}
	
	private boolean isSideEmpty(int playerIdx)
	{
		boolean isEmpty = true;
		
		int i = (playerIdx == 0) ? 0 : endZoneIdx[0] + 1;
		for (; i < endZoneIdx[playerIdx]; ++i)
		{
			if (kalahBoard[i] > 0)
			{
				isEmpty = false;
				break;
			}
		}
		
		return isEmpty;
	}
		
	public boolean isTied()
	{
		boolean result = false;

		if (kalahBoard[endZoneIdx[0]] == kalahBoard[endZoneIdx[1]])
		{
			result = true;
		}

		return result;
	}

	// NOTE(Drew): Should only be used for pie rule swaps
	public void swapSides()
	{
		int temp = kalahBoard[endZoneIdx[0]];
		kalahBoard[endZoneIdx[0]] = kalahBoard[endZoneIdx[1]];
		kalahBoard[endZoneIdx[1]] = temp;
		
		for (int i = 0; i < endZoneIdx[0]; ++i)
		{
			int opponentIdx = i + endZoneIdx[0] + 1;
			
			temp = kalahBoard[i];
			kalahBoard[i] = kalahBoard[opponentIdx];
			kalahBoard[opponentIdx] = temp;
		}
	}
}
