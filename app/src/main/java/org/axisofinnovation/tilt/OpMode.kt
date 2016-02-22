package org.axisofinnovation.tilt

import org.axisofinnovation.tilt.config.configure
import org.axisofinnovation.tilt.hardware.fetchHardware

/**
 * An extension of Qualcomm's OpMode class, this provides additional functionality that
 * may not have been possible before.
 */
abstract class OpMode : com.qualcomm.robotcore.eventloop.opmode.OpMode()
{

    /**
     * If you are looking to do initialization code, please
     * do it in the `initialize` method supplied by this class.
     */
    final override fun init()
    {
        // do the things we need to do
        fetchHardware( this );
        configure( OpMode::class.java, "" );

        // actually initialize the robot
        initialize();
    }

    //
    // Abstract
    //

    abstract fun initialize();

    abstract override fun loop();

}