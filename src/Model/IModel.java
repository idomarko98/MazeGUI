package Model;

import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    static void setConfigurations(int numberOfThreads, String generatingAlgorithmName, String solvingAlgorithmName){
        Server.Configurations.setProp(numberOfThreads, generatingAlgorithmName, solvingAlgorithmName);
    }
    //int[][] getMaze();
    Maze getMaze();
    Solution getMazeSolution();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
}
