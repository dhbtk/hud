package edanni.hud.infrastructure.controls;

/**
 * Created by eduardo on 03/02/17.
 */
public interface MusicDisplay
{
    void setFolder( String folder );

    void setSongInfo( String artist, String track, String album );

    void setPlayStatus( PlayStatus playStatus );

    enum PlayStatus
    {
        PLAYING,
        PAUSED
    }
}
