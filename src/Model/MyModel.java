package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import com.sun.org.apache.xpath.internal.operations.String;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyModel extends Observable implements IModel {

    private Maze maze;
    private Solution mazeSolution;
    private int characterPositionRow;
    private int characterPositionColumn;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratorServer, mazeSolverServer;

    public MyModel() {
        //Raise the servers
        maze = null;
        mazeSolution = null;
        characterPositionRow = 1;
        characterPositionColumn = 1;
        mazeGeneratorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolverServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratorServer.start();
        mazeSolverServer.start();
    }

    public void stopServers() {
        mazeGeneratorServer.stop();
        mazeSolverServer.stop();
    }

    @Override
    public void load(Maze loadedMaze, Position loadedPoistion) {
        setMaze(loadedMaze);
        setCharacterPosition(loadedPoistion);
    }


    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                                /*ObjectInputStream fromServer=null;
                                if(inFromServer.available() != 0)
                                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);*/
                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                                toServer.flush();
                                int[] mazeDimensions = new int[]{height, width};
                                toServer.writeObject(mazeDimensions); //send maze dimensions to server
                                toServer.flush();
                                byte[] compressedMaze = (byte[])fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                                int size = /*(int)Math.ceil((double)mazeDimensions[0]*mazeDimensions[1] / (double)8) + 24*/mazeDimensions[0]*mazeDimensions[1]*4 + 24;
                                byte[] decompressedMaze = new byte[size]; //allocating byte[] for the decompressed maze -
                                is.read(decompressedMaze); //Fill decompressedMaze with bytes
                                Maze mazeToBeSet = new Maze(decompressedMaze);
                                //mazeToBeSet.print();
                                setMaze(mazeToBeSet);
                                setCharacterPosition(maze.getStartPosition());
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
        }
        //Maze maze = mazeGeneratorServer

        /*threadPool.execute(() -> {
            //generateRandomMaze(width,height);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });*/
    }

    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new
                                        ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new
                                        ObjectInputStream(inFromServer);
                                toServer.flush();
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                setSolution((Solution) fromServer.readObject()); //read generated maze (compressed with MyCompressor) from server

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void moveCharacter(KeyCode movement) {
        if(maze != null) {
            switch (movement) {
                case UP:
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn));
                    break;
                case NUMPAD8:
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn));
                    break;

                case DOWN:
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn));
                    break;
                case NUMPAD2:
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn));
                    break;

                case RIGHT:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn + 1));
                    break;
                case NUMPAD6:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn + 1));
                    break;

                case LEFT:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn - 1));
                    break;
                case NUMPAD4:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn - 1));
                    break;

                case NUMPAD9: //move right up
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn + 1));
                    break;

                case NUMPAD7: //move left up
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn - 1));
                    break;

                case NUMPAD3: //move right down
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn + 1));
                    break;

                case NUMPAD1: //move left down
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn - 1));
                    break;
            }
            if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex())
                notifySolved(true);
        }
        /*
        setChanged();
        notifyObservers();
        */
    }

    private void notifySolved(boolean b) {
        setChanged();
        notifyObservers(b);
    }

    public void moveCharacter(MouseEvent movement, double startX, double startY){
        if(maze != null) {
            if (!movement.isControlDown()) {
                if (movement.getY() < startY && (Math.abs(movement.getY() - startY) > 70)) {
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn));
                } else if (movement.getY() > startY && (Math.abs(movement.getY() - startY) > 70)) {
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn));
                } else if (movement.getX() > startX) {
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn + 1));
                } else if (movement.getX() < startX) {
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn - 1));
                }
                if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex())
                    notifySolved(true);
            }
        }
    }

    private void notifyNotAbleToMove(KeyCode movement) {
        setChanged();
        notifyObservers(movement);
    }

    private void notifyNotAbleToMove(MouseEvent movement) {
        setChanged();
        notifyObservers(movement);
    }

    @Override
    public Maze getMaze(){
        return maze;
    }

    @Override
    public Solution getMazeSolution() {
        return mazeSolution;
    }


    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    private void setCharacterPosition(Position characterPosition) {
        characterPositionColumn = characterPosition.getColumnIndex();
        characterPositionRow = characterPosition.getRowIndex();
        setChanged();
        notifyObservers(new Position(characterPositionRow, characterPositionColumn));
    }

    private void setMaze(Maze maze){
        this.maze = maze;
        this.mazeSolution = null;
        setChanged();
        notifyObservers(this.maze);
    }

    public void setSolution(Solution solution) {
        this.mazeSolution = solution;
        setChanged();
        notifyObservers(this.mazeSolution);
    }
}
