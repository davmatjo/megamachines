package com.battlezone.megamachines.networking;

import java.util.LinkedList;

public class PathFind {
    static int isPath(int row, int column, int[][] layout) {
        // Declare direction arrays (north, south, west, east)
        int[] dirX = { 1, 0, -1, 0};
        int[] dirY = { 0, 1, 0, -1};

        // Define a linked list to keep the future coordinates to go through
        LinkedList<Integer> queueX = new LinkedList<>();
        LinkedList<Integer> queueY = new LinkedList<>();

        // Start by putting your coordinate on 0 and adding it to the queue
        queueX.add(0);
        queueY.add(0);

        // Loop until queue is empty
        while(!queueX.isEmpty()) {
            int andyX = queueX.pop();
            int andyY = queueY.pop();

            // Check if Andy found the shelf
            if ( layout[andyX][andyY] == 9 )
                return 1;

            // Check if Andy is on invalid position
            if ( layout[andyX][andyY] == 0 )
                return 0;

            // Make the visited place unreachable
            layout[andyX][andyY] = 0;

            // Add new paths for each
            for ( int i = 0; i < dirX.length; i++ ) {
                // Get new coordinates of Andy possible moves
                int newX = andyX + dirX[i];
                int newY = andyY + dirY[i];

                // If coordinates are invalid, break
                if( newX < 0 || newY < 0 || newX >= row || newY >= column )
                    continue;
                // Check if Andy can move there, if yes, add to queue
                if ( layout[newX][newY] == 1 ) {
                    queueX.add(newX);
                    queueY.add(newY);
                }
                // If new coordinates have the desired shelf, return 1
                else if ( layout[newX][newY] == 9 )
                    return 1;
            }
        }

        // If queue gets empty, return 0 because the shelf is unreachable
        return 0;
    }

    public static void main(String[] args) {
        // Please ignore this code, I needed it to solve an assessment centre problem in 30 mins :D
        int[][] l = new int[3][3];
        l[0][0] = 9; l[0][1] = 1; l[0][2] = 1;
        l[1][0] = 1; l[1][1] = 0; l[1][2] = 0;
        l[2][0] = 1; l[2][1] = 0; l[2][2] = 0;

        System.out.println(PathFind.isPath(3, 3, l));
    }
}
