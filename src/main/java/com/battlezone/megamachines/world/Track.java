package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Track extends GameObject {

    private TrackType type;

    private Track(Vector2f position, float scale, TrackType type) {
        super(position, scale);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }

    public static List<Track> generateMap(int tracksAcross, int tracksDown, int trackSize) {
        TrackType[][] world = new TrackType[tracksAcross][tracksDown];

        //start by filling the edges with track
        for (int i = 0; i < tracksAcross; i++) {
            world[i][0] = TrackType.RIGHT;
            world[i][tracksDown - 1] = TrackType.LEFT;
        }
        for (int i = 0; i < tracksDown; i++) {
            world[0][i] = TrackType.DOWN;
            world[tracksAcross - 1][i] = TrackType.UP;
        }
        // corners
        world[0][0] = TrackType.RIGHT_DOWN;
        world[tracksAcross - 1][0] = TrackType.UP_RIGHT;
        world[tracksAcross - 1][tracksDown - 1] = TrackType.LEFT_UP;
        world[0][tracksDown - 1] = TrackType.DOWN_LEFT;

        // do some mutations to randomise the map. Currently doing 0 mutations because it doesn't work
        int mutations = 0;
        int mutationSize = 3;

        for (int i = mutations; i > 0; i--) {
            // choose a random mutationSize * mutationSize square from the grid
            int x = MathUtils.randomInteger(0, tracksAcross - mutationSize);
            int y = MathUtils.randomInteger(0, tracksDown - mutationSize);

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

        //transform this into an array of track
        ArrayList<Track> track = new ArrayList<>();
        for (int i = 0; i < tracksAcross; i++) {
            for (int j = 0; j < tracksDown; j++) {
                TrackType type = world[i][j];
                if (type != null) {
                    Track t = new Track(new Vector2f((tracksAcross - i - 1) * trackSize, (tracksDown - j - 1) * trackSize), trackSize, world[i][j]);
                    track.add(t);
                }
            }
        }

        return track;
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

        newSection[start.getFirst()][start.getSecond()] = section[start.getFirst()][start.getSecond()];
        newSection[end.getFirst()][end.getSecond()] = section[end.getFirst()][end.getSecond()];

        Pair<Integer, Integer> current = start;
        while (current != end) {
            //move randomly
            ArrayList<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();
            //check the 4 directions for validity and add to possibles if valid
            int x = current.getFirst();
            int y = current.getSecond();
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
            newSection[current.getFirst()][current.getSecond()] = TrackType.LEFT;//TODO work out correct type
        }

        return newSection;
    }

}
