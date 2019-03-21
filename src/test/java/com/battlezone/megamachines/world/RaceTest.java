package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.battlezone.megamachines.world.ScaleController.RWDCAR_SCALE;
import static com.battlezone.megamachines.world.ScaleController.TRACK_SCALE;
import static com.battlezone.megamachines.world.track.TrackType.*;

public class RaceTest {

    Race race;
    Track track;
    int laps = 2;
    RWDCar c1, c2;
    List<RWDCar> cars = new ArrayList<>();

    @Before
    public void setup() {
        TrackType[][] grid = new TrackType[][]{
                {LEFT_UP, UP, UP, UP_RIGHT},
                {LEFT, null, null, RIGHT},
                {DOWN_LEFT, DOWN, DOWN, RIGHT_DOWN}
        };
        track = new Track(grid, 3, 0, 2);
        final List<Vector3f> startPos = track.getStartingPositions();
        c1 = new AffordThoroughbred(startPos.get(0).x, startPos.get(0).y, ScaleController.RWDCAR_SCALE, 0, Colour.WHITE_3, 0, 0);
        c2 = new AffordThoroughbred(startPos.get(1).x, startPos.get(1).y, ScaleController.RWDCAR_SCALE, 1, Colour.WHITE_3, 0, 0);
        cars.add(c1);
        cars.add(c2);
        c1.setControlsActive(true);
        c2.setControlsActive(true);
        race = new Race(track, laps, cars);
    }

    @Test
    public void generalTest() throws InterruptedException {
        // Check starting conditions
        {
            race.update();
            Assert.assertEquals(0, c1.getPosition());
            Assert.assertEquals(1, c2.getPosition());
            Assert.assertFalse(race.hasFinished());
            Assert.assertEquals(0, race.getFinalPositions().size());
        }
        // Check position changing
        {
            c1.setX(0);
            c1.setY(TRACK_SCALE / 2);
            c2.setX(0);
            c2.setY(TRACK_SCALE);
            race.update();
            Assert.assertEquals(0, c2.getPosition());
            Assert.assertEquals(1, c1.getPosition());
        }
        // Check lap changing
        {
            Assert.assertEquals(0, c1.getLap());
            Assert.assertEquals(0, c2.getLap());
            // Move c1 around the track
            for (var piece : track.getPieces()) {
                c1.setX(piece.getXf());
                c1.setY(piece.getYf());
                race.update();
            }
            Assert.assertEquals(1, c1.getLap());
        }
        // Throw a car off the track
        {
            c2.setX(-100);
            race.update();
            Assert.assertNotEquals(0, c2.getCurrentlyPlaying());
            Assert.assertEquals(0, c1.getCurrentlyPlaying());
        }
        // Recently finished player should be null
        {
            Assert.assertNull(race.getRecentlyFinished());
        }
        // Move c1 backwards again
        {
            Assert.assertEquals(1, c1.getLap());
            for (int i = track.getPieces().size(); i >= -1; i--) {
                final int index = MathUtils.wrap(i, 0, track.getPieces().size());
                final TrackPiece p = track.getPiece(index);
                c1.setX(p.getX());
                c1.setY(p.getY());
                race.update();
            }
            Assert.assertEquals(0, c1.getLap());
        }
        // Move c1 around the track to win
        {
            // On their first lap
            Assert.assertEquals(0, c1.getLap());
            for (var piece : track.getPieces()) {
                c1.setX(piece.getXf());
                c1.setY(piece.getYf());
                race.update();
            }
            // On their second lap
            for (var piece : track.getPieces()) {
                c1.setX(piece.getXf());
                c1.setY(piece.getYf());
                race.update();
            }
            // Finished their second lap
            for (var piece : track.getPieces()) {
                c1.setX(piece.getXf());
                c1.setY(piece.getYf());
                race.update();
            }
            Assert.assertEquals(3, c1.getLap());
        }
        // Check finished players
        {
            Assert.assertEquals(new Pair<>(c1, (byte) 0), race.getRecentlyFinished());
            Assert.assertEquals(c1, race.getFinalPositions().get(0));
            // After first access, this should be null again
            Assert.assertNull(race.getRecentlyFinished());
        }
        // Check a car with its corners on the track
        {
            // Stop animations
            c2.setCurrentlyPlaying(0);
            c2.setControlsActive(true);
            final double pos = (TRACK_SCALE + RWDCAR_SCALE / 2) / 2;
            c2.setX(pos);
            c2.setY(pos);
            c2.setAngle(135);
            race.update();
            // If it has fallen off, this will not be 0
            Assert.assertEquals(0, c2.getCurrentlyPlaying());
        }
        // Finish the race with c2
        {
            // On their first lap
            Assert.assertEquals(0, c2.getLap());
            for (var piece : track.getPieces()) {
                c2.setX(piece.getXf());
                c2.setY(piece.getYf());
                race.update();
            }
            // On their second lap
            for (var piece : track.getPieces()) {
                c2.setX(piece.getXf());
                c2.setY(piece.getYf());
                race.update();
            }
            // Finished their second lap
            for (var piece : track.getPieces()) {
                c2.setX(piece.getXf());
                c2.setY(piece.getYf());
                race.update();
            }
            Assert.assertEquals(c2, race.getRecentlyFinished().getFirst());
            Assert.assertEquals(2, race.getFinalPositions().size());
            Assert.assertTrue(race.hasFinished());
        }
    }
}
