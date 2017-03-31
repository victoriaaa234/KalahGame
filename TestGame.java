import java.util.*;
public class TestGame{

    public static void main(String[] args){
        GameState g = new GameState(6,4);
        long startTime = System.currentTimeMillis();

        treeHelper(g,6,true);
        //g.printGameState();
        System.out.println("Printing Tree");
        calcMinMax(g,true);
    //  g.printGameState();

        //printTree(g);
        //print(g);
        //need to figure out nextbestChoice
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total run time : "+totalTime+" miliseconds");
    }
    
    /*populates min and max value. Player 1 is max and player 2 is min*/
    public static void calcMinMax(GameState root,boolean minOrMax){
        // if(root.next.isEmpty()){
        //     root.setMinMax(root.getScore());
        //     return;
        // }
        // for(int i =0; i<root.next.size(); i++){
        //     calcMinMax(root.next.get(i),!minOrMax);
        // }
        // if(minOrMax){
        //     //System.out.println("max");
        //     root.setMinMax(root.getMax());
        // }
        // else{
        //     //System.out.println("min");
        //     root.setMinMax(root.getMin());
        // }
        System.out.println(alphaBetaPruning(root, Integer.MIN_VALUE, Integer.MAX_VALUE, minOrMax));
    }
    
    //alpha beta pruning
    public static int alphaBetaPruning(GameState root, int alpha, int beta, boolean player){
        if(root.next.isEmpty()) {
            root.setMinMax(root.getScore());
            //System.out.println("leaf:" + root.getMinMax());
            return root.getScore();
        }
        //Max player
        if(player) {
            root.setV(Integer.MIN_VALUE);
            for(GameState g : root.next) {
                root.setV(Math.max(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));
                /* TODO: Set the next best choice */
                // if(root.getV() == g.getMinMax()) {
                //     root.setNextChoice(g);
                // }
                alpha = Math.max(alpha, root.getV());
                if(beta <= alpha) {
                    break;
                }
            }
            root.setMinMax(root.getV());
            //System.out.println("Max:" + root.getMinMax());
            return root.getV();  
        }
        else { //Min player
            root.setV(Integer.MAX_VALUE);
            for(GameState g: root.next) {
                root.setV(Math.min(root.getV(), alphaBetaPruning(g, alpha, beta, !player)));
                /* TODO: Set the next best choice */
                // if(root.getV() == g.getMinMax()) {
                //     root.setNextChoice(g);
                // }
                beta = Math.min(beta, root.getV());
                if(beta <= alpha) {
                    break;
                }
            }
            root.setMinMax(root.getV());
            //System.out.println("Min:" + root.getMinMax());
            return root.getV();
        }
    }

    //calculates the possible next moves
    public static void buildTree(GameState current, GameState prev,boolean player){
        for(int i =1; i<=6; i++){
            GameState r = new GameState(current);
            
            int extraMove=r.turn(i,player,current.getTurnSequence());
            if(extraMove==0){
                buildTree(r,prev,player);
            }
            else if(extraMove ==1){
                prev.next.add(r);
            }
        }
    }
    
    //creates a tree of all possible moves
    public static void treeHelper(GameState root,int depth,boolean player){
        if(depth ==0)
            return;
        if(root.next.isEmpty()){
            buildTree(root,root,player);
        }
        for(GameState a : root.next){
            //buildTree(a,a,player);
            treeHelper(a,depth-1,!player);
        }   
    }
    
    //print one level
    public static void print(GameState g){
        GameState gs = g;
    
        while(gs != null){
            gs.printGameState();
            gs = gs.getNextChoice();
        }
    }
    
    //prints whole tree
    public static void printTree(GameState g){
        if(g.next.isEmpty()){
            return;
        }
        for(GameState a : g.next){
            //System.out.println("Min_Max: "+ a.getMinMax());
            a.printGameState();
        }
        for(GameState a : g.next){
            printTree(a);
        }
    }
}