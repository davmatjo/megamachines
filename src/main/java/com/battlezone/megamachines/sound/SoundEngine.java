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
import java.util.HashMap;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class SoundEngine {

    private static SoundEngine soundEngine = new SoundEngine();

    private final int BUFFER_SIZE = 8;
    private final int TOTAL_BUFFERS = 256;
    private final int RESERVED_BUFFERS = 128;

    private IntBuffer buffer;
    private HashMap<Integer, Integer> bufferSourceMap = new HashMap<>();
    private int lastReservedBuffer = 0;
    private int lastBuffer = RESERVED_BUFFERS;

    private GameStateEvent.GameState lastGameState = GameStateEvent.GameState.MENU;
    private int backgroundMusicSource;

    private float sfxVolume = 1f;
    private float backgroundVolume = 1f;

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

        buffer = BufferUtils.createIntBuffer(BUFFER_SIZE * TOTAL_BUFFERS);
        AL10.alGenBuffers(buffer);

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
                stopSound(carSound.getSoundSource());
            }
        var sounds = new CarSound[cars.length];
        for (int i = 0; i < cars.length; i++) {
            var position = new Vector2f((float) cars[i].getCenterOfMassPosition().x, (float) cars[i].getCenterOfMassPosition().y);
            var sound = playSound(SoundFiles.ENGINE_SOUND, position, SoundEvent.PLAY_FOREVER, 0.2f, SoundEvent.VOLUME_SFX, new Vector2f(camera.getX(), camera.getY()));

            sounds[i] = new CarSound(cars[i], sound.getFirst(), sound.getSecond());
        }
        this.carSounds = sounds;
    }

    public void update() {
        if (carSounds != null) {
            for (CarSound sound : carSounds) {
                //update volume
                var carPos = sound.getCar().getCenterOfMassPosition();
                float distanceSq = MathUtils.distanceSquared((float) carPos.x, (float) carPos.y, camera.getX(), camera.getY());
                var gain = getGain(0.7f, SoundEvent.VOLUME_SFX, distanceSq);
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
    public void soundSettingChanged(SoundSettingsEvent event) {
        reloadSettings();
        AL10.alSourcef(backgroundMusicSource, AL10.AL_GAIN, backgroundVolume);
    }

    public void collide(float force, Vector2f coordinates) {
        force = Math.abs(force);
        var maxForce = 200;
        playSound(SoundFiles.CRASH_SOUND, coordinates, SoundEvent.PLAY_ONCE, Math.min((force) / maxForce, 1), SoundEvent.VOLUME_SFX, new Vector2f(camera.getX(), camera.getY()));
    }

    @EventListener
    public Pair<Integer, Integer> playSound(SoundEvent event) {
        var playerPos = camera == null ? new Vector2f(0, 0) : new Vector2f(camera.getX(), camera.getY());
        return playSound(event.getFileName(), event.getPosition(), event.getPlayTimeSeconds(), event.getVolume(), SoundEvent.VOLUME_SFX, playerPos);
    }

    private float getGain(float volume, float volumeStream, float distanceSq) {
        if (volumeStream == SoundEvent.VOLUME_SFX) {
            volume *= sfxVolume;
        }

        float volumeScaled = distanceSq == 0 ? volume : Math.min(volume, volume / distanceSq * 40);
        if (volumeScaled < 0) {
            volumeScaled = 0;
        }
        return volumeScaled;
    }

    private int nextBufferIndex() {
        if (lastBuffer == TOTAL_BUFFERS - 2)
            lastBuffer = RESERVED_BUFFERS;
        else
            lastBuffer += 1;

        var oldSource = bufferSourceMap.get(lastBuffer);
        if (oldSource != null)
            stopSound(oldSource);
        return lastBuffer;
    }

    private int nextReservedBufferIndex() {
        if (lastReservedBuffer == RESERVED_BUFFERS - 2)
            lastReservedBuffer = 0;
        else
            lastReservedBuffer += 1;
        return lastReservedBuffer;
    }

    private Pair<Integer, Integer> playSound(String fileName, Vector2f position, int playTimeSeconds, float volume, int volumeStream, Vector2f playerPosition) {
        final int source = AL10.alGenSources();

        var bufferIndex = playTimeSeconds == SoundEvent.PLAY_FOREVER ? nextReservedBufferIndex() : nextBufferIndex();

        try {
            createBufferData(buffer.get(bufferIndex * BUFFER_SIZE), fileName);

            AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(bufferIndex * BUFFER_SIZE));

            float distanceSq = MathUtils.distanceSquared(position.x, position.y, playerPosition.x, playerPosition.y);

            var gain = getGain(volume, volumeStream, distanceSq);

            AL10.alSourcef(source, AL10.AL_GAIN, gain);
            AL10.alSourcePlay(source);

            if (playTimeSeconds == SoundEvent.PLAY_FOREVER)
                AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);
            else
                AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        bufferSourceMap.put(bufferIndex, source);
        return new Pair<>(source, bufferIndex);
    }

    private void stopSound(int source) {
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

}
