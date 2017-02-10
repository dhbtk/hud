package edanni.hud;

import edanni.hud.domain.service.OBDIIService;
import edanni.hud.ui.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends javafx.application.Application
{
    private static final Logger log = LoggerFactory.getLogger( Application.class );

    private static OBDIIService obdiiService;
    private static String fileName;

    public static void main( String[] args ) throws Exception
    {
        fileName = args.length == 1 ? args[0] : "/dev/pts/2";
        launch( args );
        obdiiService.close();
    }

    public void start( Stage stage ) throws Exception
    {
        log.info( "Starting HUD" );
        String fxmlFile = "/fxml/main.fxml";
        log.debug( "Loading FXML for main view from: {}", fxmlFile );
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load( getClass().getResourceAsStream( fxmlFile ) );
        MainController controller = loader.getController();

        log.debug( "Showing JFX scene" );
        Scene scene = new Scene( rootNode, 800, 480 );
        scene.getStylesheets().add( "/styles/styles.css" );
        stage.setTitle( "HUD" );
        stage.setScene( scene );
//        controller.runToMaxAndBack();
//        controller.setMusicControls( new MusicService( Paths.get( "/home/eduardo/Music" ), controller ) );
        obdiiService = new OBDIIService( fileName, controller );
        new Thread( obdiiService ).start();
        stage.show();
    }
}
