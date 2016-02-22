package org.axisofinnovation.tilt

import android.support.annotation.CallSuper;
import org.axisofinnovation.tilt.hardware.fetchHardware

/**
 * An extension of Qualcomm's OpMode class, this provides additional functionality that
 * may not have been possible before.
 */
abstract class OpMode : com.qualcomm.robotcore.eventloop.opmode.OpMode()
{

    //
    // Overrides
    //

    @CallSuper
    override fun init()
    {
        fetchHardware( this );
    }

}