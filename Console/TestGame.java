import java.util.*;

public class TestGame{



	public static void main(String[] args){
		GameState g = new GameState(6,4);
		GameState p1 = new GameState(6,4);
		Minimax ai = new Minimax();
		ai.treeHelper(g,6,true);
		ai.calcMinMax(g, true);
		//ai.printBestChoice(g);

		Scanner sc = new Scanner(System.in);
		//g.printBoard();
		System.out.println("Welcome to Mancala.");
		int choice = 0;
		boolean player = true;
		g.printBoard();
		while(!gameOver(g) || !gameOver(p1)){
			
			
			if(player){
				System.out.println("Player 1's turn");
				System.out.println("Enter a choice between 1 - 6");
				choice = sc.nextInt();
				if(p1.turn(choice,true,true,"")==0){
					
					p1.printBoard();
					continue;
				}
				else{
					if(gameOver(p1)){
						break;
					}
					p1.printBoard();
					player = !player;
				}
				
					
				
			}
			else{
				System.out.println("Player 2's turn");
				
				//g.printBoard();
				if(g.next.isEmpty()){
					ai.treeHelper(g,6,true);
					ai.calcMinMax(g, true);
				}
				for(GameState a : g.next){
					//a.printBoard();
					if(a.equals(p1)){
						//System.out.println("Found it");
						//g=a.getNextChoice();
						for(GameState aa: a.next){
							if(aa.equals(a.getNextChoice())){
								//System.out.println("best choice");
								//aa.printBoard();
								g=aa;
								break;
							}
						}
						break;
					}
				}
				g.printBoard();
				p1=new GameState(g);
				//p1=g;
				
						
				player = !player;
			}
			
			
			
			
			
			
			
		}
		p1.printBoard();
		
		

		
	}
	
	public static boolean gameOver(GameState g){
		return (g.endGame(true) || g.endGame(false));
	}


}