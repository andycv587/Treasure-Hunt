/**
 * (C) Copyright IBM Corp. 2016,2022. All Rights Reserved. US Government Users Restricted Rights - Use,
 * duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.*;
import java.awt.Point;
import com.ibm.vie.mazerunner.squares.*;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.SECONDS;


import com.ibm.vie.mazerunner.IPlayer;
import com.ibm.vie.mazerunner.IAnalysisBoard;
import com.ibm.vie.mazerunner.squares.ISquare;
import com.ibm.vie.mazerunner.Location;
import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.Move;
import com.ibm.vie.mazerunner.MyTreasureHunt;

/**
 * This class is your implementation of a player who
 * searches a board for treasure. Your goal is to do so
 * using the fewest number of steps you can.
 */
public class MyPlayer implements IPlayer {
  public Stack<Move> moves=new Stack<Move>();//move need to rea
  public Vector<Point> treasures=new Vector<Point>();//treasure indexs
  public boolean isnotfirstpt,reached,isMud,isWater,isBushes,isTrees,isMountain,isLava=isMountain=isTrees=isBushes=isWater=isMud=reached=isnotfirstpt=false;
  public Point StartPoint=new Point();
  public Point OriginalStartPoint=new Point();
  public int time=0, treasus=0;
  public int[] scoreboard=new int[7];
  public String[] pathes=new String[7];
  public String[][] maze0, orginalboard = maze0 = new String[5][5];
    /**
     * Gives the board, and the IBM judges a way to identify
     * your player, and differentiate it from other student submissions.
     *
     * @return Your name
     */
    public String getName() {
        return "Shuheng Cao - Century";// your name here;
    }

    /**
     * In the first phase of the game, your player can examine the board,
     * see where the treasure is located and make a strategy to
     * find all the treasure in the fewest possible steps.
     * <p>
     * Since this method is called every time a board is loaded, use
     * this method to reset any data kept from a previous board during the
     * analyze or selectMove phases. The same instance of your player
     * is used to play every time, and not resetting your data
     * from a previous board could cause unexpected results as
     * you play the next one.
     * </p>
     *
     * @param board The game board for your player to analyze
     */
    public void analyzeBoard(IAnalysisBoard board) {
      String path="";
      generateboard(board);
      
      int treass, muds, waters, trees, bushes, mountains, lavas=mountains=bushes=trees=waters=muds=treass=0;
      for(int i=0;i<orginalboard.length;i++){
        for(int j=0;j<orginalboard[0].length;j++){
          String str=orginalboard[i][j];
          switch(str){
            case "T":
              treass+=1;
              break;
            case "-":
              muds+=1;
              break;
            case "~":
              waters+=1;
              break;
            case "|":
              trees+=1;
              break;
            case "%":
              bushes+=1;
              break;
            case "^":
              mountains+=1;
              break;
            case "*":
              lavas+=1;
              break;
          }
        }
      }
      
      System.out.println("you are starting at "+playerlocation());
      System.out.println("you have total "+treass+" treasures on this board");
      System.out.println("you have total "+trees+" tree square on this board");
      System.out.println("you have total "+muds+" mud squares on this board");
      System.out.println("you have total "+waters+" water squares on this board");
      System.out.println("you have total "+bushes+" bush squares on this board");
      System.out.println("you have total "+mountains+" mountain squares on this board");
      System.out.println("you have total "+lavas+" lava squares on this board");
      ///////////////////////////////Boundary line//////////////////////////////////////////
      
      int executetime=this.executiontime();
      
      try{
        int mode=0;
        while(mode<executetime && time==0){
          if(time==0){
            changeboard(mode,board);
 /////////////////////////////testing portion//////////////////////////////
//            System.out.println("level: "+mode);
//            for(int i=0;i<maze0.length;i++){
//              for(int j=0;j<maze0[i].length;j++){
//                System.out.print(maze0[i][j]+" ");
//              }
//              System.out.println();
//            }
 //////////////////////////////////////////////////////////////
            this.endPts();
            if(isnotfirstpt==false){
              this.startPt();
              isnotfirstpt=false;
            }
            
            
            for(int i=0;i<treasures.size();i++){
              //if it has multi treasures, it will change position
              if(treasures.size()>1 && i!=0){
                // you have to change "S" to previous "T", and plugin new "T"
                maze0[StartPoint.x][StartPoint.y]="S";
                maze0[treasures.get(i).x][treasures.get(i).y]="T";
              }else if(i==0){
                this.only(treasures.get(i));
                OriginalStartPoint=new Point(StartPoint.x,StartPoint.y);
              }
              
              String pa=this.findPathFrom(StartPoint.x,StartPoint.y);
//              System.out.println(pa);
              if(pa.equals("")==false){
                path+=pa.substring(0,pa.length());
                
                if(i!=0 || i==treasures.size()-1){
                  treasus=i+1;
                  if(i==treasures.size()-1){
                    reached=true;
                  }
                }
              }
              if(treasures.size()>1){
                maze0[StartPoint.x][StartPoint.y]=" ";
                StartPoint=treasures.get(i);
              }
            }
            pathes[mode]=path;
            path="";
          }
          
          scoreboard[mode]=countscore(pathes[mode],OriginalStartPoint,treasus,board.getMaxSteps(),reached);
          mode+=1;
        }
        ///////////////Testing section///////////////////////////
//        System.out.println(executetime);
//        System.out.println(path);
//        System.out.println("the path:"+path.length());
//        for(int i=0;i<scoreboard.length;i++){
//          System.out.println(i+"="+scoreboard[i]);
//        }
//        for(int i=0;i<pathes.length;i++){
//          System.out.println(i+"="+pathes[i]);
//        }
        ////////////////////////////////////////////////////
        
        int idx=0;
        for(int i=0;i<scoreboard.length;i++){
          if(scoreboard[i]>scoreboard[idx])
            idx=i;
        }
        
        
        this.setMoves(pathes[idx]);
        time+=1;
      }catch(Exception e){
        reset();
        gameCompleted(board);
      }
      
      
      
    }
    
//    public String[][] replace(String[][] board, int targetx, int targety, String target2){
//      String[][] newboard=new String[board.length][board[0].length];
//      for(int i=0;i<board.length;i++){
//        for(int j=0;j<board[i].length;j++){
//          newboard[i][j]=board[i][j];
//          if(i==targetx && j==targety){
//            newboard[i][j]=target2;
//          }
//        }
//      }
//      return newboard;
//    }
    

    /**
     * In the second phase of the game, your player moves about
     * the board to claim the treasures. This method will be called
     * repeatedly until one of the conditions are met for a board:
     * <ul>
     * <li>All treasures are found</li>
     * <li>The maximum number of steps has been reached</li>
     * </ul>
     * <p>
     * A default implementation is provided to show you how to
     * interact with the board.
     * </p>
     *
     * @param board The game board for your player to move about
     *
     * @return Your next move
     */
    public Move selectMove(IBoard board) throws Error {
      if(moves.size()>1){
//        System.out.println(moves.size());
        return moves.pop();
      }else if(moves.size()==1){
          Move mov=moves.pop();
          moves=new Stack<Move>();
          this.reset();
          return mov;
        
      }
        return Move.SOUTH;
    }
    
    public String playerlocation(){
      String num="(";
      for(int i=0;i<orginalboard.length;i++) {
        for(int j=0;j<orginalboard[i].length;j++){
          if(orginalboard[i][j].equals("S")){
            num+=i+","+j+")";
          }
        }
      }
      return num;
    }
    
    /**
     * The gameCompleted method is called when the game is over and
     * no further moves are required of your player for this board.
     * <p>
     * This may be called under various circumstances, but will always
     * be called for each board:
     * - If you run out of available steps
     * - If you've obtained the last available treasure (Congratulations!)
     * - Your analyzeBoard or selectMove implementation took too long to execute.
     * </p>
     *
     * @param board The game board for your player to analyze
     */
    public void gameCompleted(IBoard board) {
      if(!board.isComplete()){
        System.out.println("gged");
        reset();
      }
    }
    
    /*
     * 
     */
    public void reset(){
      this.moves=new Stack<Move>();//move need to rea
      this.treasures=new Vector<Point>();//treasure indexs
      this.reached=false;
      this.isnotfirstpt=false;
      this.isMud=false;
      this.isWater=false;
      this.isBushes=false;
      this.isTrees=false;
      this.isMountain=false;
      this.isLava=false;
      this.time=0;
      this.treasus=0;
      this.scoreboard=new int[7];
      this.pathes=new String[7];
      this.maze0 = new String[5][5];
      this.orginalboard = new String[5][5];
      this.OriginalStartPoint=new Point();
      this.StartPoint=new Point();

    }
    
    /*
     * 
     */
    public int executiontime(){
      for(int i=0;i<orginalboard.length;i++){
        for(int j=0;j<orginalboard[i].length;j++){
          String crt=orginalboard[i][j];
          switch(crt){
            case "~":
              isWater=true;
              break;
            case "-":
              isMud=true;
              break;
            case "×":
              isLava=true;
              break;
            case "^":
              isMountain=true;
              break;
            case "|":
              isTrees=true;
              break;
            case "%":
              isBushes=true;
              break;
          }
        }
      }
       int executetime=1;
       if(isMud==true)
         executetime+=1;
       if(isWater==true)
         executetime+=1;
       if(isBushes==true)
         executetime+=1;
       if(isTrees==true)
         executetime+=1;
       if(isMountain==true)
         executetime+=1;
       if(isLava==true)
         executetime+=1;
       return executetime;
    }

    
    public void only(Point treas){
      for(int i=0;i<maze0.length;i++){
        for(int j=0;j<maze0[i].length;j++){
          if(maze0[i][j].equals("T"))
            maze0[i][j]=" ";
        }
      }
      maze0[treas.x][treas.y]="T";
    }
    
    
    
    public String[][] reasign(String[][] var){
      String[][] arr=new String[var.length][var[0].length];
      for(int i=0;i<var.length;i++){
        for(int j=0;j<var[i].length;j++){
          arr[i][j]=var[i][j];
        }
      }
      return arr;
    }
    
    /**
     * lvl 0 = only space
     * lvl 1 = space and mud
     * lvl 2 = space and mud and water
     * lvl 3 = space and mud and water and bushes
     * lvl 4 = space and mud and water and bushes and Trees
     * lvl 5 = space and mud and water and bushes and Trees and mountains
     * lvl 6 = space and mud and water and bushes and Trees and mountains and lavas
     */
    public void generateboard(IBoard board){
      String[][] bd=new String[board.getHeight()][board.getWidth()];
      for(int i=0;i<bd.length;i++){
        for(int j=0;j<bd[i].length;j++){
          String block=board.getSquareAt(new Location(i,j)).getTypeString();
          switch(block){
            case "Wall":
              bd[i][j]="#";
              break;
            case "Space":
              bd[i][j]=" ";
              break;
            case "Treasure":
              bd[i][j]="T";
              break;
            case "Water":
              bd[i][j]="~";
              break;
            case "Mud":
              bd[i][j]="-";
              break;
            case "Lava":
              bd[i][j]="*";
              break;
            case "Mountain":
              bd[i][j]="^";
              break;
            case "Trees":
              bd[i][j]="|";
              break;
            case "Bushes":
              bd[i][j]="%";
              break;
          }
        }
      }
      orginalboard=bd;
      Location loc=board.getStartingLocation();
      orginalboard[loc.getRow()][loc.getCol()]="S";
    }
    
    public void setMoves(String move){
      for(int i=move.length()-1;i>=0;i--){
        char letter=move.charAt(i);
        switch(letter){
          case 'U':
            this.moves.add(Move.NORTH);
            break;
          case 'D':
            this.moves.add(Move.SOUTH);
            break;
          case 'L':
            this.moves.add(Move.WEST);
            break;
          case 'R':
            this.moves.add(Move.EAST);
            break;
        }
      }
    }
    
    public void startPt(){
      Point start=new Point(0,0);
      for(int i=0;i<orginalboard.length;i++){
        for(int j=0;j<orginalboard[i].length;j++){
          if(orginalboard[i][j].equals("S"))
            start=new Point(i,j);
        }
      }
      this.StartPoint=start;
    }
    
    public void endPts(){
      treasures=new Vector<Point>();
      for(int i=0;i<orginalboard.length;i++){
        for(int j=0;j<orginalboard[i].length;j++){
          if(orginalboard[i][j].equals("T"))
            treasures.add(new Point(i,j));
        }
      }
    }
    
    public String findPathFrom(int row, int col){
      String ans="";
      Maze mz=new Maze(this.maze0);
      Vector<int[]> pts=mz.getPath();
      for(int i=0;i<pts.size()-1;i++){
        int stx,sty,edx,edy;
        
        stx=pts.get(i)[0];
        sty=pts.get(i)[1];
        edx=pts.get(i+1)[0];
        edy=pts.get(i+1)[1];
        
        if(edx-stx>0){
          ans+="D";
        }else if(edx-stx<0){
          ans+="U";
        }else if(edy-sty>0){
          ans+="R";
        }else if(edy-sty<0){
          ans+="L";
        }
      }
      
      
      return ans;
    }

    public int countscore(String Path, Point startPoint, int treas, int max, boolean ifcompleted){
//      System.out.println("start point is at ("+startPoint.x+","+startPoint.y+")");
      if(Path.equals(""))
        return 0;
      
      int crtx=startPoint.x;
      int crty=startPoint.y;
      if(treas==0)
        return 0;
      
      int score=max;
      for(int i=0;i<Path.length();i++){
        char le=Path.charAt(i);
        switch(le){
          case 'U':
            crtx-=1;
            score=calc(orginalboard[crtx][crty],score);
            break;
          case 'D':
            crtx+=1;
            score=calc(orginalboard[crtx][crty],score);
            break;
          case 'L':
            crty-=1;
            score=calc(orginalboard[crtx][crty],score);
            break;
          case 'R':
            crty+=1;
            score=calc(orginalboard[crtx][crty],score);
            break;
        }
      }
//      System.out.println("reach if completed statement");
      if(ifcompleted){
        score+=500;
      }
      score+=treas*100;
      treasus=0;
      reached=false;
      return score;
    }
    
    
    /*BELOW HAS NO PROBLEM*/
    public int calc(String type, int score){
            switch(type){
              case " ":
                score-=1;
                break;
              case "-":
                score-=5;
                break;
              case "~":
                score-=13;
                break;
              case "%":
                score-=23;
                break;
              case "|":
                score-=37;
                break;
              case "^":
                score-=47;
                break;
              case "*":
                score-=61;
                break;
              case "#":
                score=0;
                break;
            }
          return score;
    }
    
    
    public void changeboard(int level,IBoard board){
      String[][] nbd=reasign(orginalboard);
      for(int i=0;i<nbd.length;i++){
        for(int j=0;j<nbd[i].length;j++){
          if(level==0){
            if(!nbd[i][j].equals(" ")){
              if((!(nbd[i][j].equals("T"))&&(!nbd[i][j].equals(" "))))
                nbd[i][j]="#";
            }
          }else if(level==1){
            if(nbd[i][j].equals(" ")==false && nbd[i][j].equals("-")==false){
              if((!nbd[i][j].equals("T")))
                nbd[i][j]="#";
            }
            
            if(nbd[i][j].equals("-"))
              nbd[i][j]=" ";
                  
            
          }else if(level==2){
            if(nbd[i][j].equals(" ")==false && nbd[i][j].equals("-")==false && nbd[i][j].equals("~")==false){
              if(!nbd[i][j].equals("T"))
                nbd[i][j]="#";
            }  
            if(nbd[i][j].equals("-") || nbd[i][j].equals("~"))
              nbd[i][j]=" ";
                              
          }else if(level==3){
            if(nbd[i][j].equals(" ")==false && nbd[i][j].equals("-")==false && nbd[i][j].equals("~")==false && nbd[i][j].equals("%")==false){
              if(!nbd[i][j].equals("T"))
                nbd[i][j]="#";
            }
            
            if(nbd[i][j].equals("-") || nbd[i][j].equals("~") || nbd[i][j].equals("%"))
              nbd[i][j]=" ";
               
          }else if(level==4){
            if(nbd[i][j].equals(" ") == false && nbd[i][j].equals("-") == false && nbd[i][j].equals("~") == false && nbd[i][j].equals("%") == false && nbd[i][j].equals("|") == false){
              if(!nbd[i][j].equals("T"))
                nbd[i][j]="#";
            }
            if(nbd[i][j].equals("-") || nbd[i][j].equals("~") || nbd[i][j].equals("%") || nbd[i][j].equals("|"))
              nbd[i][j]=" ";
               
          }else if(level==5){
            if(nbd[i][j].equals(" ") == false && nbd[i][j].equals("-") == false && nbd[i][j].equals("~") == false && nbd[i][j].equals("%") == false && nbd[i][j].equals("|") == false && nbd[i][j].equals("^") == false){
              if(!nbd[i][j].equals("T"))
                nbd[i][j]="#";
            }
            if(nbd[i][j].equals("-") || nbd[i][j].equals("~") || nbd[i][j].equals("%") || nbd[i][j].equals("|") || nbd[i][j].equals("^"))
              nbd[i][j]=" ";
               
          }else if(level==6){
            if(nbd[i][j].equals(" ") == false && nbd[i][j].equals("-") == false && nbd[i][j].equals("~") == false && nbd[i][j].equals("%") == false && nbd[i][j].equals("|") == false && nbd[i][j].equals("^") == false && nbd[i][j].equals("*") == false){
              if(!nbd[i][j].equals("T"))
                nbd[i][j]="#";
            }
            if(nbd[i][j].equals("-") || nbd[i][j].equals("~") || nbd[i][j].equals("%") || nbd[i][j].equals("|") || nbd[i][j].equals("^") || nbd[i][j].equals("*"))
              nbd[i][j]=" ";
               
          }
        }
      }
      maze0=nbd;        
      Location loc=board.getStartingLocation();
      nbd[loc.getRow()][loc.getCol()]="S";
    }
    /**
     * Game launcher. Create an instance of your player class and
     * run the game with it.
     *
     * @param args
     */
    public static void main(String args[]) {
        MyTreasureHunt.run(new MyPlayer());
    }
}


class Maze {

    public int endx, endy, startx, starty = startx = endy = endx = 0;
    public int[][] arr;

    private class Point {

        int x;
        int y;
        Point parent;

        public Point(int x, int y, Point parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        public Point getParent() {
            return this.parent;
        }

        public String toString() {
            return "x = " + x + " y = " + y;
        }
    }

    public Maze(String[][] maze) {
        this.arr = new int[maze.length][maze[0].length];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j].equals("#")) {
                    arr[i][j] = 5;
                } else if (maze[i][j].equals("T")) {
                    arr[i][j] = 9;
                    this.endx=i;
                    this.endy=j;
                } else {
                    arr[i][j] = 0;
                }

                if (maze[i][j].equals("S")) {
                    this.startx = i;
                    this.starty = j;
                }
            }
        }

    }

    public Queue<Point> q = new LinkedList<Point>();

    public Point getPathBFS(int x, int y) {

        q.add(new Point(x, y, null));

        while (!q.isEmpty()) {
            Point p = q.remove();

            if (arr[p.x][p.y] == 9) {
//                System.out.println("Exit is reached!");
                return p;
            }

            if (isFree(p.x + 1, p.y)) {
                arr[p.x][p.y] = -1;
                Point nextP = new Point(p.x + 1, p.y, p);
                q.add(nextP);
            }

            if (isFree(p.x - 1, p.y)) {
                arr[p.x][p.y] = -1;
                Point nextP = new Point(p.x - 1, p.y, p);
                q.add(nextP);
            }

            if (isFree(p.x, p.y + 1)) {
                arr[p.x][p.y] = -1;
                Point nextP = new Point(p.x, p.y + 1, p);
                q.add(nextP);
            }

            if (isFree(p.x, p.y - 1)) {
                arr[p.x][p.y] = -1;
                Point nextP = new Point(p.x, p.y - 1, p);
                q.add(nextP);
            }

        }
        return null;
    }

    public boolean isFree(int x, int y) {
        if ((x >= 0 && x < arr.length) && (y >= 0 && y < arr[x].length) && (arr[x][y] == 0 || arr[x][y] == 9)) {
            return true;
        }
        return false;
    }

    public Vector<int[]> getPath(){
      Vector<int[]> vc=new Vector<int[]>();
      try{
        Point p = getPathBFS(startx,starty);
        Vector<int[]> vec=new Vector<int[]>();
        int end[]={endx,endy};
        vec.add(end);
        while(p.getParent() != null) {
            int cod[]={p.x,p.y};
            vec.add(cod);
            p = p.getParent();
        }
        int start[]={startx,starty};
        vec.add(start);
        
        //reverse adding
        for(int i=vec.size()-1;i>=0;i--){
          vc.add(vec.get(i));
        }
      }catch(java.lang.NullPointerException e){
        System.out.println("Null Pointer Exception for getPath()");
      }
        return vc;
    }
    

}