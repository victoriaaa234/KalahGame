import java.io.IOException;
import java.util.Scanner;

public class KalahGame
{
	public static void main(String[] args)
	{
        try
        {
        	Scanner scanner = new Scanner(System.in);
        	Boolean programRunning = true;
        	int again = 1;
        	while (programRunning && again == 1)
        	{
        		KalahGameController gameController = new KalahGameController(scanner);
        		gameController.runGame();
        		System.out.println("Would you like to play again?");
        		System.out.println("1. Play again.");
        		System.out.println("2. Do not play again.");
        		again = scanner.nextInt();
        		scanner.nextLine();
        	}
        	scanner.close();
        }
		
        catch (IOException e)
        {
        	// NOTE(Drew): We crash on cases where data should have been validated, but wasn't.
			e.printStackTrace();
		}
    }
}
