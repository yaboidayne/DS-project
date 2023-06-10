import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Maze extends JFrame {
    //assign numbers for every color that will be used, colors are defined later (line:349 in paint)
    //block (black square)
    final static int X = 1;
    //free space   (white Square)
    final static int C = 0;
    //initial state
    final static int S = 2;
    //goal
    final static int E = 8;
    // the path
    final static int V = 9;

    //initial state   (i,j)
    final static int START_I = 1, START_J = 1;
    //goal  (i,j)
    final static int END_I = 2, END_J = 9;

    int[][] maze = new int[][]{ // the initial array for the maze
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 2, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 1, 0, 1, 1, 0, 8},
        {1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
        {1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
        {1, 1, 1, 1, 0, 1, 0, 0, 0, 1},
        {1, 1, 0, 1, 0, 1, 1, 0, 0, 1},
        {1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 1}
    };

    // for random array which we will store the randomly generated array in.
    int[][] arr;

    // Buttons For GUI (still not initialized)
    JButton solveBFS;
    JButton clear;
    JButton exit;
    JButton genRandom;

    // JLabel and JTextField For GUI (still not initialized)
    JLabel elapsedBFS;
    JTextField textBFS;

    boolean repaint = false;  //(line:349 in paint) the value will define what we want to paint on the Jframe, solve the maze or paint the maze

    // start time
    long startTime;
    //stop time
    long stopTime;
    //calculate the elapsed time
    long duration;
    //time for BFS
    double bfsTime;

   // take copy of the original maze, used when we want to remove (clear) the solution from the JFrame   
   int[][] savedMaze = clone();

    // the maze class constructor, this will be the first thing to be executed when we creat an object from this class
    public Maze() {

        setTitle("Maze");     //Title For JFrame
        setSize(960, 530);    // Size For JFrame  (width,height)
        
        setLocationRelativeTo(null);                                                // to make JFrame appear in the Middle of the screen 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                             // To close the app when click on exit or (X)
        setLayout(null);                                                            // to set the position of the components (like JLabel and JTextField, Buttons) and by hand (we will choose the position by ourselves)

        elapsedBFS = new JLabel("Elapsed Time :");
        textBFS = new JTextField();
        solveBFS = new JButton("Solve BFS");
        clear = new JButton("Clear");
        exit = new JButton("Exit");
        genRandom = new JButton("Generate Random Maze");
      
        add(solveBFS);
        add(clear);
        add(elapsedBFS);
        add(textBFS);
        add(exit);
        add(genRandom);

        // make JFrame visible (it's invisible by default, we don't know why!!)
        setVisible(true);

        // set the positions of the components on the JFrame (x,y,width,height). here we chose the position by hand, this is why we sat the set Layout to null
        solveBFS.setBounds(500, 50, 100, 40);
        clear.setBounds(760, 50, 100, 40);
        exit.setBounds(760, 400, 100, 40);
        genRandom.setBounds(560, 130, 250, 40);
        elapsedBFS.setBounds(630, 35, 100, 40);
        textBFS.setBounds(630, 67, 100, 25);

        // what happen when click on Generate Random Maze Button
        genRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int x[][] = GenerateArray();  // generate random array and store in x 
                repaint = true;//(line:349 in paint) the value will define what we want to paint on the Jframe, solve the maze or paint the maze (in this case "paint the maze")
                restore(x);   // make the array maze equal the array x  || to show the array x on screen
                repaint();   // repaint the maze on the JFrame
            }
        });

        // what happen when click on Exit Button
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);        //Close The App
            }
        });

        // what happen when click on Clear Button
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (arr == null) {       // happens if the random array (resulted when we press random button) is empty (we didn't generate a random array)
                    repaint = true;      // set to repaint maze layout (not maze solution)
                    restore(savedMaze);  // restore the maze to the original, we use a clone() method (line:298 ) to make a copy
                    repaint();           // repaint the maze (savedMaze) on the JFrame
                } else {                 // happens if a random array was generated
                    repaint = true;      // set to repaint maze layout (not maze solution)
                    restore(arr);        // to make the array arr equal to the array maze || to show the array arr on the screen
                    repaint();           // repaint the maze on the JFrame
                }

                textBFS.setText("");       // clear the text in JTextField, the one indicating the elapsed time of BFS 
            }
        });

        // what happen when click on Solve BFS Button
        solveBFS.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (arr == null) {       // happens if the random array (resulted when we press random button) is empty (we didn't generate a random array)
                    restore(savedMaze);  // restore the maze to the original
                    repaint = false;     // set to repaint maze solution (not maze layout)
                    solveQueue();        // call method solveQueue() which solves the maze using BFS
                    repaint();           // repatin the maze on the JFrame
                } else {                 // happens if a random array was generated
                    restore(arr);       // to make the array arr equal to the array maze || to show the array arr on the screen
                    repaint = false;    // set to repaint maze solution (not maze layout)
                    solveQueue();       // call method solveQueue() which solves the maze using BFS
                    repaint();          // repatin the maze on the JFrame
                }

            }
        });

    }

    // get size of the maze
    public int Size() {
        return maze.length;
    }

    // Print the Maze to CL
    public void Print() {
        for (int i = 0; i < Size(); i++) {      //go to every row
            for (int J = 0; J < Size(); J++) {  //go to every element in the row
                System.out.print(maze[i][J]);   //print the element
                System.out.print(' ');          //print space
            }
            System.out.println();               // go to new line
        }
    }

    //return true if cell is within maze 
    public boolean isInMaze(int i, int j) {  //parameters are the position (i and j) of the cell

        if (i >= 0 && i < Size() && j >= 0 && j < Size()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInMaze(MazePos pos) {   //overloaded of isInMaze(int i, int j) , parameter is the node itself
        return isInMaze(pos.i(), pos.j());   //extract the position of the cell (i and j) and call the first method isInMaze(int i, int j)
    }

    // to mark the node in the array with a certain value; ex: if explored mark with value 9 (Green)
    public int mark(int i, int j, int value) {
        assert (isInMaze(i, j));  // it is used for test. if the condition is false it will throw an error named AssertionError.
        int temp = maze[i][j];    // store the original value in temp
        maze[i][j] = value;       // put the value from the parameter in maze cell with corresponding i,j
        return temp;              // return original value
    }

    public int mark(MazePos pos, int value) {   //overloaded of mark(int i, int j, int value) , parameter is the node itself and the value we want to insert
        return mark(pos.i(), pos.j(), value);   //extract the position of the cell (i and j) and call the first method mark(int i, int j, int value)
    }

    // return true if the node equal to v=9 (Green, Explored)
    public boolean isMarked(int i, int j) {
        assert (isInMaze(i, j));
        return (maze[i][j] == V);

    }

    public boolean isMarked(MazePos pos) {   //overloaded of isMarked(int i, int j) , parameter is the node itself
        return isMarked(pos.i(), pos.j());   //extract the position of the cell (i and j) and call the first method isMarked(int i, int j)
    }

    // return true if the node is equal to 0 (White, Unexplored)
    public boolean isClear(int i, int j) {
        assert (isInMaze(i, j));
        return (maze[i][j] != X && maze[i][j] != V);

    }

    public boolean isClear(MazePos pos) {   //overloaded of isClear(int i, int j) , parameter is the node itself
        return isClear(pos.i(), pos.j());   //extract the position of the cell (i and j) and call the first method isClear(int i, int j)
    }

    // to make sure if it is reach the goal (Goal Test)
    public boolean isFinal(int i, int j) {

        return (i == Maze.END_I && j == Maze.END_J);
    }

    public boolean isFinal(MazePos pos) {  //overloaded of isFinal(int i, int j) , parameter is the node itself
        return isFinal(pos.i(), pos.j());  //extract the position of the cell (i and j) and call the first method isFinal(int i, int j)
    }

    // make Copy from the original maze
    public int[][] clone() { //used to create savedMaze[][] we already discussed its use before
        int[][] mazeCopy = new int[Size()][Size()];
        for (int i = 0; i < Size(); i++) {
            for (int j = 0; j < Size(); j++) {
                mazeCopy[i][j] = maze[i][j];
            }
        }
        return mazeCopy;
    }


    // to restore the maze to the initial state 
    public void restore(int[][] savedMazed) {
        for (int i = 0; i < Size(); i++) {
            for (int j = 0; j < Size(); j++) {
                maze[i][j] = savedMazed[i][j];
            }
        }

        maze[1][1] = 2;  // the start point
        maze[2][9] = 8;  // the goal
    }

    //generate random maze whith values 0 and 1 (black and white blocks)
    public int[][] GenerateArray() {
        arr = new int[10][10];
        Random rnd = new Random();
        int min = 0;
        int high = 1;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int n = rnd.nextInt((high - min) + 1) + min;
                arr[i][j] = n;

            }
        }
          arr[0][1] = 0;arr[1][0] = 0;arr[2][1] = 0;arr[1][2] = 0;//make sure all paths from initial state are legal moves (white block)
          arr[1][9] = 0;arr[2][8] = 0;arr[3][9] = 0;              //make sure all paths to goal are legal moves (white block)
//        for(int i=0; i<10;i++){ 
//           for(int j=0;j<10;j++){
//               System.out.print(arr[i][j]);
//               System.out.print(' ');
//            }
//           System.out.println("");
//          }
        return arr;
    }

    //draw the maze on the JFrame
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(70, 70);      //move the maze to begin at 70 from x and 70 from y

        // draw the maze
        if (repaint == true) {  // what to do if the repaint was set to true (draw the maze as a problem without the solution)
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.darkGray;       // block (black)
                            break;
                        case 8:
                            color = Color.RED;          // goal (red)
                            break;
                        case 2:
                            color = Color.YELLOW;      //initial state   (yellow)
                            break;
                        //   case '.' : color=Color.ORANGE; break;
                        default:
                            color = Color.WHITE;        // white free space 0  (white)
                    }
                    g.setColor(color);
                    g.fillRect(40 * col, 40 * row, 40, 40);    // fill rectangular with color 
                    g.setColor(Color.BLUE);                    //the border rectangle color
                    g.drawRect(40 * col, 40 * row, 40, 40);    //draw rectangular with color

                }
            }
        }

        if (repaint == false) {   // what to do if the repaint was set to false (draw the solution for the maze)
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.darkGray;     // block (black)
                            break;
                        case 8:
                            color = Color.RED;         // goal  (red)
                            break;
                        case 2:
                            color = Color.YELLOW;      //initial state   (yellow)
                            break;
                        case 9:
                            color = Color.green;   // the path from the initial state to the goal
                            break;
                        default:
                            color = Color.WHITE;   // white free space 0  (white)
                    }
                    g.setColor(color);
                    g.fillRect(40 * col, 40 * row, 40, 40);  // fill rectangular with color 
                    g.setColor(Color.BLUE);                  //the border rectangle color
                    g.drawRect(40 * col, 40 * row, 40, 40);  //draw rectangular with color

                }

            }

        }

    }

    public static void main(String[] args) {  // the main program

        SwingUtilities.invokeLater(new Runnable() {  // run the program through Swing (the entire program is run by GUI)
                                                     // we chose invokelater it won't make much difference if we chose invokeAndWait since the operation done by the first button will be done in a very short time
            @Override                                // you can read more here: https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
            public void run() {
                Maze maze = new Maze();              // we create new class which will invoke the constructor

            }
        });

    }

    public void solveQueue() { //BFS correspond to Queue.
        //start the timer
        startTime = System.nanoTime();

        //create LinkedList of MazPos (MazPos (the node) is what we will be adding and removing from the LinkedList)
        LinkedList<MazePos> list = new LinkedList<MazePos>();

        // add initial node to the list
        list.add(new MazePos(START_I, START_J));

        MazePos crt, next;
        while (!list.isEmpty()) {

            //get current position
            crt = list.removeFirst();

            // to be sure if it reach the goal
            if (isFinal(crt)) { //if the goal is reached then exit, no need for further exploration.
                break;
            }

            //mark the current position as explored
            mark(crt, V);

            //add its neighbors in the queue
            next = crt.north();    //move up
            if (isInMaze(next) && isClear(next)) { //isClear() function is used to implement Graph Search
                list.add(next);
            }
            next = crt.east();    //move right
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
             next = crt.west();    //move left 
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
            next = crt.south();   //move down
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }

        }

        if (!list.isEmpty()) {               //you exited before the list was emptied, meaning that you found the solution and exited.
            stopTime = System.nanoTime();    //stop the timer
            JOptionPane.showMessageDialog(rootPane, "You Got it");
        } else {                             //the list is empty, meaning that you went through all nodes and didn't find the solution.
            JOptionPane.showMessageDialog(rootPane, "You Are stuck in the maze");
        }

        System.out.println("\nFind Goal By BFS : ");
        Print();
        
        duration = stopTime - startTime;      //calculate the elapsed time
       
        bfsTime = (double) duration / 1000000;     //convert to ms
        System.out.println(String.format("Time %1.3f ms", bfsTime));

        textBFS.setText(String.format("%1.3f ms", bfsTime));

        
    }

}
