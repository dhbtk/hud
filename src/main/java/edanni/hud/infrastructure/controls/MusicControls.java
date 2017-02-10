package edanni.hud.infrastructure.controls;

/**
 * Created by eduardo on 27/01/17.
 */
public interface MusicControls
{
    void playPause();

    void next();

    void previous();

    void nextFolder();

    void previousFolder();

    void increaseVolume();

    void decreaseVolume();

    void shutdown();
}
