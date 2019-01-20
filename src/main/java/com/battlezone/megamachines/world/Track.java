package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.MathUtils;
import javafx.util.Pair;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;

public class Track extends GameObject {

    enum TrackType {
        UP, DOWN, LEFT, RIGHT,
        UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
        LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN,
        UNKNOWN
    }

    private TrackType type;

    public Track(Vector2f position, TrackType type) {
        super(position);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }

    static TrackType[][] generateMap(int width, int height) {
        TrackType[][] world = new TrackType[width][height];

        //start by filling the edges with track
        for (int i = 0; i < width; i++) {
            world[i][0] = TrackType.RIGHT;
            world[i][height - 1] = TrackType.LEFT;
        }
        for (int i = 0; i < height; i++) {
            world[0][i] = TrackType.DOWN;
            world[width - 1][i] = TrackType.UP;
        }
        // corners
        world[0][0] = TrackType.RIGHT_DOWN;
        world[width - 1][0] = TrackType.UP_RIGHT;
        world[width - 1][height - 1] = TrackType.LEFT_UP;
        world[0][height - 1] = TrackType.DOWN_LEFT;

        // do some mutations to randomise the map. Currently doing 0 mutations because it doesn't work
        int mutations = 0;
        int mutationSize = 3;

        for (int i = mutations; i > 0; i--) {
            // choose a random mutationSize * mutationSize square from the grid
            int x = MathUtils.randomInteger(0, width - mutationSize);
            int y = MathUtils.randomInteger(0, height - mutationSize);

            TrackType[][] section = new TrackType[mutationSize][mutationSize];
            for (int j = 0; j < mutationSize; j++) {
                for (int k = 0; k < mutationSize; k++) {
                    section[j][k] = world[x + j][y + k];
                }
            }

            TrackType[][] newSection = mutateSection(section);

            // insert it back into place
            for (int j = 0; j < mutationSize; j++) {
                for (int k = 0; k < mutationSize; k++) {
                    world[x + j][y + k] = newSection[j][k];
                }
            }
        }

        return world;
    }

    private static TrackType[][] mutateSection(TrackType[][] section) {

        Pair<Integer, Integer> start = null;
        Pair<Integer, Integer> end = null;


        // find the start and edge of the track within this section
        // we do this by finding pieces of track such that they are surrounded by null in all but one direction

        int size = section.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (section[i][j] == null)
                    continue;
                // look at the 4 adjacent and see how many are null
                int nullCount = 0;
                if (i == 0 || section[i - 1][j] == null)
                    //nothing to the left
                    nullCount += 1;
                if (j == 0 || section[i][j - 1] == null)
                    nullCount += 1;
                if (i == size - 1 || section[i + 1][j] == null)
                    nullCount += 1;
                if (j == size - 1 || section[i][j + 1] == null)
                    nullCount += 1;

                if (nullCount == 3) {
                    //this is a start or end. Which is which doesn't matter
                    if (start == null)
                        start = new Pair<>(i, j);
                    else if (end == null)
                        end = new Pair<>(i, j);
                    else {
                        //3 edges, prob already mutated, abort!
                        return section;
                    }
                }
            }
        }

        if (end == null) {
            // must be no track in this section
            return section;
        }
        System.out.println("CHECK SECTION ");
        System.out.println(Arrays.deepToString(section).replace("], ", "]\n").replace("[[", "[\n[").replace("]]", "]\n]"));

        System.out.println(start);
        System.out.println(end);

        // generate a new random route from start to end
        // starting from the start, choose a random adjacent tile to lay down new track
        // start with a blank slate with only the start and end
        TrackType[][] newSection = new TrackType[size][size];

        newSection[start.getKey()][start.getValue()] = section[start.getKey()][start.getValue()];
        newSection[end.getKey()][end.getValue()] = section[end.getKey()][end.getValue()];

        Pair<Integer, Integer> current = start;
        while (current != end) {
            //move randomly
            ArrayList<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();
            //check the 4 directions for validity and add to possibles if valid
            int x = current.getKey();
            int y = current.getValue();
            if (x > 0 && newSection[x - 1][y] == null)
                possibleMoves.add(new Pair<>(x - 1, y));
            if (y > 0 && newSection[x][y - 1] == null)
                possibleMoves.add(new Pair<>(x, y - 1));
            if (x < size - 1 && newSection[x + 1][y] == null)
                possibleMoves.add(new Pair<>(x + 1, y));
            if (y < size - 1 && newSection[x][y + 1] == null)
                possibleMoves.add(new Pair<>(x, y + 1));

            //choose randomly from possibles
            if (possibleMoves.size() == 0) {
                current = end;
            } else {
                int chosen = MathUtils.randomInteger(0, possibleMoves.size());
                current = possibleMoves.get(chosen);
            }
            newSection[current.getKey()][current.getValue()] = TrackType.UNKNOWN;
        }

        return newSection;
    }

}
