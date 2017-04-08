import java.util.*;
import java.io.*;

public class GameState 
{
    public ArrayList<GameState> next;
    private int[] board;
    private int score;
    private int holes;
    private int seeds;
    private int size;
    private int min_max;
    private GameState nextChoice;
    private String turnSequence;
    private int v;
    private boolean isEndGame;
    private boolean endP1;
    private boolean endP2;
    
    public GameState()
    {
        score = 0;
        holes = 0;
        seeds = 0;
        size = 0;
        min_max= 0;
        turnSequence = "";
        nextChoice = null;
        isEndGame = false;
    }
    
    public GameState(int holes, int seeds) 
    {
        next = new ArrayList<GameState>();
        size = 2*holes + 2;
        board = new int[size];
        for(int i = 0; i < size; i++) 
        {
            if(i != 0 && i != holes + 1) 
            {
                board[i] = seeds;
            }
            else 
            {
                board[i] = 0;
            }
        }
        //this.score = 0;
        //this.min_max = 0;
        //this.nextChoice = null;
        //this.turnSequence = "";
        //this.v = 0;
        this.holes = holes; 
        this.seeds = seeds;
        this.isEndGame = false;
    }
    
    public GameState(int holes, int[] seeds) 
    {
    	next = new ArrayList<GameState>();
    	size = 2*holes + 2;
    	board = new int[size];
    	int totalSeeds = 0;
    	for(int i = 0; i < holes; i++) 
    	{
    		board[i+1] = seeds[i];
    		board[holes + 2 + i] = seeds[i];
    		totalSeeds += seeds[i];
    	}
    	board[0] = 0;
    	board[holes + 1] = 0;
    	this.holes = holes;
    	this.seeds = totalSeeds/holes;
    	//this.score = 0;
    	//this.min_max = 0;
    	//this.v = 0;
    	//this.nextChoice = null;
    	//this.turnSequence = "";
    	this.isEndGame = false;
    }
    
    public GameState(GameState prev)
    //currently doesn't work...
    {
//    	if(prev.getNextGameStates().isEmpty()) {
//    		next = new ArrayList<GameState>();
//    	}
//    	else {
//    		for(GameState g : prev.getNextGameStates()) {
//    			next = new ArrayList<GameState>();
//    			next.add(new GameState(g));
//    		}
//    	}
//    	this.score = prev.getScore();
//        this.holes = prev.getHoles(); 
//        this.seeds = prev.getSeeds();
//        this.turnSequence = prev.getTurnSequence();
//        this.min_max = prev.getMinMax();
//        if(prev.getNextChoice() != null) {
//        	this.setNextChoice(prev.getNextChoice());
//        }
//        else {
//        	this.nextChoice = null;
//        }
//        size = prev.getSize();
//        board = new int[size];
//        for(int i = 0; i < size; i++){
//            board[i] = prev.getBoard()[i];
//        }
//        this.v = prev.getV();
//        this.isEndGame = prev.getEndGame();
        next = new ArrayList<GameState>();
        this.holes = prev.getHoles(); 
        this.seeds = prev.getSeeds();;
        this.turnSequence=prev.getTurnSequence();
        this.min_max=prev.getMinMax();
        this.nextChoice=prev.nextChoice;
         size = 2*holes+2;
        board = new int[size];
        for(int i =0; i<size; i++){
            board[i]= prev.getBoard()[i];
        }
        this.v = prev.getV();
        this.isEndGame = prev.getEndGame();
    }
    
    //get methods
    public ArrayList<GameState> getNextGameStates() {
    	return next;
    }
    public boolean getEndGame() 
    {
        return isEndGame;
    }
    public int getSize() 
    {
        return size;
    }
    public int getHoles()
    {
        return holes;
    }
    public int getV()
    {
        return v;
    }
    public int getSeeds()
    {
        return seeds;
    }
    public int[] getBoard()
    {
        return board;
    }
    public int getScore()
    {
        return score;
    }
    public int getMinMax()
    {
        return min_max;
    }
    public GameState getNextChoice()
    {
        return nextChoice;
    }
    public String getTurnSequence()
    {
        return turnSequence;
    }
    
    //set methods
    public void setEndGame(boolean value)
    {
        isEndGame = value;
    }
    public void setHoles(int holes)
    {
        this.holes = holes;
    }
    public void setSeeds(int seeds)
    {
        this.seeds = seeds;
    }
    public void updateScore()
    {
        score =  board[holes+1] - board[0];
    }
    public void setMinMax(int num)
    {
        min_max=num;
    }  
    public void setNextChoice(GameState g)
    {
        nextChoice= new GameState(g);
//    	nextChoice = new GameState();
//    	nextChoice = g;
    }
    public void setV(int value)
    {
        v = value;
    }

    //helper functions
    public int getMax()
    {
        int m = Integer.MIN_VALUE;
        int tempM = m;
        for(GameState a : next)
        {
            tempM = Math.max(m, a.getMinMax());
            if(m < tempM)
            {
                setNextChoice(a);
                m = tempM;
            }
        }
        return m;
    }
    public int getMin()
    {
        int m = Integer.MAX_VALUE;
        int tempM = m;
        for(GameState a : next)
        {
            tempM = Math.min(m, a.getMinMax());
            if(m > tempM)
            {
                setNextChoice(a);
                m = tempM;
            }
        }
        return m;
    }
   
    //check if end of game
    public boolean endGameP1()
    {
            for(int i = 1; i <= holes; i++)
            {
                if(board[i] != 0)
                {
                    return false;
                }
            }
            endP1 = true;
            return true;
        
    }
    
    public boolean endGameP2()
    {
        for(int i = holes + 2; i <= size-1; i++)
        {
            if(board[i] != 0)
            {
                return false;
            }
        }
        endP2 = true;
        return true;
    }
    
    public boolean endGame(boolean player) {
    	return endGameP1() || endGameP2();
    }
    //-1 if error 0 if you need to repeat  1 if move valid
    //player1 = true player 2= false
    public int turn(int choice, boolean player, boolean end, String ts)
    {
        if(player)
        {
            //System.out.println("Player 1 Chose"+ choice);
            turnSequence = ts + " P1:" + choice;
            if(choice < 1 || choice > holes)
            {
                //System.out.println("Error choose a house between 1 and the number of holes chosen by the player");
                return -1;
            }
            int index = choice;
            if(board[choice] == 0)
            {
                //System.out.println("Choose again house empty");
                //throw some exception
                return -1;
            }
            //check player options
            //System.out.println(board[choice]);
            while(board[choice] != 0)
            {
                //if(board[choice])
                index++;
                if((index)%size != 0)
                {
                        
                    board[choice]--;
                    //war scenario
                    if(board[choice] == 0 && board[index%size] == 0 && index%size >= 1 && index%size <= holes && board[size-(index%size)] != 0)
                    {
                        board[holes+1] += board[size-(index%size)] + 1;
                        board[size-(index%size)] = 0;
                    }
                    else
                    {
                        board[index%size]++;
                    }
                }   
            }
            if(endGame(end))
            {
            	if(endP1) {
                    int remainingSeeds = 0;
                    for(int i = holes + 2; i <= size - 1; i++)
                    {
                        remainingSeeds += board[i]; 
                        board[i] = 0;
                    }
                    board[0] += remainingSeeds;
            	}
            	else if(endP2) {
                    int remainingSeeds = 0;
                    for(int i = 1; i <= holes; i++)
                    {
                        remainingSeeds += board[i]; 
                        board[i] = 0;
                    }
                    board[holes + 1] += remainingSeeds;
            	}
            	else {
            		
            	}
                System.out.println("end game");
                printBoard();
                setEndGame(true);
            }    
            else
            {
                if(index%size == holes+1)
                {
                    return 0;
                }
            }
            updateScore();
            return 1;
        }
        else
        {
            //System.out.println("Player 2 Chose"+ choice);
            turnSequence = ts + " P2:" + choice;
            if(choice < 1 || choice > holes)
            {
                //System.out.println("Error choose a house between 1 and the number of holes chosen by the player");
                return -1;
            }
            int index = choice + holes + 1;
            if(board[choice+holes+1] == 0)
            {
                //System.out.println("Choose again house empty");
                //throw some exception
                return -1;
            }
            //check player options
            while(board[choice+holes+1] != 0)
            {
                //if(board[choice])
                index++;
                if((index)%size != holes+1)
                {
                    board[choice+holes+1]--;
                    //war scenario
                    if(board[choice+holes+1] == 0 && board[index%size] == 0 && index%size >= holes + 2 && index%size <= size - 1 && board[size-(index%size)] != 0)
                    {
                        board[0] += board[size-(index%size)] + 1;
                        board[size-(index%size)] = 0;
                    }
                    else
                    {
                        board[index%size]++;
                    }
                }   
                
            }
            if(endGame(end))
            {
            	if(endP1) {
                    int remainingSeeds = 0;
                    for(int i = holes + 2; i <= size - 1; i++)
                    {
                        remainingSeeds += board[i]; 
                        board[i] = 0;
                    }
                    board[0] += remainingSeeds;
            	}
            	else if(endP2) {
                    int remainingSeeds = 0;
                    for(int i = 1; i <= holes; i++)
                    {
                        remainingSeeds += board[i]; 
                        board[i] = 0;
                    }
                    board[holes + 1] += remainingSeeds;
            	}
            	else {
            		
            	}
                System.out.println("End Game");
                printBoard();
                setEndGame(true);
            } 
            else
            {
                if(index%size == 0)
                {
                    return 0;
                }
            }
            updateScore();
            return 1;
        }
    }
    
    public void printBoard(){
		System.out.println("\n----Player 2----\n");
            int size = holes*2 + 2;
            //prints player 2 holes
            System.out.print(" ");

            for(int i = size-1; i >= holes+2; i--)
            {
                System.out.print(""+board[i]+" ");
            }
            System.out.println("");
            System.out.print(""+board[0]);

            //Prints both players score
            for(int i = 0; i < holes; i++)
            {
                System.out.print("  ");
            }
            System.out.println(""+board[holes+1]);
           // System.out.println("\n");
            System.out.print(" ");

            //prints player 1 holes
            for(int i = 1; i <= holes; i++)
            {
                System.out.print(""+board[i]+" ");
            }
            System.out.println("\n");
            System.out.println("----player 1----\n\n");
            
	}
  //checks to see if a GameState is equal to another 
  	public boolean equals(GameState gs)
  	{
  		for(int i = 0; i < this.size; i++)
  		{
  			if(this.board[i] != gs.getBoard()[i])
  			{
  				return false;
  			}
  		}
  		return true;
  	}
    
    //prints turn sequence to the console
    public void printTurnSequence()
    {
    	System.out.println(turnSequence);
    }
    
    //prints gamestate to file.
    public void printGameState()
    {
        try
        {
            FileWriter writer = new FileWriter("output.txt", true);
        
            writer.write("The Heuristic is :"+ min_max+"\n");   
            writer.write("Turn Sequence: "+ turnSequence);
            writer.write("\n----Player 2----\n");
            int size = holes*2 + 2;
            //prints player 2 holes
            writer.write(" ");

            for(int i = size-1; i >= holes+2; i--)
            {
                writer.write(""+board[i]+" ");
            }
            writer.write("\n");
            writer.write(""+board[0]);

            //Prints both players score
            for(int i = 0; i < holes; i++)
            {
                writer.write("  ");
            }
            writer.write(""+board[holes+1]);
            writer.write("\n");
            writer.write(" ");

            //prints player 1 holes
            for(int i = 1; i <= holes; i++)
            {
                writer.write(""+board[i]+" ");
            }
            writer.write("\n");
            writer.write("----player 1----\n\n");
            
            writer.flush();
            writer.close();
        } 
        catch (IOException e)
        {
           // do something
        }
    }
}