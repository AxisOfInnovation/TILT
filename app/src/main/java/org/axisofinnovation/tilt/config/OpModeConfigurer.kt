package org.axisofinnovation.tilt.config

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.axisofinnovation.tilt.utils.FieldFilter
import org.axisofinnovation.tilt.utils.getConfig
import org.axisofinnovation.tilt.utils.saveConfig

/**
 * Configures the fields marked with the @Configurable annotation
 * to how the ftc.config file reads.
 */
class OpModeConfigurer( private val clazz: Class< out OpMode > )
{

    //
    // Actions
    //

    /**
     * Configures the OpMode to match the values found in the `ProgramConfig`
     * for the given name.
     *
     * @param[name]
     *          The `ProgramConfig` name for the file.
     */
    fun configure( name: String )
    {
        Log.d( "tilt.OpModeConfigurer", "Configuring properties in $name" );

        val config = getConfig().getProgram( name ); // get the config for this program

        // all configurable properties in this
        val fieldFinder = FieldFilter(clazz).with( Configurable::class.java );

        // configure all those things
        configure( config, fieldFinder, Boolean::class.java );
        configure( config, fieldFinder, Int::class.java );
        configure( config, fieldFinder, Double::class.java );
        configure( config, fieldFinder, String::class.java );

        saveConfig(); // save the new configuration after each opmode
    }

    /**
     * Configures the properties of the provided type.
     *
     * @param[config]
     *          The `ProgramConfig` that we're working off of.
     * @param[fieldFinder]
     *          The `FieldFinder` for filtering properties.
     * @param[clazz]
     *          The class of the fields to search for.
     */
    private fun configure( config: ProgramConfig, fieldFinder: FieldFilter, clazz: Class< * > )
    {
        val fields = fieldFinder.getFieldsOfType( clazz );

        for ( field in fields )
        {
            val type = clazz.simpleName;
            val default = field.get( null );

            val newValue: Any =
                    if      ( default is Int )     config.get( field.name, default );
                    else if ( default is Double )  config.get( field.name, default );
                    else if ( default is Boolean ) config.get( field.name, default );
                    else                           config.get( field.name, default.toString() ); // everything else is interpreted as a string

            // change the value of the field to the new one
            field.set( null, newValue );

            Log.i( "tilt.OpModeConfigurer", "Set value of $type ${config.name}.${field.name} to $newValue" );
        }
    }

}