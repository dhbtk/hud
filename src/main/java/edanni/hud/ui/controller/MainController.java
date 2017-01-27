package edanni.hud.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;

/**
 * Created by eduardo on 24/01/17.
 */
public class MainController
{
    private static final int MAX_RPM = 7000;
    private static final int MAX_WIDTH = 700;
    // view stuff
    @FXML
    private Canvas canvas;
    @FXML
    private Label dateLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private TextField rpmField;

    private Image[] sevenSegmentImages = new Image[10];

    private int oldRpm = 0;

    public MainController()
    {
        for ( int i = 0; i < 10; i++ )
        {
            sevenSegmentImages[i] = new Image( getClass().getResourceAsStream( "/images/7segment/" + i + ".png" ) );
        }
    }

    public void onClick()
    {
        String text = rpmField.getText();
        int rpm = Integer.parseInt( text );
        new Thread( () ->
        {
            if ( rpm > oldRpm )
            {
                for ( int i = oldRpm; i <= rpm; i += 50 )
                {
                    final int currentRpm = i;
                    Platform.runLater( () -> drawRPMAndSpeed( currentRpm, 0 ) );
                    try
                    {
                        Thread.sleep( 5 );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                for ( int i = oldRpm; i >= rpm; i -= 50 )
                {
                    final int currentRpm = i;
                    Platform.runLater( () -> drawRPMAndSpeed( currentRpm, 0 ) );
                    try
                    {
                        Thread.sleep( 5 );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
            oldRpm = rpm;
        } ).start();
    }

    public void drawRPMAndSpeed( int rpm, int speed )
    {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke( Color.AZURE );
        graphicsContext.setFill( Color.BLACK );
        // reset
        graphicsContext.fillRect( 0, 0, 800, 400 );

        drawRPM( graphicsContext, rpm );
        drawSpeed( graphicsContext, speed );
    }

    private void drawRPM( GraphicsContext graphicsContext, int rpm )
    {
        double percent = ((double) rpm) / MAX_RPM;

        int MAX_BARS = (MAX_RPM / 1000) * 15; // we only draw the even-numbered bars
        final double MAX_ANGLE = 120;
        final double MIN_ANGLE = (MAX_ANGLE - 90) * 2;
        double ANGLE_SPAN = MIN_ANGLE / MAX_BARS;
        final double X_RADIUS = 790;
        final double Y_RADIUS = 250;
        final double X_INNER_RADIUS = X_RADIUS * 0.9;
        final double Y_INNER_RADIUS = Y_RADIUS * 0.9;
        final double X_LETTER_RADIUS = X_RADIUS * 0.85;
        final double Y_LETTER_RADIUS = Y_RADIUS * 0.85;
        final double X_CENTER = 400;
        final double Y_CENTER = 265;

        // drawing line under the gauge
        graphicsContext.setStroke( Color.WHITE );
        graphicsContext.setLineWidth( 5 );
        double lineX[] = new double[MAX_BARS + 1];
        double lineY[] = new double[MAX_BARS + 1];
        for ( int i = 0; i <= MAX_BARS; i++ )
        {
            double leftAngle = Math.toRadians( MAX_ANGLE - ANGLE_SPAN * i );
            lineX[i] = Math.cos( leftAngle ) * X_INNER_RADIUS + X_CENTER;
            lineY[i] = Y_CENTER - Math.sin( leftAngle ) * Y_INNER_RADIUS;
        }
        graphicsContext.strokePolyline( lineX, lineY, MAX_BARS + 1 );

        int bars = (int) (percent * MAX_BARS);
        for ( int i = 0; i < MAX_BARS; i++ )
        {
            if ( i % 2 == 0 && i < bars )
            {
                final double STEP1 = 2500;
                final double STEP2 = 4000;
                final double STEP3 = 5750;

                double currentPercent = ((double) i) / MAX_BARS;
                double currentRPM = currentPercent * MAX_RPM;
                if ( currentRPM > STEP3 )
                {
                    graphicsContext.setFill( Color.RED );
                }
                else if ( currentRPM > STEP2 )
                {
                    graphicsContext.setFill( Color.ORANGERED.interpolate( Color.RED, (currentRPM - STEP2) / (STEP3 - STEP2) ) );
                }
                else if ( currentRPM > STEP1 )
                {
                    graphicsContext.setFill( Color.ORANGE.interpolate( Color.ORANGERED, (currentRPM - STEP1) / (STEP2 - STEP1) ) );
                }
                else
                {
                    graphicsContext.setFill( Color.ORANGE );
                }
            }
            else
            {
                graphicsContext.setFill( Color.BLACK );
            }
            double leftAngle = Math.toRadians( MAX_ANGLE - ANGLE_SPAN * i );
            double rightAngle = Math.toRadians( MAX_ANGLE - ANGLE_SPAN * (i + 1) );
            double x1, y1, x2, y2; // left, bottom then up
            double x3, y3, x4, y4; // right, up then bottom
            // x is cos and y is sin!!
            x1 = Math.cos( leftAngle ) * X_INNER_RADIUS + X_CENTER;
            y1 = Y_CENTER - Math.sin( leftAngle ) * Y_INNER_RADIUS;
            x2 = Math.cos( leftAngle ) * X_RADIUS + X_CENTER;
            y2 = Y_CENTER - Math.sin( leftAngle ) * Y_RADIUS;
            x3 = Math.cos( rightAngle ) * X_RADIUS + X_CENTER;
            y3 = Y_CENTER - Math.sin( rightAngle ) * Y_RADIUS;
            x4 = Math.cos( rightAngle ) * X_INNER_RADIUS + X_CENTER;
            y4 = Y_CENTER - Math.sin( rightAngle ) * Y_INNER_RADIUS;
            graphicsContext.fillPolygon( new double[]{x1, x2, x3, x4}, new double[]{y1, y2, y3, y4}, 4 );
        }

        // labels
        graphicsContext.setStroke( Color.WHITE );
        graphicsContext.setFontSmoothingType( FontSmoothingType.LCD );
        graphicsContext.setLineWidth( 1 );
        for ( int i = 0; i <= MAX_RPM; i += 1000 )
        {
            double labelPercent = ((double) i) / (MAX_RPM);
            double angle = Math.toRadians( MAX_ANGLE - MIN_ANGLE * labelPercent );

            graphicsContext.strokeText( String.valueOf( i / 1000 ), Math.cos( angle ) * X_LETTER_RADIUS + X_CENTER,
                Y_CENTER - Math.sin( angle ) * Y_LETTER_RADIUS );
        }
    }

    private void drawSpeed( GraphicsContext graphicsContext, int speed )
    {
        int units = speed % 10;
        int decimals = ((speed % 100) - units) / 10;
        int hundreds = (speed - decimals * 10 - units) / 100;

        // we are going to draw 3 of them. so we center them by setting the x position to 800/2 - (width*3)/2
        double imageWidth = sevenSegmentImages[0].getWidth() / 1.5;
        double imageHeight = sevenSegmentImages[0].getHeight() / 1.5;
        double kern = 13;
        double startX = 800 / 2 - (imageWidth * 3 - kern * 2) / 2;
        double startY = 60; // guesswork

        graphicsContext.drawImage( sevenSegmentImages[hundreds], startX, startY, imageWidth, imageHeight );
        graphicsContext.drawImage( sevenSegmentImages[decimals], startX + imageWidth - kern, startY, imageWidth, imageHeight );
        graphicsContext.drawImage( sevenSegmentImages[units], startX + imageWidth * 2 - kern * 2, startY, imageWidth, imageHeight );
    }

    public void runToMaxAndBack()
    {
        new Thread( () ->
        {
            final int MAX = 2000;
            // 5 ms per iteration, 1s in total
            for ( int i = 0; i <= MAX; i += 5 )
            {
                double rpm = Math.sin( ((double) i) / MAX * Math.PI ) * MAX_RPM;
                double speed = Math.sin( ((double) i) / MAX * Math.PI ) * 300;
                Platform.runLater( () -> drawRPMAndSpeed( (int) rpm, (int) speed ) );
                try
                {
                    Thread.sleep( 5 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
            Platform.runLater( () -> drawRPMAndSpeed( 0, 0 ) );
        } ).start();
    }
}
