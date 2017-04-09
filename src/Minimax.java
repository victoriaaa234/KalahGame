import java.util.StringTokenizer;

public class Minimax
{
    public static boolean gameOver = false;
    public static boolean endGame = true;

    public static void main(String[] args)
    {
        GameState g = new GameState(6,4);
        long startTime = System.currentTimeMillis();
        while (!gameOver)
        {
            treeHelper(g, 5, endGame);
            calcMinMax(g, endGame);
            printTurn(g);
            if (!gameOver)
            {
                g = setNewGameState(g);
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total run time : " + totalTime + " miliseconds");
    }
    
    public static boolean getGameOverState()
    {
    	return gameOver;
    }
    
    // NOTE(Suraj): If AI is player 2: Returns true if AI should steal, returns false if doesn't need to
    // If AI is player 1: Pick a GameState thats not the worst or the best
    public static boolean pieRuleSteal(GameState opponentMove, GameState bestMove, boolean player)
    {
    	if (!player)
    	{
    		return opponentMove.equals(bestMove);
    	}
    	return false;
    }
    
    // NOTE(Victoria): Parses turn sequence for values
    public static String parseTurnSequence(String turnSequence) 
    {
    	if (turnSequence == null)
    	{
    		return "";
    	}
    	StringBuilder builder = new StringBuilder(turnSequence.length() + 1);
    	String[] words = turnSequence.split(" ");
    	for (int i = words.length - 1; i >= 0; --i)
    	{
    		builder.append(words[i]).append(' ');
    	}
    	builder.setLength(builder.length() - 1);
    	String reversedString = builder.toString();
    	
    	StringTokenizer st = new StringTokenizer(reversedString);
    	String token = "";
    	String turn = "";
    	String key = "";
    	int iter = 0;
    	while (st.hasMoreTokens())
    	{
    		token = st.nextToken();
    		if (iter == 0 && token.substring(0,3).equals("P1:"))
    		{
    			key = "P1:";
    			iter = 1;
    		}
    		else if (iter == 0 && token.substring(0,3).equals("P2:"))
    		{
    			key = "P2:";
    			iter = 1;
    		}
    		if (!key.equals(token.substring(0,3)))
    		{
    			break;
    		}
    		else
    		{
    			turn += token.substring(3) + " ";
    		}
    	}
        turn = turn.substring(0, turn.length() -1);
        
    	StringBuilder finalStringBuilder = new StringBuilder(turn.length() + 1);
    	String[] finalWords = turn.split(" ");
    	int[] finalIntegers = new int[finalWords.length];
    	for (int i = 0; i < finalWords.length; ++i)
    	{
    	    finalIntegers[i] = Integer.parseInt(finalWords[i]);
    	}
    	for (int i = 0; i < finalIntegers.length; ++i)
    	{
    		finalIntegers[i] = finalIntegers[i] - 1;
    	}
    	for (int i = 0; i < finalIntegers.length; ++i)
    	{
    		finalWords[i] = Integer.toString(finalIntegers[i]);
    	}
    	for (int i = finalWords.length - 1; i >= 0; --i)
    	{
    		finalStringBuilder.append(finalWords[i]).append(' ');
    	}
    	finalStringBuilder.setLength(finalStringBuilder.length() - 1);
    	String finalString = finalStringBuilder.toString();
        return finalString;
    }
    
    // NOTE(Victoria): Set new GameState and calculate more depth of Minimax
    public static GameState setNewGameState(GameState root)
    {
        GameState gs = root;
        GameState newGameState = root;
        
        while (gs != null)
        {
            newGameState = gs;
            gs = gs.getNextChoice();
        }
        return newGameState;
    }
    
    // NOTE(Suraj): Populates min and max value. Player 1 is max and player 2 is min
    public static void calcMinMax(GameState root, boolean minOrMax)
    {
    	//NOTE(Suraj): Basic Minimax tree
    	/*
         if (root.next.isEmpty())
         {
             root.setMinMax(root.getScore());
             return;
         }
         for (int i = 0; i < root.next.size(); ++i)
         {
             calcMinMax(root.next.get(i),!minOrMax);
         }
         if (minOrMax)
         {
             root.setMinMax(root.getMax());
         }
         else
         {
             root.setMinMax(root.getMin());
         }
         */
         alphaBetaPruning(root, Integer.MIN_VALUE, Integer.MAX_VALUE, minOrMax);
    }
    
    // NOTE(Victoria): Alpha beta pruning
    public static int alphaBetaPruning(GameState root, int alpha, int beta, boolean player)
    {
        if (root.next.isEmpty())
        {
            root.setMinMax(root.getScore());
            return root.getScore();
        }
        // NOTE(Victoria): Max player
        if (player)
        {
            root.setV(Integer.MIN_VALUE);
            for (GameState g : root.next)
            {
                root.setV(Math.max(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));                
                alpha = Math.max(alpha, root.getV());
                if (beta <= alpha)
                {
                    break;
                }
            }
            root.setMinMax(root.getV());
            for (GameState g : root.next)
            {
                 if (root.getV() == g.getMinMax())
                 {
                     root.setNextChoice(g);
                     if (g.getEndGame())
                     {
                         gameOver = true;
                     }
                     break;
                 }
            }  
            return root.getV();  
        }
        // NOTE(Victoria): Min player
        else
        {
            root.setV(Integer.MAX_VALUE);
            for (GameState g: root.next)
            {
                root.setV(Math.min(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));                
                beta = Math.min(beta, root.getV());
                if (beta <= alpha)
                {
                    break;
                }
            }
            root.setMinMax(root.getV());
            for (GameState g : root.next)
            {
                 if (root.getV() == g.getMinMax())
                 {
                     root.setNextChoice(g);
                     if (g.getEndGame())
                     {
                         gameOver = true;
                     }
                     break;
                 }
            } 
            return root.getV();
        }
    }

    // NOTE(Suraj): Calculates the possible next moves
    public static void buildTree(GameState current, GameState prev, boolean player)
    {
        for (int i = 1; i <= current.getHoles(); ++i)
        {
            GameState r = new GameState(current);
            
            int extraMove = r.turn(i, player, endGame, current.getTurnSequence());
            if (extraMove == 0)
            {
                buildTree(r, prev, player);
            }
            else if (extraMove == 1)
            {
                prev.next.add(r);
            }
        }
    }
    
    // NOTE(Suraj): Creates a tree of all possible moves
    public static void treeHelper(GameState root, int depth, boolean player)
    {
        if (depth == 0)
        	return;
        if (root.next.isEmpty())
        {
            buildTree(root, root, player);
        }
        for (GameState a : root.next)
        {
            treeHelper(a, depth-1, !player);
        }   
    }
    
    // NOTE(Victoria): Prints turn sequence
    public static void printTurn(GameState g)
    {
    	GameState gs = g;
    	while (gs != null)
    	{
    		gs.printTurnSequence();
    		System.out.println("TURN:" + parseTurnSequence(gs.getTurnSequence()));
    		gs = gs.getNextChoice();
    	}
    }
    
    // NOTE(Suraj): Prints best choice
    public static void printBestChoice(GameState g)
    {
        GameState gs = g;
    
        while (gs != null)
        {
            gs.printGameState();
            gs = gs.getNextChoice();
        }
    }
    
    // NOTE(Suraj): Prints whole tree
    public static void printTree(GameState g)
    {
        if (g.next.isEmpty())
        {
            return;
        }
        for (GameState a : g.next)
        {
            a.printGameState();
        }
        for (GameState a : g.next)
        {
            printTree(a);
        }
    }
}