package org.axisofinnovation.tilt.config

import android.util.Log
import java.util.*

/**
 * The configuration for a program.
 */
class ProgramConfig( val name: String ) : Comparable< ProgramConfig >
{
    //
    // Fields
    //

    /**
     * The properties for the program.
     * Stored as a `TreeMap` because it will be sorted as it's added
     * so when it is iterated through when exporting it will already
     * be in order.
     */
    private val properties = object : HashMap< String, String >()
    {

        override fun put( key: String, value: String ): String?
        {
            return super.put( key.toLowerCase(), value )
        }

        override fun get( key: String ): String?
        {
            return super.get( key.toLowerCase() )
        }
    }

    //
    // Getters
    //

    /**
     * @return A copy of the properties map.
     */
    fun getMap(): HashMap< String, String > = HashMap( properties );

    /**
     * Gets the value associated with a key.
     *
     * @param[key]
     *          The name of the property.
     * @return The value of the property; however, in this case the data type is not known,
     *         but the value will be returned casted to the correct data type.
     */
    fun get( key: String ): Any?
    {
        val raw = properties[ key ] ?: "null_";

        if ( !raw.contains( "_" ) )
        {
            Log.e( "tilt.ProgramConfig", "No type prefix for $key! Skipping" );
            return "";
        }

        // the type of the value
        val type = raw.substring( 0, raw.indexOf( '_' ) );
        val value = raw.substring( type.length + 1 );

        // parse the data correctly
        return when( type )
        {
            "d"  -> value.toDouble();
            "i"  -> value.toInt();
            "b"  -> value.toBoolean();
            "s"  -> value;
            else -> null;
        }
    }

    /**
     * Gets a 64-bit floating point value out of the program properties.
     *
     * @param[key]
     *          THe name of the property.
     * @param[default]
     *          The default value of the property if the property was not present or
     *          was the wrong type.
     * @return The value of the key as a `Double` or `0.0` if the property was not found
     *         or was a different data type.
     */
    fun get( key: String, default: Double ): Double
    {
        return ensureDataType( key, Double::class.java, default ) as Double;
    }

    /**
     * Gets an integer value out of the program properties
     *
     * @param[key]
     *          The name of the property.
     * @param[default]
     *          The default value of the property if the property was not present or
     *          was the wrong type.
     * @return The value of the key as an `Int` or `0` if the property was not found
     *         or was a different data type.
     */
    fun get( key: String, default: Int ): Int
    {
        return ensureDataType( key, Int::class.java, default ) as Int;
    }

    /**
     * Gets a string value out of the program properties.
     *
     * @param[key]
     *          The name of the property.
     * @param[default]
     *          The default value of the property if the property was not present or
     *          was the wrong type.
     * @return The value of the key as a `String` or `default` if the property was not found
     *         or was a different data type.
     */
    fun get( key: String, default: String ): String
    {
        return ensureDataType( key, String::class.java, default ) as String;
    }

    /**
     * Gets a boolean value out of the program properties.
     *
     * @param[key]
     *          The name of the property.
     * @param[default]
     *          The default value of the property if the property was not present or
     *          was the wrong type.
     * @return The value of the key as a `Boolean` or `default` if the property was not found
     *         or was a different data type.
     */
    fun get( key: String, default: Boolean ): Boolean
    {
        return ensureDataType( key, Boolean::class.java, default ) as Boolean;
    }

    /**
     * Ensures the correct data type is being returned by the method.
     *
     * @param[key]
     *          The name of the property
     * @param[type]
     *          The type of property that the key should return
     *
     * @return The value of the property as a `type` Class or a default value
     *         for the class.
     */
    private fun ensureDataType( key: String, type: Class< * >, default: Any ): Any
    {
        val input = get( key );

        // Convert the variable to the correct type and return the default value
        // if the value isn't actually the correct type.
        // Also, if we have to return the default value, store the default value
        // in the config for future reference.
        return when ( type )
        {
            // convert it to a Double or return the default value
            Double::class.java ->
                if ( input !is Double )
                {
                    logIncompatibleTypes( key, input, default )
                    put( key, default as Double )
                    default;
                }
                else input;

            // convert it to an Int or return the default value
            Int::class.java ->
                if ( input !is Int )
                {
                    logIncompatibleTypes( key, input, default )
                    put( key, default as Int )
                    default;
                }
                else input;

            // convert it to a String or return the default value
            String::class.java ->
                if ( input !is String )
                {
                    logIncompatibleTypes( key, input, default )
                    put( key, default as String )
                    default;
                }
                else input;

            Boolean::class.java ->
                if ( input !is Boolean )
                {
                    logIncompatibleTypes( key, input, default )
                    put( key, default as Boolean )
                    default;
                }
                else input;

            else ->
            {
                Log.w( "tilt.ProgramConfig", "Property $key had an unsupported type associated with it: ${type.name}, returning \"\"" )
                "";
            }

        }
    }

    /**
     * Logs an incompatible types error message, this occurs when the value associated with the
     * property was not of the expected type. This is just because I'm lazy and didn't want
     * to have to write the same error message over and over and over again.
     *
     * @param[key]
     *          The property name.
     * @param[actual]
     *          The actual value of the property as stored
     * @param[default]
     *          The default value to use instead.
     */
    private fun logIncompatibleTypes( key: String, actual: Any?, default: Any )
    {
        val message = "$name.$key " +
            if ( actual == null )
            {
                "had no value, setting as $default";
            }
            else
            {
                "was not a ${default.javaClass.simpleName} (actually ${actual.javaClass.simpleName} $actual), setting to $default";
            }

        Log.e( "tilt.ProgramConfig", message );
    }

    //
    // Setters
    //

    /**
     * Sets the property to the given value based on what the prefix on the value
     * string is.
     *
     * @param[key]
     *      The name of the property.
     * @param[value]
     *      The raw value of the property (as read from a file).
     */
    fun putRaw( key: String, value: String )
    {
        properties.put( key, value );
    }

    /**
     * Sets the property to the given value.
     *
     * @param[key]
     *          The name of the property.
     * @param[value]
     *          The value of the property.
     */
    fun put( key: String, value: String )
    {
        // add the s_ prefix for new strings
        if ( value.length < 2 || value[ 1 ] != '_' )
        {
            properties.put( key, "s_$value" );
            return;
        }

        properties.put( key, value );
    }

    /**
     * Sets the property to the given value.
     *
     * @param[key]
     *          The name of the property.
     * @param[value]
     *          The value of the property.
     */
    fun put( key: String, value: Int )
    {
        properties.put( key, "i_$value" );
    }

    /**
     * Sets the property to the given value.
     *
     * @param[key]
     *          The name of the property.
     * @param[value]
     *          The value of the property.
     */
    fun put( key: String, value: Double )
    {
        properties.put( key, "d_$value" );
    }

    /**
     * Sets the property to the given value.
     *
     * @param[key]
     *          The name of the property.
     * @param[value]
     *          The value of the property.
     */
    fun put( key: String, value: Boolean )
    {
        properties.put( key, "b_$value" );
    }

    //
    // Overrides
    //

    override fun compareTo( other: ProgramConfig ): Int
    {
        return name.compareTo( other.name );
    }

    override fun toString(): String
    {
        val sb = StringBuilder();

        // add the entries in
        for ( ( key, value ) in properties )
        {
            sb.append( name ).append( '.' ).append( key ).append( '=' ).append( value ).append( '\n' );
        }

        return sb.toString();
    }

}
