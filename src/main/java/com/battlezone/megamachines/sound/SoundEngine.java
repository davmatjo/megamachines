package com.battlezone.megamachines.sound;

import com.battlezone.megamachines.events.game.CollisionEvent;
import com.battlezone.megamachines.events.game.GameStateEvent;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class SoundEngine {

    private IntBuffer buffer;
    private int backgroundMusicSource;
    private GameStateEvent.GameState lastGameState = GameStateEvent.GameState.MENU;

    public SoundEngine() {
        MessageBus.register(this);

        long device = ALC10.alcOpenDevice((ByteBuffer) null);

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

        contextAttribList.put(ALC_REFRESH);
        contextAttribList.put(60);

        contextAttribList.put(ALC_SYNC);
        contextAttribList.put(ALC_FALSE);

        contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttribList.put(2);

        contextAttribList.put(0);
        contextAttribList.flip();

        long newContext = ALC10.alcCreateContext(device, contextAttribList);

        if (!ALC10.alcMakeContextCurrent(newContext)) {
            return;
        }

        AL.createCapabilities(deviceCaps);

        buffer = BufferUtils.createIntBuffer(6400);
        AL10.alGenBuffers(buffer);

        startBackgroundMusic();
    }

    private void startBackgroundMusic() {
        var backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        backgroundMusicSource = playSound(new SoundEvent(soundFileForGameState(lastGameState), SoundEvent.PLAY_FOREVER, backgroundVolume));
    }

    private String soundFileForGameState(GameStateEvent.GameState state) {
        switch (state) {
            case PLAYING:
                return SoundFiles.IN_GAME_MUSIC;
            case MENU:
                return SoundFiles.MENU_MUSIC;
            default:
                return SoundFiles.MENU_MUSIC;
        }
    }

    @EventListener
    public void gameStateChanged(GameStateEvent event) {
        if (event.getNewState() != lastGameState) {
            // Change bg music
            stopSound(backgroundMusicSource);
            lastGameState = event.getNewState();
            startBackgroundMusic();
        }
    }

    @EventListener
    public void backgroundMusicVolumeChanged(SoundSettingsEvent event) {
        var backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        AL10.alSourcef(backgroundMusicSource, AL10.AL_GAIN, backgroundVolume);
    }

    @EventListener
    public void collision(CollisionEvent event) {
        playSound(new SoundEvent(SoundFiles.CRASH_SOUND, SoundEvent.PLAY_ONCE, 1f, event.getCollisionCoordinates(), new Vector2f(0, 0)));
    }

    private ArrayList<Integer> playingSounds = new ArrayList<>();

    @EventListener
    public int playSound(SoundEvent event) {
        AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f);
        AL10.alListener3f(AL10.AL_ORIENTATION, 0f, 0f, -1f);

        final int source = AL10.alGenSources();
        var highest = playingSounds.stream().max(Comparator.naturalOrder());

        var next = highest.map(i -> i + 1).orElse(0);

        try {
            final long runtime = createBufferData(buffer.get(next * 8), event.getFileName());

            AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(next * 8));
            AL10.alSource3f(source, AL10.AL_POSITION, event.getPosition().x, event.getPosition().y, 0f);
            AL10.alSource3f(source, AL10.AL_VELOCITY, event.getVelocity().x, event.getVelocity().y, 0f);
            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);
            AL10.alSourcef(source, AL10.AL_GAIN, event.getVolume());
            AL10.alSourcePlay(source);

            if (event.getPlayTimeSeconds() != SoundEvent.PLAY_FOREVER)
                new Thread(() -> {
                    try {
                        if (event.getPlayTimeSeconds() == SoundEvent.PLAY_ONCE)
                            Thread.sleep(runtime);
                        else
                            Thread.sleep(event.getPlayTimeSeconds() * 1000);
                        stopSound(source);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            playingSounds.add(next);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return source;
    }

    public void stopSound(int source) {
        AL10.alSourceStop(source);
        AL10.alDeleteSources(source);
    }

    private long createBufferData(int p, String path) throws UnsupportedAudioFileException, IOException {
        var url = AssetManager.class.getResource(path);
        AudioInputStream stream = AudioSystem.getAudioInputStream(url);
        AudioFormat format = stream.getFormat();

        byte[] b = stream.readAllBytes();

        ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
        data.flip();

        AL10.alBufferData(p, getFormat(format), data, (int) format.getSampleRate());
        return (long) (1000f * stream.getFrameLength() / format.getFrameRate());
    }

    private int getFormat(AudioFormat format) {
        if (format.getChannels() == 1)
            if (format.getSampleSizeInBits() == 8)
                return AL10.AL_FORMAT_MONO8;
            else
                return AL10.AL_FORMAT_MONO16;

        if (format.getSampleSizeInBits() == 8)
            return AL10.AL_FORMAT_STEREO8;
        else
            return AL10.AL_FORMAT_STEREO16;
    }

}
