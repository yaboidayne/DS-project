
public class MazePos {

    int i,j;                   // cell position
    
    public MazePos(int i, int j){
     this.i=i;
     this.j=j;
    };
    public int i() { return i;}   // get i (for overloaded methods)

    public int j() { return j;}   // get j (for overloaded methods)
    
    public void Print(){
       System.out.println("(" + i + "," + j + ")");  //print the position
    }
    
    // go up
    public MazePos north(){
      return new MazePos(i-1,j);
    }
    
    //go down
    public MazePos south(){
        return new MazePos(i+1 , j);
    }
    
    //go right
    public MazePos east(){
        return new MazePos(i,j+1);
    }
    
    //go left
    public MazePos west(){
      return new MazePos(i,j-1);
    }
    
}
