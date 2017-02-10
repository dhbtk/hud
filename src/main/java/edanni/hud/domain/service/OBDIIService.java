package edanni.hud.domain.service;

import edanni.hud.infrastructure.controls.OBDIIDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by eduardo on 10/02/17.
 */
public class OBDIIService implements Runnable, Closeable
{
    private final OBDIIDisplay obdiiDisplay;
    private final File file;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean closed = false;
    private static final Logger LOG = LoggerFactory.getLogger( OBDIIService.class );

    public OBDIIService( String fileName, OBDIIDisplay obdiiDisplay ) throws IOException
    {
        this.obdiiDisplay = obdiiDisplay;

        this.file = new File( fileName );
        this.reader = new BufferedReader( new FileReader( file ) );
        this.writer = new BufferedWriter( new FileWriter( file, true ) );
    }

    @Override
    public void run()
    {
        int timer = 0;
        LOG.info( "Starting up" );
        send( "ATZ" );
        send( "ATSP0" );
        while ( !closed )
        {
            if ( timer == 1000 )
            {
                timer = 0;
                LOG.info( "Resetting" );
            }

            if ( timer % 50 == 0 )
            {
                // send 010C (RPM)
                // send 010D (speed)
                send( "010C" );
                send( "010D" );
            }
            try
            {
                while ( reader.ready() )
                {
                    String line = reader.readLine();
                    handleLine( normalizeLine( line ) );
                }
            }
            catch ( IOException e )
            {
                attemptReopen();
            }
            try
            {
                Thread.sleep( 5 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
            timer += 5;
        }
        LOG.info( "Shutting down" );
    }

    @Override
    public void close() throws IOException
    {
        this.closed = true;
        this.reader.close();
        this.writer.close();
    }

    private void send( String s )
    {
        try
        {
            this.writer.write( s + "\n" );
            this.writer.flush();
        }
        catch ( IOException e )
        {
            attemptReopen();
        }
    }

    private String normalizeLine( String line )
    {
        return line.replaceAll( "\\s+", "" );
    }

    private void handleLine( String line )
    {
        if ( line.startsWith( "410C" ) ) // RPM
        {
            int rpm = Integer.valueOf( line.substring( 4 ), 16 ) / 4;
            obdiiDisplay.setRPM( rpm );
        }
        else if ( line.startsWith( "410D" ) )
        {
            int speed = Integer.valueOf( line.substring( 4 ), 16 );
            obdiiDisplay.setSpeed( speed );
        }
    }

    private void attemptReopen()
    {
        try
        {
            this.reader.close();
            this.writer.close();
            this.reader = new BufferedReader( new FileReader( file ) );
            this.writer = new BufferedWriter( new FileWriter( file, true ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}
