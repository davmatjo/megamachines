package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.renderer.ui.menu.TrackOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackStorageManager {

    private final static String EXTENSION = ".track";

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
        String path = folderPath + File.separator + fileName + EXTENSION;
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

    public List<TrackOption> getTrackOptions() {
        var tracks = new ArrayList<TrackOption>();

        Path rootPath = getPath();
        try {
            Files.list(rootPath).forEach(filePath -> {
                Track t = trackFromPath(filePath);
                if (t != null)
                    tracks.add(new TrackOption(getFileName(filePath), t));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tracks;
    }

    private String getFileName(Path path) {
        String filename = path.getFileName().toString();
        if (filename.endsWith(EXTENSION)) {
            filename = filename.substring(0, filename.length() - EXTENSION.length());
        }
        return filename;
    }

    private Track trackFromPath(Path path) {
        try {
            var bytes = Files.readAllBytes(path);
            return Track.fromByteArray(bytes, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void rename(String oldName, String newName) {
        var oldPath = getFolder().getAbsolutePath() + File.separator + oldName + EXTENSION;
        var newPath = getFolder().getAbsolutePath() + File.separator + newName + EXTENSION;

        var oldFile = new File(oldPath);
        var newFile = new File(newPath);
        oldFile.renameTo(newFile);
    }
}
