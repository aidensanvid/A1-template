package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class RightHandPathFinder extends MazeExplorer {

     public String solveMaze(char entrance, char exit) {


        if (entrance == 'W') {
            facing = 'E';
            location[0] = maze.getWestEntrance()[0];
            location[1] = maze.getWestEntrance()[1];
        }
        else {
            facing = 'W';
            location[0] = maze.getEastEntrance()[0];
            location[1] = maze.getEastEntrance()[1];
        }

        String path = "";

        while (!maze.atEnd(location, exit)) {

            turn('R');

            if (isNextMoveValid()){
                moveForward();

                path += "RF";
            }
            else{
                turn('L');
                if (isNextMoveValid()) {
                    moveForward();
                    path += "F";
                }
                else {
                    turn('L');
                    path += "L";
                }
            }
        }

        return path;
    }

    public RightHandPathFinder(String maze_file) throws IOException{
        super(maze_file);
    }

}

class PathVerifier extends MazeExplorer {

    public boolean testPathEitherEntrance(String path) {

        boolean west_entrance_result = testPath(path, 'W', 'E');
        boolean east_entrance_result = testPath(path, 'E', 'W');

        return (west_entrance_result || east_entrance_result);

    }
 
    public boolean testPath(String path, char entrance, char exit) {

        if (entrance == 'W') {
            facing = 'E';
            location[0] = maze.getWestEntrance()[0];
            location[1] = maze.getWestEntrance()[1];

        }
        else {
            facing = 'W';
            location[0] = maze.getEastEntrance()[0];
            location[1] = maze.getEastEntrance()[1];
        }

        String num_buffer = "";

        // Removes whitespace from input path
        path = path.replaceAll("\\s", "");


        for (int i=0; i<path.length(); i++) {
            char c = path.charAt(i);

            if (c == 'F') {

                // Processes canonical inputs (e.g. F, FFF, FF)
                // Repeatedly moves forward until end of maze is reached, or an illegal move is made
                if (num_buffer.equals("")) {
                    
                    if (isNextMoveValid()) {
                        moveForward();

                        if (maze.atEnd(location, exit)) {
                            return true;
                        }
                    }
                    else {
                        return false;
                    }
                }
                // Processes factorized inputs (e.g. 4F, 2F, 19F)
                // Repeatedly moves forward until end of maze is reached, or an illegal move is made
                else {
                    int repetitions = Integer.parseInt(num_buffer);

                    for (int j = 0; j<repetitions; j++) {

                        if (isNextMoveValid()) {
                            moveForward();

                            if (maze.atEnd(location, exit)) {
                                return true;
                            }

                        }
                        else {
                            return false;
                        }
                    }
                }

                num_buffer = "";

            }
            else if (c == 'R') {

                // Processes canonical inputs (e.g. R, RR, RRR)
                // Turns maze explorer right in-place
                if (num_buffer.equals("")) {
                    turn('R');
                }
                // Processes factorized inputs (e.g. 2R, 4R, 27R)
                // Turns maze explorer right in-place
                else {
                    int repetitions = Integer.parseInt(num_buffer);

                    for (int j = 0; j<repetitions; j++) {
                        turn('R');
                    }
                }

                num_buffer = "";

            }
            else if (c == 'L') {

                // Processes canonical inputs (e.g. L, LL, LLL)
                // Turns maze explorer left in-place
                if (num_buffer.equals("")) {
                    turn('L');
                }
                // Processes factorized inputs (e.g. 2L, 4L, 16L)
                // Turns maze explorer left in-place
                else {
                    int repetitions = Integer.parseInt(num_buffer);

                    for (int j = 0; j<repetitions; j++) {
                        turn('L');
                    }
                }

                num_buffer = "";

            }
            // Tracks the coefficient infront of factorized inputs in a numeric buffer
            else {
                num_buffer += c;
            }
        }

        return false;

    }

    public PathVerifier(String maze_file) throws IOException{
        super(maze_file);

    }
}

class MazeExplorer {

    protected Maze maze;
    protected int[] location = new int[2];
    protected char facing;

    // Turns the maze explorer left or right in-place
    public void turn(char direction) {

        char[] directions = {'N', 'E', 'S', 'W'};

        // Rotates to the next direction, depending on whether the maze explorer turns left or right
        // Left: N -> W, Right: N -> E
        for (int i=0; i<directions.length; i++) {

            if (directions[i] == facing) {

                if (direction == 'L') {
                
                    facing = directions[Math.floorMod(i-1, directions.length)];
                }
                else {
                    facing = directions[Math.floorMod(i+1, directions.length)];
                }
                break;

            }
        }
    }

    // Ensures the next move the maze explorer makes is legal
    public boolean isNextMoveValid() {

        // Checks the direction the maze explorer is currently facing (North, East, South, West)
        if (facing == 'N') {

            int[] next_location = {location[0] - 1, location[1]};

            // Checks the maze explorer doesn't move out of the maze bounds
            if (maze.inBounds(next_location)) {
                // Checks the maze explorer doesn't collide with a wall
                if (maze.isWall(next_location)) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return false;
            }
        }
        else if (facing == 'E') {

            int[] next_location = {location[0], location[1] + 1};

            // Checks the maze explorer doesn't move out of the maze bounds
            if (maze.inBounds(next_location)) {
                // Checks the maze explorer doesn't collide with a wall
                if (maze.isWall(next_location)) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return false;
            }
        }
        else if (facing == 'S') {

            int[] next_location = {location[0] + 1, location[1]};

            // Checks the maze explorer doesn't move out of the maze bounds
            if (maze.inBounds(next_location)) {
                // Checks the maze explorer doesn't collide with a wall
                if (maze.isWall(next_location)) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return false;
            }
        }
        else {

            int[] next_location = {location[0], location[1] - 1};

            // Checks the maze explorer doesn't move out of the maze bounds
            if (maze.inBounds(next_location)) {
                // Checks the maze explorer doesn't collide with a wall
                if (maze.isWall(next_location)) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return false;
            }
        }
    }

    // Moves the maze explorer in the direction its currently facing
    public void moveForward() {

        if (facing == 'N') {
            location[0] -= 1;
        }
        else if (facing == 'E') {
            location[1] += 1;
        }
        else if (facing == 'S') {
            location[0] += 1;
        }
        else {
            location[1] -= 1;
        }
    }

    public MazeExplorer (String maze_file) throws IOException {

        this.maze = new Maze(maze_file);
        
    }

}

class Maze {

    private char[][] maze;

    private int rows;
    private int cols;

    private int[] westEntrance;
    private int[] eastEntrance;

    // Retrieve the dimensions (rows x cols) of a maze in a file
    public int[] getMazeDimensions(String maze_file) throws FileNotFoundException, IOException {

        int[] dimensions = new int[2];

        int rows = 0;
        int cols = 0;

        BufferedReader reader = new BufferedReader(new FileReader(maze_file));
        String line;

        while ((line = reader.readLine()) != null) {
            if (cols == 0) {
                cols = line.length();
            }
            rows++;
        }

        dimensions[0] = rows;
        dimensions[1] = cols;

        return dimensions;

    }

    public int[] getWestEntrance() {
        return westEntrance;
    }

    public int[] getEastEntrance() {
        return eastEntrance;
    }
 
    public int[] findWestEntrance() {

        for (int i=0; i<rows; i++) {
            if (maze[i][0] != '#') {
                int[] location = {i, 0};
                return location;
            }
        }

        int[] location = {-1, -1};
        return location;

    }

    // Locates the exit of the maze
    public int[] findEastEntrance() {

        for (int i=0; i<rows; i++) {
            if (maze[i][cols-1] != '#') {
                int[] location = {i, cols-1};
                return location;
            }
        }

        int[] location = {-1, -1};
        return location;

    }

     public boolean atEnd(int[] coordinates, char exit) {

        if (exit == 'W') {
            return (coordinates[0] == westEntrance[0] && coordinates[1] == westEntrance[1]);
        }
        else {
            return (coordinates[0] == eastEntrance[0] && coordinates[1] == eastEntrance[1]);
        }
        
    }

    public boolean inBounds(int[] coordinates) {

        return (coordinates[0] >= 0 && coordinates[0] < rows && coordinates[1] >= 0 && coordinates[1] < cols);

    }

    public boolean isWall(int[] coordinates) {

        return (maze[coordinates[0]][coordinates[1]] == '#');
 
    }

    // Load a maze from a selected file
    public char[][] loadMaze(String maze_file) throws FileNotFoundException, IOException {

        char[][] maze = new char[rows][cols];

        BufferedReader reader = new BufferedReader(new FileReader(maze_file));
        String line;

        int row = 0;
        int col = 0;

        while ((line = reader.readLine()) != null) {
            for (int idx = 0; idx < line.length(); idx++) {
                maze[row][col] = line.charAt(idx);
                col++;
            }
            col = 0;
            row++;
        }

        return maze;
    }

    public Maze(String maze_file) throws FileNotFoundException, IOException{

        int[] dimensions = getMazeDimensions(maze_file);

        this.rows = dimensions[0];
        this.cols = dimensions[1];
        this.maze = loadMaze(maze_file);
        this.westEntrance = findWestEntrance();
        this.eastEntrance = findEastEntrance();
    }


}

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        // Generate flags accepted in command-line (-i)
        Options options = new Options();
        options.addOption("i", true, "select maze file");
        options.addOption("p", true, "enter a canonical or factorized path through the maze");

        CommandLineParser parser = new DefaultParser();

        // Ensure empty -i flags are caught
        try {
            CommandLine cmd = parser.parse(options, args);
            
            // Loads appropriate maze file
            if(cmd.hasOption("i")) {

                String maze_file = cmd.getOptionValue("i");

                logger.info("Starting Maze Runner");
                logger.info("Reading the maze from file " + maze_file);
                

                // Checks for user-entered path
                if (cmd.hasOption("p")) {


                    String path = cmd.getOptionValue("p");


                    PathVerifier path_verifier = new PathVerifier(maze_file);
                    
                    if (path_verifier.testPathEitherEntrance(path)) {
                        System.out.println("correct path");
                    }
                    else {
                        System.out.println("incorrect path");
                    }

                    
                }
                else {
                    
                    RightHandPathFinder path_finder = new RightHandPathFinder(maze_file);

                    // Solves the maze if the user doesn't include their own path to verify
                    System.out.println("Path: " + path_finder.solveMaze('W', 'E'));

                }

            }
            else{
                logger.error("No maze file provided.");
            }

        }
        catch (Exception exception) {
            logger.error("Invalid file provided", exception);
        }

    }
}
