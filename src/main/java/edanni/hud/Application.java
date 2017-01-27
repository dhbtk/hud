package edanni.hud;

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

    public static void main( String[] args ) throws Exception
    {
        launch( args );
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
        controller.runToMaxAndBack();
        stage.show();
    }
}
