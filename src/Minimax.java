import java.util.StringTokenizer;

public class Minimax
{
    public static boolean gameOver = false;
    public static boolean endGame = true;

    public static void main(String[] args)
    {
        GameState g = new GameState(6,4);
        long startTime = System.currentTimeMillis();
        while(!gameOver)
        {
            treeHelper(g, 6, endGame);
            //g.printGameState();
            calcMinMax(g, endGame);
            //printBestChoice(g);
            printTurn(g);
            if(!gameOver)
            {
                g = setNewGameState(g);
            }
            //  g.printGameState();

            //   printTree(g);
            
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total run time : " + totalTime + " miliseconds");
    }
    
    /*parses turn sequence for values. to be used later*/
    public static String parseTurnSequence(String turnSequence, boolean player) 
    {
    	String turn = "";
    	String token = "";
    	StringTokenizer st = new StringTokenizer(turnSequence);
    	while(st.hasMoreTokens()) 
    	{
    		token = st.nextToken();
    		if(player)
    		{
    			if(token.substring(0,3).equals("P1:"))
    			{
    				token = token.substring(3, token.length());
    			}
    		}
    		else
    		{
    			if(token.substring(0,3).equals("P2:"))
    			{
    				token = token.substring(3, token.length());
    			}
    		}
    		turn += token + " ";
    	}
    	return turn;
    }
    
    /*set new game state and calculate more depth of min max */
    public static GameState setNewGameState(GameState root)
    {
        GameState gs = root;
        GameState newGameState = root;
        
        while(gs != null)
        {
            newGameState = gs;
            gs = gs.getNextChoice();
        }
        return newGameState;
    }
    
    /*populates min and max value. Player 1 is max and player 2 is min*/
    public static void calcMinMax(GameState root,boolean minOrMax)
    {
/*            if(root.next.isEmpty()){
             root.setMinMax(root.getScore());
             return;
         }
         for(int i =0; i<root.next.size(); i++){
             calcMinMax(root.next.get(i),!minOrMax);
         }
         if(minOrMax){
             //System.out.println("max");
             root.setMinMax(root.getMax());
         }
         else{
             //System.out.println("min");
             root.setMinMax(root.getMin());
         }   */
         alphaBetaPruning(root, Integer.MIN_VALUE, Integer.MAX_VALUE, minOrMax);
    }
    
    //alpha beta pruning
    public static int alphaBetaPruning(GameState root, int alpha, int beta, boolean player)
    {
        if(root.next.isEmpty())
        {
            root.setMinMax(root.getScore());
            return root.getScore();
        }
        //Max player
        if(player)
        {
            root.setV(Integer.MIN_VALUE);
            for(GameState g : root.next)
            {
                root.setV(Math.max(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));                
                alpha = Math.max(alpha, root.getV());
                if(beta <= alpha)
                {
                    break;
                }
            }
            root.setMinMax(root.getV());
            for(GameState g : root.next)
            {
                 if(root.getV() == g.getMinMax())
                 {
                     root.setNextChoice(g);
                     if(g.getEndGame())
                     {
                         gameOver = true;
                     }
                     break;
                 }
            }  
            return root.getV();  
        }
        //Min player
        else {
            root.setV(Integer.MAX_VALUE);
            for(GameState g: root.next)
            {
                root.setV(Math.min(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));                
                beta = Math.min(beta, root.getV());
                if(beta <= alpha)
                {
                    break;
                }
            }
            root.setMinMax(root.getV());
            for(GameState g : root.next)
            {
                 if(root.getV() == g.getMinMax())
                 {
                     root.setNextChoice(g);
                     if(g.getEndGame())
                     {
                         gameOver = true;
                     }
                     break;
                 }
            } 
            return root.getV();
        }
    }

    //calculates the possible next moves
    public static void buildTree(GameState current, GameState prev,boolean player)
    {
        for(int i = 1; i <= current.getHoles(); i++)
        {
            GameState r = new GameState(current);
            
            int extraMove = r.turn(i, player, endGame, current.getTurnSequence());
            if(extraMove == 0)
            {
                buildTree(r, prev, player);
            }
            else if(extraMove == 1)
            {
                prev.next.add(r);
            }
        }
    }
    
    //creates a tree of all possible moves
    public static void treeHelper(GameState root,int depth,boolean player)
    {
        if(depth == 0)
            return;
        if(root.next.isEmpty())
        {
            buildTree(root, root, player);
        }
        for(GameState a : root.next)
        {
            //buildTree(a,a,player);
            treeHelper(a, depth-1, !player);
        }   
    }
    
    //prints turn sequence
    public static void printTurn(GameState g)
    {
    	GameState gs = g;
    	while(gs != null)
    	{
    		gs.printTurnSequence();
    		gs = gs.getNextChoice();
    	}
    }
    
    //prints best choice
    public static void printBestChoice(GameState g)
    {
        GameState gs = g;
    
        while(gs != null)
        {
            gs.printGameState();
            gs = gs.getNextChoice();
        }
    }
    
    //prints whole tree
    public static void printTree(GameState g)
    {
        if(g.next.isEmpty())
        {
            return;
        }
        for(GameState a : g.next)
        {
            //System.out.println("Min_Max: "+ a.getMinMax());
            a.printGameState();
        }
        for(GameState a : g.next)
        {
            printTree(a);
        }
    }
}