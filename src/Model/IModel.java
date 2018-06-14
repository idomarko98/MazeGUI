package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    //int[][] getMaze();
    Maze getMaze();
    Solution getMazeSolution();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
}
