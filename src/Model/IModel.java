package Model;

import Server.Server;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    void moveCharacter(MouseEvent movement, double startX, double startY);
    static void setConfigurations(int numberOfThreads, String generatingAlgorithmName, String solvingAlgorithmName){
        Server.Configurations.setProp(numberOfThreads, generatingAlgorithmName, solvingAlgorithmName);
    }

    static int getConfigurationNumberOfThreads(){
        return Server.Configurations.getThreadPoolSize();
    }

    static IMazeGenerator getConfigurationGeneratingAlgorithm(){
        return Server.Configurations.getMazeGeneratingAlgorithm();
    }

    static String getConfigurationSolvingAlgorithmName(){
        return Server.Configurations.getMazeSearchingAlgorithm().getName();
    }

    //int[][] getMaze();
    Maze getMaze();
    Solution getMazeSolution();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
}
