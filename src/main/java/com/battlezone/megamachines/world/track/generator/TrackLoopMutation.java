package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.battlezone.megamachines.world.track.Track.getAround;
import static com.battlezone.megamachines.world.track.Track.getTrue;

/**
 * A track generator which generates a square and then applies random transformations to it to add excitement
 */
public class TrackLoopMutation extends TrackGenerator {

    public TrackLoopMutation(int tracksAcross, int tracksDown) {
        super(tracksAcross, tracksDown);
    }

    @Override
    void generateMap() {
        boolean[][] boolGrid = gen(tracksAcross, tracksDown);
        grid = Track.createFromBoolGrid(boolGrid);
    }

    /**
     * Generates the grid
     * @param tracksAcross the number of tracks across in the grid
     * @param tracksDown the number of tracks down in the grid
     * @return
     */
    private boolean[][] gen(int tracksAcross, int tracksDown) {
        boolean[][] grid = new boolean[tracksAcross][tracksDown];

        // do the sides
        for (int i = 0; i < tracksAcross; i++) {
            grid[i][0] = true;
            grid[i][tracksDown - 1] = true;
        }

        for (int i = 0; i < tracksDown; i++) {
            grid[0][i] = true;
            grid[tracksAcross - 1][i] = true;
        }

        // choose 2 random true pieces and reroute
        var trues = getTrue(grid).toArray();
        var first = (Pair<Integer, Integer>) ArrayUtil.randomElement(trues);
        var second = (Pair<Integer, Integer>) ArrayUtil.randomElement(trues);

        if (MathUtils.pythagoras(first.getFirst(), first.getSecond(), second.getFirst(), second.getSecond()) < tracksAcross) {
            return gen(tracksAcross, tracksDown);
        }


        //reroute first to second
        if (reroute(grid, first, second)) {
            return grid;
        } else {
            return gen(tracksAcross, tracksDown);
        }
    }

    private boolean reroute(boolean[][] grid, Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        // follow the track around from start to end and delete all that track. Then make new route
        clearBetween(first, grid, true, second);

        //find a new route from first to second
        // start at first, find the possibles positions we could move to, choose one, move to it, repeat until at second

        var pos = first;
        while (!pos.equals(second)) {
            var possibleMoves = possibleMoves(grid, pos, second);
            // choose a move, move to it
            if (getAround(grid, pos).contains(second)) {
                return true;
            }
            if (possibleMoves.size() == 0) {
                // are we adjacent to the final piece? Then we're done
                return getAround(grid, pos).contains(second);
            }
            var move = (Pair<Integer, Integer>) ArrayUtil.randomElement(possibleMoves.toArray());
            grid[move.getFirst()][move.getSecond()] = true;
            pos = move;
        }
        return true;
    }

    private ArrayList<Pair<Integer, Integer>> possibleMoves(boolean[][] grid, Pair<Integer, Integer> pos, Pair<Integer, Integer> end) {
        var pieces = blankPiecesAround(grid, pos);

        return pieces.stream().filter((move) -> {
                    //the move is only valid if there is nothing adjacent to this new piece other than pos, or end piece
                    var around = getAround(grid, move);

                    if (around.size() == 1 && around.get(0).equals(pos)) {
                        return true;
                    }
                    if (around.size() == 2) {
                        // one should be pos, one should be end
                        return (around.get(0).equals(pos) && around.get(1).equals(end)) || (around.get(0).equals(end) && around.get(1).equals(pos));
                    }
                    return false;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<Pair<Integer, Integer>> blankPiecesAround(boolean[][] grid, Pair<Integer, Integer> pos) {
        var res = new ArrayList<Pair<Integer, Integer>>();

        // look at the 4 places around me. See which ones are false. Those are valid moves
        var left = new Pair<>(pos.getFirst() - 1, pos.getSecond());
        var right = new Pair<>(pos.getFirst() + 1, pos.getSecond());
        var above = new Pair<>(pos.getFirst(), pos.getSecond() + 1);
        var below = new Pair<>(pos.getFirst(), pos.getSecond() - 1);

        if (Boolean.FALSE.equals(ArrayUtil.safeGet(grid, left.getFirst(), left.getSecond())))
            res.add(left);
        if (Boolean.FALSE.equals(ArrayUtil.safeGet(grid, right.getFirst(), right.getSecond())))
            res.add(right);
        if (Boolean.FALSE.equals(ArrayUtil.safeGet(grid, above.getFirst(), above.getSecond())))
            res.add(above);
        if (Boolean.FALSE.equals(ArrayUtil.safeGet(grid, below.getFirst(), below.getSecond())))
            res.add(below);

        return res;
    }


    private void clearBetween(Pair<Integer, Integer> pos, boolean[][] grid, boolean first, Pair<Integer, Integer> upto) {
        var around = getAround(grid, pos);

        // delete self, delete one of the around, then run delete on that
        if (!first)
            grid[pos.getFirst()][pos.getSecond()] = false;

        if (around.size() > 0) {
            var next = around.get(0);
            if (next.equals(upto)) {
                return;
            }
            grid[next.getFirst()][next.getSecond()] = false;
            clearBetween(next, grid, false, upto);
        }

    }

}
