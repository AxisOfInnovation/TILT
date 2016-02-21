package org.axisofinnovation.tilt.register

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister
import org.axisofinnovation.tilt.config.configure
import org.axisofinnovation.tilt.utils.ClassFilter

class OpModeRegistrar() : OpModeRegister
{

    /**
     * Registers the OpModes.
     */
    override fun register( manager: OpModeManager )
    {
        Log.d( "tilt.OpModeRegistrar", "Discovering OpModes!" );

        // the op modes that are:
        // - instantiable
        // - subclasses of OpMode
        // - have the @RegisterOpMode annotation
        val opModeClasses = ClassFilter().subclassOf( OpMode::class.java ).with( RegisterOpMode::class.java ).get();

        // register all the OpModes with the manager
        for( opMode in opModeClasses )
        {
            val annotationName = opMode.getAnnotation( RegisterOpMode::class.java ).value;

            // the name the
            val name =
                    if ( annotationName.isEmpty() )
                    {
                        Log.w( "tilt.OpModeRegistrar", "The OpMode defined in ${opMode.simpleName} does not have a name!" );
                        opMode.simpleName;
                    }
                    else if ( annotationName.equals( "StopRobot" ) )
                    {
                        Log.w("tilt.OpModeRegistrar", "The OpMode defined in ${opMode.simpleName} cannot have the name 'StopRobot'!");
                        opMode.simpleName; // just go with the name of the class instead
                    }
                    else if ( annotationName.contains( "=" ) )
                    {
                        Log.w( "tilt.OpModeRegistrar", "The OpMode defined in ${opMode.simpleName} cannot have a '=' in the name!" );
                        opMode.simpleName;
                    }
                    else
                    {
                        annotationName;
                    }

            Log.i( "tilt.OpModeRegistrar", "Registered OpMode class ${opMode.simpleName} as $name" );
            manager.register( name, opMode );

            // configure the properties in the OpMode now
            // (ignore the cast, the subclassOf( ... ) filter ensures this is true
            configure( opMode as Class< out OpMode >, name );
        }
    }

}