package com.battlezone.megamachines.sound;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameStateEvent;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.storage.Storage;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class SoundEngine {

    class CarSound {
        private RWDCar car;
        private int soundSource, bufferIndex;

        CarSound(RWDCar car, int soundSource, int bufferIndex) {
            this.car = car;
            this.soundSource = soundSource;
            this.bufferIndex = bufferIndex;
        }

        RWDCar getCar() {
            return car;
        }

        int getSoundSource() {
            return soundSource;
        }

        int getBufferIndex() {
            return bufferIndex;
        }
    }

    private IntBuffer buffer;
    private int backgroundMusicSource;
    private int backgroundMusicBuffer;
    private GameStateEvent.GameState lastGameState = GameStateEvent.GameState.MENU;

    private float sfxVolume = 1f;
    private float backgroundVolume = 1f;

    private static SoundEngine soundEngine = new SoundEngine();
    private Camera camera;
    private CarSound[] carSounds;

    private SoundEngine() {
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

        for (int i = 0; i < buffer.capacity() / 8; i++) {
            freeBuffers.add(i);
        }

        reloadSettings();
        startBackgroundMusic();
    }

    public static SoundEngine getSoundEngine() {
        return soundEngine;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setCars(RWDCar[] cars) {
        //clear old sounds
        if (this.carSounds != null)
            for (CarSound carSound : this.carSounds) {
                stopSound(carSound.getSoundSource(), carSound.getBufferIndex());
            }
        var sounds = new CarSound[cars.length];
        for (int i = 0; i < cars.length; i++) {
            var position = new Vector2f(cars[i].getCenterOfMassPosition().getFirst().floatValue(), cars[i].getCenterOfMassPosition().getSecond().floatValue());
            var sound = playSound(SoundFiles.ENGINE_SOUND, position, new Vector2f(0, 0), SoundEvent.PLAY_FOREVER, sfxVolume, new Vector2f(camera.getX(), camera.getY()));

            sounds[i] = new CarSound(cars[i], sound.getFirst(), sound.getSecond());
        }
        this.carSounds = sounds;
    }

    public void update() {
        if (carSounds != null) {
            for (CarSound sound : carSounds) {
                //update volume
                var carPos = sound.getCar().getCenterOfMassPosition();
                float distanceSq = MathUtils.distanceSquared(carPos.getFirst().floatValue(), carPos.getSecond().floatValue(), camera.getX(), camera.getY());
                var gain = getGain(sfxVolume, distanceSq);
                AL10.alSourcef(sound.getSoundSource(), AL10.AL_GAIN, gain);
                AL10.alSourcef(sound.getSoundSource(), AL10.AL_PITCH, 1f + (float) /*sound.car.getSpeed() / 30f */(sound.getCar().getGearbox().getNewRPM() - 1500f) / 2500f);
            }
        }
    }

    private void reloadSettings() {
        backgroundVolume = Storage.getStorage().getFloat(Storage.BACKGROUND_MUSIC_VOLUME, 1);
        sfxVolume = Storage.getStorage().getFloat(Storage.SFX_VOLUME, 1);
    }

    private void startBackgroundMusic() {
        var backgroundMusic = playSound(new SoundEvent(soundFileForGameState(lastGameState), SoundEvent.PLAY_FOREVER, backgroundVolume));
        backgroundMusicSource = backgroundMusic.getFirst();
        backgroundMusicBuffer = backgroundMusic.getSecond();
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
            stopSound(backgroundMusicSource, backgroundMusicBuffer);
            lastGameState = event.getNewState();
            startBackgroundMusic();
        }
    }

    @EventListener
    public void soundSettingChanged(SoundSettingsEvent event) {
        reloadSettings();
        AL10.alSourcef(backgroundMusicSource, AL10.AL_GAIN, backgroundVolume);
    }

    public void collide(float force, Vector2f coordinates) {
        force = Math.abs(force);
        playSound(SoundFiles.CRASH_SOUND, coordinates, new Vector2f(0, 0), SoundEvent.PLAY_ONCE, (force / 250000) * sfxVolume, new Vector2f(camera.getX(), camera.getY()));
    }

    private ConcurrentLinkedQueue<Integer> freeBuffers = new ConcurrentLinkedQueue<>();

    @EventListener
    public Pair<Integer, Integer> playSound(SoundEvent event) {
        return playSound(event.getFileName(), event.getPosition(), event.getVelocity(), event.getPlayTimeSeconds(), event.getVolume(), new Vector2f(0, 0));
    }

    private float getGain(float volume, float distanceSq) {
        if (volume == SoundEvent.VOLUME_SFX) {
            volume = sfxVolume;
        }

        float volumeScaled = distanceSq == 0 ? volume : Math.min(volume, volume / distanceSq * 20);
        if (volumeScaled < 0) {
            volumeScaled = 0;
        }
        return volumeScaled;
    }

    private Pair<Integer, Integer> playSound(String fileName, Vector2f position, Vector2f velocity, int playTimeSeconds, float volume, Vector2f playerPosition) {

        final int source = AL10.alGenSources();

        var next = freeBuffers.poll();

        try {
            final long runtime = createBufferData(buffer.get(next * 8), fileName);

            AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(next * 8));

            float distanceSq = MathUtils.distanceSquared(position.x, position.y, playerPosition.x, playerPosition.y);

            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);
            AL10.alSourcef(source, AL10.AL_GAIN, getGain(volume, distanceSq));
            AL10.alSourcePlay(source);

            if (playTimeSeconds != SoundEvent.PLAY_FOREVER)
                new Thread(() -> {
                    try {
                        if (playTimeSeconds == SoundEvent.PLAY_ONCE)
                            Thread.sleep(runtime);
                        else
                            Thread.sleep(playTimeSeconds * 1000);
                        stopSound(source, next);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();


        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(source, next);
    }

    private void stopSound(int source, int bufferIndex) {
        freeBuffers.add(bufferIndex);
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
