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
import algorithms.search.MazeState;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
//import com.sun.org.apache.xpath.internal.operations.String;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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

    private int gombaPositionRow;
    private int gombaPositionColumn;

    private int tortugaPositionRow;
    private int tortugaPositionColumn;

    private int mushroomPositionRow;
    private int mushroomPositionColumn;

    private volatile Object gombaLock;

    private volatile Object tortugaLock;

    private volatile Object mushroomLock;

    public MyModel() {
        //Raise the servers
        gombaLock = new Object();
        tortugaLock = new Object();
        mushroomLock = new Object();

        maze = null;
        mazeSolution = null;
        characterPositionRow = 1;
        characterPositionColumn = 1;
        mazeGeneratorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolverServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());

        gombaPositionRow = 1;
        gombaPositionColumn = 1;

        tortugaPositionRow = 1;
        tortugaPositionColumn = 1;

        mushroomPositionRow = 1;
        mushroomPositionColumn = 1;
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
        setSolution(null);
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
                                setGombaPosition(randomPositionOnMazePath());
                                setTortugaPosition(randomPositionOnMazePath());
                                setMushroomPosition(randomPositionOnMazePath());
                                moveGomba();
                                moveTortuga();
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

    private Position randomPositionOnMazePath() {
        int resultRow = -1;
        int resultCol = -1;
        do{
            Random r = new Random();
            int lowRow = 0;
            int highRow = maze.getRowSize();
            resultRow = r.nextInt(highRow - lowRow) + lowRow;

            int lowCol = 0;
            int highCol = maze.getRowSize();
            resultCol = r.nextInt(highCol - lowCol) + lowCol;

        } while(maze.getAtIndex(resultRow ,resultCol) != 0 && ((resultRow != maze.getStartPosition().getRowIndex()) || (resultCol != maze.getStartPosition().getColumnIndex())));

        return new Position(resultRow, resultCol);
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
                    else {
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn));
                        notifyPossibleMoves(new Position(characterPositionRow - 1, characterPositionColumn));
                    }
                    break;
                case NUMPAD8:
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn));
                        notifyPossibleMoves(new Position(characterPositionRow - 1, characterPositionColumn));
                    }
                    break;

                case DOWN:
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn));
                        notifyPossibleMoves(new Position(characterPositionRow + 1, characterPositionColumn));
                    }
                    break;
                case NUMPAD2:
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn));
                        notifyPossibleMoves(new Position(characterPositionRow + 1, characterPositionColumn));
                    }
                    break;

                case RIGHT:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn + 1));
                        notifyPossibleMoves(new Position(characterPositionRow, characterPositionColumn + 1));
                    }
                    break;
                case NUMPAD6:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn + 1));
                        notifyPossibleMoves(new Position(characterPositionRow, characterPositionColumn + 1));
                    }
                    break;

                case LEFT:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn - 1));
                        notifyPossibleMoves(new Position(characterPositionRow, characterPositionColumn - 1));
                    }
                    break;
                case NUMPAD4:
                    if (maze.getAtIndex(characterPositionRow, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow, characterPositionColumn - 1));
                        notifyPossibleMoves(new Position(characterPositionRow, characterPositionColumn - 1));
                    }
                    break;

                case NUMPAD9: //move right up
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn + 1));
                        notifyPossibleMoves(new Position(characterPositionRow - 1, characterPositionColumn + 1));
                    }
                    break;

                case NUMPAD7: //move left up
                    if (maze.getAtIndex(characterPositionRow - 1, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow - 1, characterPositionColumn - 1));
                        notifyPossibleMoves(new Position(characterPositionRow - 1, characterPositionColumn - 1));
                    }
                    break;

                case NUMPAD3: //move right down
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn + 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn + 1));
                        notifyPossibleMoves(new Position(characterPositionRow + 1, characterPositionColumn + 1));
                    }
                    break;

                case NUMPAD1: //move left down
                    if (maze.getAtIndex(characterPositionRow + 1, characterPositionColumn - 1) != 0)
                        notifyNotAbleToMove(movement);
                    else {
                        setCharacterPosition(new Position(characterPositionRow + 1, characterPositionColumn - 1));
                        notifyPossibleMoves(new Position(characterPositionRow + 1, characterPositionColumn - 1));
                    }
                    break;
            }
            if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex())
                notifySolved(true);
            if((characterPositionRow == tortugaPositionRow && characterPositionColumn == tortugaPositionColumn) || (characterPositionRow == gombaPositionRow && characterPositionColumn == gombaPositionColumn))
                notifyCollide("collide");
            if(characterPositionColumn == mushroomPositionColumn && characterPositionRow == mushroomPositionRow)
                notifyCollideWithMushroom("collideWithMushroom");
        }
        /*
        setChanged();
        notifyObservers();
        */
    }

    private void notifyPossibleMoves(Position position) {
        MazeState state = new MazeState(position);
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        List<AState> stateList = searchableMaze.getAllPossibleStates(state);
        Object[] states = stateList.toArray();
        setChanged();
        notifyObservers(states);
    }

    private void notifyCollideWithMushroom(String collideWithMushroom) {
        setChanged();
        notifyObservers(collideWithMushroom);
    }

    private void notifyCollide(String colide) {
        setChanged();
        notifyObservers(colide);
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
                if((characterPositionRow == tortugaPositionRow && characterPositionColumn == tortugaPositionColumn) || (characterPositionRow == gombaPositionRow && characterPositionColumn == gombaPositionColumn))
                    notifyCollide("collide");
                if(characterPositionColumn == mushroomPositionColumn && characterPositionRow == mushroomPositionRow)
                    notifyCollideWithMushroom("collideWithMushroom");
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

    public void setMushroomPosition(Position mushroomPosition) {
        synchronized (mushroomLock) {
            this.mushroomPositionRow = mushroomPosition.getRowIndex();
            this.mushroomPositionColumn = mushroomPosition.getColumnIndex();
        }
        setChanged();
        notifyObservers("MushroomMoved");
    }

    private String gombaMoveToSide = "";

    public void setGombaPosition(Position gombaPosition) {
        synchronized (gombaLock) {
            this.gombaPositionRow = gombaPosition.getRowIndex();
            this.gombaPositionColumn = gombaPosition.getColumnIndex();
        }
        setChanged();
        notifyObservers("GombaMoved" + gombaMoveToSide);
    }

    private void moveGomba(){
        Thread moveGomba = new Thread(()->{
            while (true) {
                Position moveTo = pickRandomAdjPosition(gombaPositionRow, gombaPositionColumn);
                setGombaPosition(moveTo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        moveGomba.start();
    }

    private Position pickRandomAdjPosition(int gombaPositionRow, int gombaPositionColumn) {
        int prevGombaPositionRow;
        int prevGombaPositionColumn;
        synchronized (gombaLock) {
            prevGombaPositionRow = gombaPositionRow;
            prevGombaPositionColumn = gombaPositionColumn;
        }
        int tempRow = prevGombaPositionRow;
        int tempCol = prevGombaPositionColumn;

        Position gombaPosition = new Position(tempRow, tempCol);
        MazeState gombaState = new MazeState(gombaPosition);
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        List<AState> stateList = searchableMaze.getAllPossibleStates(gombaState);
        //do{
        stateList.remove(new MazeState(new Position(mushroomPositionRow,mushroomPositionColumn)));
        stateList.remove(new MazeState(new Position(maze.getGoalPosition())));

        Random r = new Random();
        int low = 0;
        int high = stateList.size();
        int result = r.nextInt(high - low) + low;

        MazeState gombaNextState = (MazeState) stateList.get(result);
        Position gombaNextPosition = gombaNextState.getPositionOfMazeState();

        tempRow = gombaNextPosition.getRowIndex();
        tempCol = gombaNextPosition.getColumnIndex();

        if (tempCol < prevGombaPositionColumn)
            gombaMoveToSide = "Left";
        else if (tempCol > prevGombaPositionColumn)
            gombaMoveToSide = "Right";
        else
            gombaMoveToSide = "";
            /*
            switch (result) {
                case 0:
                    tempRow++;
                    gombaMoveToSide = "";
                    break;
                case 1:
                    tempRow--;
                    gombaMoveToSide = "";
                    break;
                case 2:
                    tempCol++;
                    gombaMoveToSide = "Right";
                    break;
                case 3:
                    tempCol--;
                    gombaMoveToSide = "Left";
                    break;
            }
            */
        //}while (maze.getAtIndex(tempRow,tempCol) != 0);
        return new Position(tempRow, tempCol);
    }

    public int getGombaPositionRowIndex(){
        return gombaPositionRow;
    }

    public int getGombaPositionColumnIndex(){
        return gombaPositionColumn;
    }

    private String tortugaMoveToSide = "";

    public void setTortugaPosition(Position tortugaPosition) {
        synchronized (tortugaLock) {
            this.tortugaPositionRow = tortugaPosition.getRowIndex();
            this.tortugaPositionColumn = tortugaPosition.getColumnIndex();
        }
        setChanged();
        notifyObservers("TortugaMoved" + tortugaMoveToSide);
    }

    private void moveTortuga(){
        Thread moveTortuga = new Thread(()->{
            while (true) {
                Position moveTo = pickTortugaRandomAdjPosition(tortugaPositionRow, tortugaPositionColumn);
                setTortugaPosition(moveTo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        moveTortuga.start();
    }

    private Position pickTortugaRandomAdjPosition(int tortugaPositionRow, int tortugaPositionColumn) {
        int prevTortugaPositionRow;
        int prevTortugaPositionColumn;
        synchronized (tortugaLock) {
            prevTortugaPositionRow = tortugaPositionRow;
            prevTortugaPositionColumn = tortugaPositionColumn;
        }
        int tempRow = prevTortugaPositionRow;
        int tempCol = prevTortugaPositionColumn;

        Position tortugaPosition = new Position(tempRow, tempCol);
        MazeState tortugaState = new MazeState(tortugaPosition);
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        List<AState> stateList = searchableMaze.getAllPossibleStates(tortugaState);
        //do{

        stateList.remove(new MazeState(new Position(mushroomPositionRow,mushroomPositionColumn)));
        stateList.remove(new MazeState(new Position(maze.getGoalPosition())));

        Random r = new Random();
        int low = 0;
        int high = stateList.size();
        int result = r.nextInt(high - low) + low;

        MazeState tortugaNextState = (MazeState) stateList.get(result);
        Position tortugaNextPosition = tortugaNextState.getPositionOfMazeState();

        tempRow = tortugaNextPosition.getRowIndex();
        tempCol = tortugaNextPosition.getColumnIndex();

        if (tempCol < prevTortugaPositionColumn)
           tortugaMoveToSide = "Left";
        else if (tempCol > prevTortugaPositionColumn)
            tortugaMoveToSide = "Right";
        else
           tortugaMoveToSide = "";
        return new Position(tempRow, tempCol);
    }

    public int getTortugaPositionRowIndex(){
        return tortugaPositionRow;
    }

    public int getTortugaPositionColumnIndex(){
        return tortugaPositionColumn;
    }

    public int getMushroomPositionRowIndex(){
        return mushroomPositionRow;
    }

    public int getMushroomPositionColumnIndex(){
        return mushroomPositionColumn;
    }
}
