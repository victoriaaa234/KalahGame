
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
	
	public KalahGame(KalahGame toCopy)
	{
		this.kalahBoard = new int[toCopy.kalahBoard.length];
		this.endZoneIdx = new int[toCopy.endZoneIdx.length];
		
		System.arraycopy(toCopy.kalahBoard, 0, this.kalahBoard, 0, toCopy.kalahBoard.length);
		System.arraycopy(toCopy.endZoneIdx, 0, this.endZoneIdx, 0, toCopy.endZoneIdx.length);
	}
	
	// NOTE(Drew): Uses return codes
	// 0 - Completed successfully, no bonus move
	// 1 - Player earned a bonus move
	// 2 - Given an invalid move
	public int executeMove(int idx, int playerIdx)
	{
		int result = 0;
		int opponentIdx = (playerIdx + 1) % 2;
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
					// NOTE(Drew): Don't put seeds into opponent's end zone
					if (boardIdx == endZoneIdx[opponentIdx])
					{
						--i;
					}
					kalahBoard[boardIdx]++;
				}

				int finishIdx = boardIdx - 1;
				if (finishIdx == -1)
				{
					finishIdx = endZoneIdx[0]; // NOTE(Drew): Fixes edge case movement issue
				}

				// NOTE(Drew): Handle assigning bonus moves
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

				// NOTE(Drew): Handle war scenarios
				if (kalahBoard[finishIdx] == 1)
				{
					if (playerIdx == 0)
					{
						if (finishIdx < endZoneIdx[0])
						{
							int oppositeIdx = (endZoneIdx[0] - finishIdx) * 2 + finishIdx;
							if (kalahBoard[oppositeIdx] > 0)
							{
								kalahBoard[endZoneIdx[0]] += kalahBoard[oppositeIdx] + 1;
								kalahBoard[oppositeIdx] = 0;
								kalahBoard[finishIdx] = 0;
							}
						}
					}
					else
					{
						if (finishIdx > endZoneIdx[0] && finishIdx != endZoneIdx[1])
						{
							int oppositeIdx = finishIdx - (finishIdx - endZoneIdx[0]) * 2;
							if (kalahBoard[oppositeIdx] > 0)
							{
								kalahBoard[endZoneIdx[1]] += kalahBoard[oppositeIdx] + 1;
								kalahBoard[oppositeIdx] = 0;
								kalahBoard[finishIdx] = 0;
							}
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

		// NOTE(Drew): If one of the sides is empty, the game is over
		result = playerSideEmpty || opponentSideEmpty;

		// NOTE(Drew): If player is empty, move all opponent seeds to opponent end zone
		if (playerSideEmpty && !opponentSideEmpty)
		{
			for (int i = endZoneIdx[0] + 1; i < endZoneIdx[1]; ++i)
			{
				kalahBoard[endZoneIdx[1]] += kalahBoard[i];
				kalahBoard[i] = 0;
			}
		}

		// NOTE(Drew): If opponent is empty, move all player seeds to player end zone
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
	
	public static boolean isDifferentAt(KalahGame realGame, KalahGame potentialGame, int idx, int playerIdx)
	{
		boolean result = realGame.getSeedCountAt(idx, playerIdx) != potentialGame.getSeedCountAt(idx, playerIdx);
		
		return result;
	}
	
	public static boolean isDifferentAtMy(KalahGame realGame, KalahGame potentialGame, int idx)
	{
		boolean result = realGame.getSeedCountAtMy(idx) != potentialGame.getSeedCountAtMy(idx);
		
		return result;
	}
	
	public static boolean isDifferentAtOpponent(KalahGame realGame, KalahGame potentialGame, int idx)
	{
		boolean result = realGame.getSeedCountAtOpponent(idx) != potentialGame.getSeedCountAtOpponent(idx);
		
		return result;
	}
	
	public static boolean isDifferentInMyPit(KalahGame realGame, KalahGame potentialGame)
	{
		boolean result = realGame.getSeedCountInMyPit() != potentialGame.getSeedCountInMyPit();
		
		return result;
	}
	
	public static boolean isDifferentInOpponentPit(KalahGame realGame, KalahGame potentialGame)
	{
		boolean result = realGame.getSeedCountInOpponentPit() != potentialGame.getSeedCountInOpponentPit();
		
		return result;
	}

	public boolean isPlayerLosing(int playerIdx)
	{
		int opponentIdx = (playerIdx + 1) % 2;

		boolean result = kalahBoard[endZoneIdx[playerIdx]] < kalahBoard[endZoneIdx[opponentIdx]];

		return result;
	}

	public boolean isPlayerWinning(int playerIdx)
	{
		int opponentIdx = (playerIdx + 1) % 2;

		boolean result = kalahBoard[endZoneIdx[playerIdx]] > kalahBoard[endZoneIdx[opponentIdx]];

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
