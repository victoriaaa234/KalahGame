import java.io.IOException;

public class KalahGame
{
	public static void main(String[] args)
	{
        try
        {
        	Boolean programRunning = true;
        	while (programRunning)
        	{
        		KalahGameController gameController = new KalahGameController();
        		gameController.runGame();
        	}
		}
        catch (IOException e)
        {
        	// NOTE(Drew): We crash on cases where data should have been validated, but wasn't.
			e.printStackTrace();
		}
    }
}
