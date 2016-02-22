package org.axisofinnovation.tilt.hardware

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.axisofinnovation.tilt.utils.FieldFilter
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

/**
 * Fetches all of the hardware by the names in their annotations
 *
 * @param opMode
 *          The opmode to search hardware for.
 */
fun fetchHardware( opMode: OpMode )
{
    Log.d( "tilt.HardwareFetcher", "Fetching hardware in ${opMode.javaClass.simpleName}" );

    // all the hardware fields
    val fieldFilter = FieldFilter( opMode ).with( Hardware::class.java );

    // all the hardware maps in the hardware class
    val hardwareMaps = FieldFilter( opMode.hardwareMap ).getFieldsOfType( HardwareMap.DeviceMapping::class.java );

    // loop over each DeviceMapping
    for ( field in hardwareMaps )
    {
        // get the actual field value
        val map = field.get( opMode.hardwareMap ) as HardwareMap.DeviceMapping< * >;

        fetch( opMode, map, fieldFilter );
    }
}

/**
 * Actually sets the value of the field.
 *
 * @param opMode
 *          The OpMode we're modifying
 * @param list
 *          The DeviceMapping we're getting values from.
 * @param fieldFilter
 *          Used to find fields only matching the same type of DeviceMapping we're accessing.
 */
private fun fetch( opMode: OpMode, list: HardwareMap.DeviceMapping< * >, fieldFilter: FieldFilter )
{
    // gets the fields of the same type in the device mapping list
    // yes, this is terrible
    // I think it works
    val fields = fieldFilter.getFieldsOfType( ( list.javaClass.genericSuperclass as ParameterizedType ).actualTypeArguments[ 0 ].javaClass );

    // set the value of each field to the correct name
    fields.forEach { f -> f.set( opMode, list.get( getHardwareName( f ) ) ) };
}

/**
 * @return The value of the configName field in the Annotation if it was set, otherwise
 *         just the field name.
 */
fun getHardwareName( field: Field ): String
{
    val configName = field.getAnnotation( Hardware::class.java ).configName;
    if ( !configName.equals( " " ) ) return configName;
    return field.name;
}