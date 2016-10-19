/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.nasa.worldwindx;

import android.os.Bundle;
import android.view.Choreographer;

import gov.nasa.worldwind.Navigator;
import gov.nasa.worldwind.layer.ShowTessellationLayer;
import gov.nasa.worldwindx.experimental.AtmosphereLayer;

public class BasicStressTestActivity extends GeneralGlobeActivity implements Choreographer.FrameCallback {

    protected double cameraDegreesPerSecond = 0.1;

    protected long lastFrameTimeNanos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setAboutBoxTitle("About the " + this.getResources().getText(R.string.title_basic_stress_test));
        this.setAboutBoxText("Continuously moves the navigator in an Easterly direction from a low altitude.");


        // Initialize the Navigator so that it's looking in the direction of movement and the horizon is visible.
        Navigator navigator = this.getWorldWindow().getNavigator();
        navigator.setAltitude(1e3); // 1 km
        navigator.setHeading(90); // looking east
        navigator.setTilt(75); // looking at the horizon
    }

    /**
     * Adds the layers to the globe.
     */
    @Override
    protected void initializeLayers() {
        super.initializeLayers();

        // Add the ShowTessellation layer to provide some visual feedback regardless of texture details
        this.getWorldWindow().getLayers().addLayerBeforeNamed(AtmosphereLayer.LAYER_NAME, new ShowTessellationLayer());
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (this.lastFrameTimeNanos != 0) {
            // Compute the frame duration in seconds.
            double frameDurationSeconds = (frameTimeNanos - this.lastFrameTimeNanos) * 1.0e-9;
            double cameraDegrees = (frameDurationSeconds * this.cameraDegreesPerSecond);

            // Move the navigator to continuously bring new tiles into view.
            Navigator navigator = getWorldWindow().getNavigator();
            navigator.setLongitude(navigator.getLongitude() + cameraDegrees);

            // Redraw the World Window to display the above changes.
            this.getWorldWindow().requestRedraw();
        }

        Choreographer.getInstance().postFrameCallback(this);
        this.lastFrameTimeNanos = frameTimeNanos;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop running the animation when this activity is paused.
        Choreographer.getInstance().removeFrameCallback(this);
        this.lastFrameTimeNanos = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Use this Activity's Choreographer to animate the Navigator.
        Choreographer.getInstance().postFrameCallback(this);
        this.lastFrameTimeNanos = 0;
    }
}
