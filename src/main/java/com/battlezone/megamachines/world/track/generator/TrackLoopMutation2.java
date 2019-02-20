package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TrackLoopMutation2 extends TrackGenerator {

    public TrackLoopMutation2(int tracksAcross, int tracksDown) {
        super(tracksAcross, tracksDown);
    }

    @Override
    void generateMap() {
        boolean[][] boolGrid = gen();

        grid = convertToGrid(boolGrid);
        printGrid(grid);
        System.out.println("DONE");
    }

    private void printGrid(TrackType[][] grid) {

        String[][] str = new String[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                str[i][j] = grid[i][j] == null ? "  " : grid[i][j].toString();
            }
        }

        System.out.println(Arrays.deepToString((str)).replace("], ", "]\n").replace("[[", "[\n[").replace("]]", "]\n]").replace(",", "").replace("[", "").replace("]", ""));
    }

    private TrackType[][] convertToGrid(boolean[][] boolGrid) {
        var track = new TrackType[boolGrid.length][boolGrid[0].length];

        for (int i = 0; i < boolGrid.length; i++) {
            for (int j = 0; j < boolGrid[0].length; j++) {
                track[i][j] = boolGrid[i][j] ? TrackType.UP : null;
            }
        }

        var trues = getTrue(boolGrid).toArray();
        var start = (Pair<Integer, Integer>) ArrayUtil.randomElement(trues);

        var pos = start;
        var adjs = getAround(boolGrid, start);
        var next = adjs.get(0);
        var prev = pos;
        while (!next.equals(start)) {
            var type = getType(pos, next, prev);
            track[pos.getFirst()][pos.getSecond()] = type;
            prev = pos;
            pos = next;
            var around = getAround(boolGrid, pos);
            next = around.get(0);
            if(next.equals(prev))
                next = around.get(1);
            System.out.println(next);
        }

        return track;
    }

    private TrackType getType(Pair<Integer, Integer> pos, Pair<Integer, Integer> next, Pair<Integer, Integer> prev) {
        var initial = directionOf(pos, prev);
        var end = directionOf(next, pos);
        var type = TrackType.fromDirections(initial, end);
        return type;
    }

    private TrackType directionOf(Pair<Integer, Integer> pos, Pair<Integer, Integer> from) {
        if (pos.getFirst().equals(from.getFirst())) {
            if (pos.getSecond() - from.getSecond() == 1) {
                return TrackType.RIGHT;
            }
            return TrackType.LEFT;
        }

        if (pos.getFirst() - from.getFirst() == 1) {
            return TrackType.DOWN;
        }
        return TrackType.UP;
    }

    private boolean[][] gen() {
        var tracksAcross = 20;
        var tracksDown = 20;
        boolean[][] grid = new boolean[20][20];

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

        if (first.equals(second)) {
            return gen();
        }

        //reroute first to second
        if (reroute(grid, first, second)) {
            return grid;
        } else {
            return gen();
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
                    System.out.println("check if valid: " + move.toString());

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

    private ArrayList<Pair<Integer, Integer>> getAround(boolean[][] grid, Pair<Integer, Integer> pos) {
        var left = new Pair<>(pos.getFirst() - 1, pos.getSecond());
        var right = new Pair<>(pos.getFirst() + 1, pos.getSecond());
        var above = new Pair<>(pos.getFirst(), pos.getSecond() + 1);
        var below = new Pair<>(pos.getFirst(), pos.getSecond() - 1);

        var res = new ArrayList<Pair<Integer, Integer>>();

        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, left.getFirst(), left.getSecond())))
            res.add(left);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, right.getFirst(), right.getSecond())))
            res.add(right);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, above.getFirst(), above.getSecond())))
            res.add(above);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, below.getFirst(), below.getSecond())))
            res.add(below);


        return res;
    }

    private ArrayList<Pair<Integer, Integer>> getTrue(boolean[][] array) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j]) {
                    res.add(new Pair<>(i, j));
                }
            }
        }

        return res;
    }

}
