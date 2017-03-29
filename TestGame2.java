import java.util.*;
public class TestGame2{

	public static void main(String[] args){
		GameState g = new GameState(6,4);
		g.printGameState();
		Scanner sc = new Scanner(System.in);
		boolean player;
		while(true){
			//check the mod for the index changes.
			System.out.println("player 1 or player 2");
			if(sc.nextInt() ==1){
				player = true;
			}
			else{
				player = false;
			}
			g.turn(sc.nextInt(),player);
			g.printGameState();
			
		}
		
		
	
	}

}