import java.util.*;
import java.io.*;
import java.lang.*;

public class GameState{

	public ArrayList<GameState> next;
	private int[] board;
	private int score;
	//public booleanean extraMove;
	private int holes;
	private int seeds;
	private int size;
	private int min_max;

	public GameState(int holes, int seeds){
		next = new ArrayList<GameState>();
		size = 2*holes+2;
		board = new int[size];
		for(int i =0; i<size; i++){
			if(i !=0 && i!=holes+1){
				//System.out.println(i);
				board[i]=seeds;
			}
			else{
				board[i]=0;
			}
		}
		this.holes = holes; 
		this.seeds = seeds;
	}
	public GameState(GameState prev){
		next = new ArrayList<GameState>();
		this.holes = prev.getHoles(); 
		this.seeds = prev.getSeeds();;
		 size = 2*holes+2;
		board = new int[size];
		for(int i =0; i<size; i++){
			board[i]= prev.getBoard()[i];
		}
	}
	//get methods
	public int getHoles(){
		return holes;
	}
	public int getSeeds(){
		return seeds;
	}
	public int[] getBoard(){
		return board;
	}
	public int getScore(){
		return score;
	}
	public int getMinMax(){
		return min_max;
	}
	
	//set methods
	public void setHoles(int holes){
		this.holes = holes;
	}
	public void setSeeds(int seeds){
		this.seeds = seeds;
	}
	public void updateScore(){
		score =  board[holes+1]- board[0];

	}
	public void setMinMax(int num){
		min_max=num;
	}  
	//helper functions
	
	public int getMax(){
		int m = Integer.MIN_VALUE;
		for(GameState a : next){
			m=Math.max(m,a.getMinMax());
		}
		System.out.println(m);
		return m;
	}
	public int getMin(){
		int m = Integer.MAX_VALUE;
		for(GameState a : next){
			m=Math.min(m,a.getMinMax());
		}
		return m;
		
	}
	
	
	
	//-1 if error 0 if you need to repeat  1 if move valid
	//player1 = true player 2= false
	public int turn(int choice, boolean player){
		
		if(player){
			System.out.println("Player 1 Chose"+ choice);
			if(choice<1 || choice >6){
				System.out.println("Error choose a house between 1 and 6");
				return -1;

			}
			int index = choice;
			if(board[choice]==0){
				System.out.println("Choose again house empty");
				//throw some exception
				return -1;
			}
			//check player options
			//System.out.println(board[choice]);
			while(board[choice]!=0){
				//if(board[choice])
					index++;

				if((index)%size != 0){
						
					board[choice]--;
					board[index%size]++;
				}	
				
			}
			if(index%size == holes+1){
				return 0;
			}
			updateScore();
			//System.out.println("Done");
			return 1;
		}
		else{
			System.out.println("Player 2 Chose"+ choice);

			if(choice<1 || choice >6){
				System.out.println("Error choose a house between 1 and 6");
				return -1;

			}
			int index = choice+holes+1;
			if(board[choice+holes+1]==0){
				System.out.println("Choose again house empty");
				//throw some exception
				return -1;
			}
			//check player options
			while(board[choice+holes+1]!=0){
				//if(board[choice])
					index++;

				if((index)%size != holes+1){
						
					board[choice+holes+1]--;
					board[index%size]++;
				}	
				
			}
			if(index%size == 0){
				return 0;
			}
			updateScore();
			return 1;
		}
		
	}
	//prints gamestate to file.
	public void printGameState(){
		
		try{
			FileWriter writer = new FileWriter("output.txt", true);
			
			writer.write("The Heuristic is :"+ min_max+"\n");	
			writer.write("----Player 2----\n");
			int size = holes*2+2;
			//prints player 2 holes
			writer.write(" ");

			for(int i =size-1; i>=holes+2; i--){
				writer.write(""+board[i]+" ");
			}
			writer.write("\n");
			writer.write(""+board[0]);

			//Prints both players score
			for(int i =0; i<holes; i++){
				writer.write("  ");
			}
			writer.write(""+board[holes+1]);
			writer.write("\n");
			writer.write(" ");

			//prints player 1 holes
			for(int i =1; i<=holes; i++){
				writer.write(""+board[i]+" ");
			}
			writer.write("\n");
			writer.write("----player 1----\n\n");
			
			
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
		   // do something
		}
	
		
	}
	








}