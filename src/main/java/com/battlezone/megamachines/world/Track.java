package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.renderer.game.Renderable;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Track implements Renderable {

    private List<TrackPiece> pieces;
    private int tracksAcross;
    private int tracksDown;
    private int trackSize;

    public Track(int tracksAcross, int tracksDown, int trackSize) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;
        this.trackSize = trackSize;
        pieces = generateMap(tracksAcross, tracksDown, trackSize);
    }

    public List<TrackPiece> getPieces() {
        return pieces;
    }

    public TrackPiece getPiece(int index) {
        return getPieces().get(index);
    }

    public int getTracksAcross() {
        return tracksAcross;
    }

    public int getTracksDown() {
        return tracksDown;
    }

    public int getTrackSize() {
        return trackSize;
    }

    private static List<TrackPiece> generateMap(int tracksAcross, int tracksDown, int trackSize) {
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
        world[0][0] = TrackType.DOWN_RIGHT;
        world[tracksAcross - 1][0] = TrackType.RIGHT_UP;
        world[tracksAcross - 1][tracksDown - 1] = TrackType.UP_LEFT;
        world[0][tracksDown - 1] = TrackType.LEFT_DOWN;

        // do some mutations to randomise the map. Currently doing 0 mutations because it doesn't work
        int mutations = 1;
        int mutationSize = 8;

        for (int i = mutations; i > 0; i--) {
            // choose a random mutationSize * mutationSize square from the grid
            int x = MathUtils.randomInteger(0, tracksAcross - mutationSize - 1);
            int y = MathUtils.randomInteger(0, tracksDown - mutationSize - 1);

            TrackType[][] section = new TrackType[mutationSize][mutationSize];
            ArrayUtil.prettyPrint(world);
            for (int j = 0; j < mutationSize; j++) {
                System.arraycopy(world[x + j], y, section[j], 0, mutationSize);
            }

            world = mutateSection(section, world, x, y);

            ArrayUtil.prettyPrint(world);
        }

        //transform this into an array of track
        ArrayList<TrackPiece> track = new ArrayList<>();
        for (int i = 0; i < tracksAcross; i++) {
            for (int j = 0; j < tracksDown; j++) {
                TrackType type = world[i][j];
                if (type != null) {
                    TrackPiece t = new TrackPiece((i) * trackSize, (j) * trackSize, trackSize, world[i][j]);
                    track.add(t);
                }
            }
        }

        return track;
    }

    private static TrackType[][] mutateSection(TrackType[][] section, TrackType[][] world, int sectionX, int sectionY) {

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
                    //this is a start or end
                    if (start == null)
                        start = new Pair<>(i, j);
                    else if (end == null)
                        end = new Pair<>(i, j);
                    else {
                        //3 edges, prob already mutated, abort!
                        return world;
                    }
                }
            }
        }

        if (end == null) {
            // must be no track in this section
            return world;
        }

        //fix start and end, make sure they're right way around
        // on of the pieces adj to start should be able to be linked into start, otherwise swap with end

        var valid = false;
        if (start.getFirst() > 0 && section[start.getFirst() - 1][start.getSecond()] != null)
            //something to the left
            //do i lead into it
            valid = (section[start.getFirst() - 1][start.getSecond()].initialDirection() == TrackType.LEFT && section[start.getFirst()][start.getSecond()].finalDirection() == TrackType.LEFT);
        else if (start.getSecond() > 0 && section[start.getFirst()][start.getSecond() - 1] != null)
            valid = (section[start.getFirst()][start.getSecond() - 1].initialDirection() == TrackType.DOWN && section[start.getFirst()][start.getSecond()].finalDirection() == TrackType.DOWN);
        else if (start.getFirst() < size - 1 && section[start.getFirst() + 1][start.getSecond()] != null)
            valid = (section[start.getFirst() + 1][start.getSecond()].initialDirection() == TrackType.RIGHT && section[start.getFirst()][start.getSecond()].finalDirection() == TrackType.RIGHT);
        else if (start.getSecond() < size - 1 && section[start.getFirst()][start.getSecond() + 1] != null)
            valid = (section[start.getFirst()][start.getSecond() + 1].initialDirection() == TrackType.UP && section[start.getFirst()][start.getSecond()].finalDirection() == TrackType.UP);

        if (!valid) {
            var tmp = start;
            start = end;
            end = tmp;
        }

        // generate a new random route from start to end
        // starting from the start, choose a random adjacent tile to lay down new track
        // start with a blank slate with only the start and end
        TrackType[][] newSection = new TrackType[size][size];

        newSection[start.getFirst()][start.getSecond()] = section[start.getFirst()][start.getSecond()];
        newSection[end.getFirst()][end.getSecond()] = section[end.getFirst()][end.getSecond()];

        var current = start;
        Pair<Integer, Integer> beforeOld = null;
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

            var old = current;
            if (possibleMoves.size() == 0) {
                //current should be adjacent to end. If it is not then something is very wrong and we should abort
                var xDiff = Math.abs(current.getFirst() - end.getFirst());
                var yDiff = Math.abs(current.getSecond() - end.getSecond());
                var adjacent = ((xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1));
                if (!adjacent)
                    return mutateSection(section, world, sectionX, sectionY);
                current = end;
            } else {
                int chosen = MathUtils.randomInteger(0, possibleMoves.size());
                current = possibleMoves.get(chosen);
            }

            //work out track type when moving from old to current
            if (old.getFirst() == current.getFirst() + 1) {
                newSection[current.getFirst()][current.getSecond()] = TrackType.LEFT;
            }
            if (old.getFirst() == current.getFirst() - 1) {
                newSection[current.getFirst()][current.getSecond()] = TrackType.RIGHT;
            }
            if (old.getSecond() == current.getSecond() + 1) {
                newSection[current.getFirst()][current.getSecond()] = TrackType.DOWN;
            }
            if (old.getSecond() == current.getSecond() - 1) {
                newSection[current.getFirst()][current.getSecond()] = TrackType.UP;
            }
            if (beforeOld != null) {
                if (beforeOld.getFirst().equals(old.getFirst()) && !old.getFirst().equals(current.getFirst())) {
                    //old should be a turner
                    if (current.getFirst() == old.getFirst() + 1 && beforeOld.getSecond() == old.getSecond() + 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.DOWN_RIGHT;
                    }
                    if (current.getFirst() == old.getFirst() + 1 && beforeOld.getSecond() == old.getSecond() - 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.UP_RIGHT;
                    }
                    if (current.getFirst() == old.getFirst() - 1 && beforeOld.getSecond() == old.getSecond() + 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.DOWN_LEFT;
                    }
                    if (current.getFirst() == old.getFirst() - 1 && beforeOld.getSecond() == old.getSecond() - 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.UP_LEFT;
                    }

                }
                if (beforeOld.getSecond().equals(old.getSecond()) && !old.getSecond().equals(current.getSecond())) {
                    //old should be a turner
                    if (current.getSecond() == old.getSecond() + 1 && beforeOld.getFirst() == old.getFirst() + 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.LEFT_UP;
                    }
                    if (current.getSecond() == old.getSecond() + 1 && beforeOld.getFirst() == old.getFirst() - 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.RIGHT_UP;
                    }
                    if (current.getSecond() == old.getSecond() - 1 && beforeOld.getFirst() == old.getFirst() + 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.LEFT_DOWN;
                    }
                    if (current.getSecond() == old.getSecond() - 1 && beforeOld.getFirst() == old.getFirst() - 1) {
                        newSection[old.getFirst()][old.getSecond()] = TrackType.RIGHT_DOWN;
                    }
                }
            }
            beforeOld = old;

        }

        // insert it back into place
        for (int j = 0; j < size; j++) {
            System.arraycopy(newSection[j], 0, world[sectionX + j], sectionY, size);
        }

        //adjust start and end
        start = new Pair<>(start.getFirst() + sectionX, start.getSecond() + sectionY);
        end = new Pair<>(end.getFirst() + sectionX, end.getSecond() + sectionY);

        fixCorners(world, start);
        fixCorners(world, end);

        return world;
    }

    private static void fixCorners(TrackType[][] world, Pair<Integer, Integer> pos) {
        fixCorners(world, pos, -1, 0, 0, 1, TrackType.RIGHT, TrackType.UP, TrackType.RIGHT_UP);
        fixCorners(world, pos, -1, 0, 0, -1, TrackType.RIGHT, TrackType.DOWN, TrackType.RIGHT_DOWN);
        fixCorners(world, pos, 1, 0, 0, 1, TrackType.LEFT, TrackType.UP, TrackType.LEFT_UP);
        fixCorners(world, pos, 1, 0, 0, -1, TrackType.LEFT, TrackType.DOWN, TrackType.LEFT_DOWN);

        fixCorners(world, pos, 0, 1, 1, 0, TrackType.DOWN, TrackType.RIGHT, TrackType.DOWN_RIGHT);
        fixCorners(world, pos, 0, -1, 1, 0, TrackType.UP, TrackType.RIGHT, TrackType.UP_RIGHT);
        fixCorners(world, pos, 0, 1, -1, 0, TrackType.DOWN, TrackType.LEFT, TrackType.DOWN_LEFT);
        fixCorners(world, pos, 0, -1, -1, 0, TrackType.UP, TrackType.LEFT, TrackType.UP_LEFT);
    }

    private static void fixCorners(TrackType[][] world, Pair<Integer, Integer> pos, int horizontalBefore, int verticalBefore, int horizontalAfter, int verticalAfter, TrackType before, TrackType after, TrackType corner) {
        var prevPiece = ArrayUtil.safeGet(world, pos.getFirst() + horizontalBefore, pos.getSecond() + verticalBefore);
        var nextPiece = ArrayUtil.safeGet(world, pos.getFirst() + horizontalAfter, pos.getSecond() + verticalAfter);
        if (prevPiece == before && nextPiece != null && nextPiece.initialDirection() == after) {
            world[pos.getFirst()][pos.getSecond()] = corner;
        }
    }


    @Override
    public void render() {

    }

    @Override
    public Shader getShader() {
        return null;
    }

}
