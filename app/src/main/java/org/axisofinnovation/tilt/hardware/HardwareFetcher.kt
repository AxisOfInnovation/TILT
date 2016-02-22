package org.axisofinnovation.tilt.hardware

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.axisofinnovation.tilt.utils.FieldFilter

fun fetchHardware( clazz: Class< OpMode > )
{
    Log.d( "tilt.HardwareFetcher", "Fetching hardware in ${clazz.simpleName}" );

    // all the hardware fields
    val fieldFilter = FieldFilter( clazz ).with( Hardware::class.java );
}