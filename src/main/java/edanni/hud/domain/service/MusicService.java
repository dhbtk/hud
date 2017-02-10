package edanni.hud.domain.service;

import edanni.hud.infrastructure.controls.MusicControls;
import edanni.hud.infrastructure.controls.MusicDisplay;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eduardo on 27/01/17.
 */
public class MusicService implements MusicControls
{
    private static final Logger log = LoggerFactory.getLogger( MusicService.class );
    private final Path baseDirectory;
    private final MusicDisplay musicDisplay;
    private final ArrayList<Path> folders = new ArrayList<>();

    private int currentDirectoryIndex;
    private int currentSongIndex;
    private MediaPlayer mediaPlayer = null;

    public MusicService( Path baseDirectory, MusicDisplay musicDisplay )
    {
        this.baseDirectory = baseDirectory;
        this.musicDisplay = musicDisplay;
        if ( !baseDirectory.toFile().exists() || !baseDirectory.toFile().isDirectory() )
        {
            throw new IllegalArgumentException( "baseDirectory não é um diretório ou não existe" );
        }

        addSubFolders( baseDirectory );
        log.debug( "Pastas encontradas:" );
        folders.forEach( folder -> log.debug( folder.toString() ) );
        currentDirectoryIndex = 0;
        currentSongIndex = 0;
        playSong( currentDirectoryIndex, currentSongIndex );
    }

    /**
     * Builds directory tree depth-first
     * @param path
     */
    private void addSubFolders( Path path )
    {
        folders.add( path );
        Arrays.stream( path.toFile().listFiles(
            file -> file.isDirectory() && file.listFiles( childFile -> childFile.isFile() && childFile.getName().endsWith( ".mp3" ) ).length > 0 ) )
              .forEach( file -> addSubFolders( file.toPath() ) );
    }

    private void playSong( int directoryIndex, int songIndex )
    {
        if ( mediaPlayer != null )
        {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        File[] files = folders.get( directoryIndex ).toFile().listFiles( childFile -> childFile.isFile() && childFile.getName().endsWith( ".mp3" ) );
        assert files != null && files.length > songIndex;
//        MediaPlayer player = new MediaPlayer( new Media( files[songIndex].toURI().toString() ) );
//        player.play();
    }

    @Override
    public void playPause()
    {

    }

    @Override
    public void next()
    {

    }

    @Override
    public void previous()
    {

    }

    @Override
    public void nextFolder()
    {

    }

    @Override
    public void previousFolder()
    {

    }

    @Override
    public void increaseVolume()
    {

    }

    @Override
    public void decreaseVolume()
    {

    }

    @Override
    public void shutdown()
    {

    }
}
