package com.battlezone.megamachines.world.track;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrackStorageManager {

    private File getFolder() {
        File f = new File("user_tracks");
        f.mkdir();
        return f;
    }

    private Path getPath() {
        return getFolder().toPath();
    }

    private String generateFileName() {
        var formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
        return formatter.format(new Date());
    }

    private Path getPathForNewFile() {
        File folder = getFolder();
        String folderPath = folder.getAbsolutePath();
        String fileName = generateFileName();
        String path = folderPath + File.separator + fileName + ".track";
        return new File(path).toPath();
    }

    public void saveTrack(Track track) {
        byte[] bytes = track.toByteArray();
        Path path = getPathForNewFile();
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Track[] getTracks() {
        var tracks = new ArrayList<Track>();

        Path path = getPath();
        try {
            Files.list(path).forEach(file -> {
                Track t = trackFromFile(file);
                if (t != null)
                    tracks.add(t);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tracks.toArray(Track[]::new);
    }

    private Track trackFromFile(Path path) {
        try {
            var bytes = Files.readAllBytes(path);
            return Track.fromByteArray(bytes, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
