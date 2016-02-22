package org.axisofinnovation.tilt.utils

import android.os.Environment
import android.util.Log
import org.axisofinnovation.tilt.config.Configuration
import java.io.*

//
// Fields
//

/**
 * The Directory for internal storage.
 */
val INTERNAL_STORAGE = Environment.getExternalStorageDirectory();

/**
 * The location of the ftc.config file.
 */
private val configFile = File( INTERNAL_STORAGE, "ftc.config" );

/**
 * The configuration object for the ftc.config file.
 */
private val configuration by lazy()
{
    Configuration( configFile );
}

//
// Input
//

/**
 * Reads the given file and returns the content as text.
 *
 * @param[file]
 *          The file to read.
 *
 * @return The text in the file.
 */
fun readFile( file: File ): String
{
    if ( !file.exists() )
    {
        file.createNewFile();
        return "";
    }

    BufferedReader( FileReader( file ) ).use {
        return it.readText();
    };
}

/**
 * Reads the given file and returns the content as an array of its lines.
 *
 * @param[file]
 *          The file to read.
 *
 * @return The text in the file, seperated by lines.
 */
fun readFileLines( file: File ): List< String >
{
    return readFile( file ).split( "\n" );
}

fun writeFile( file: File, text: String )
{
    BufferedWriter( FileWriter( file ) ).use {
        it.write( text );
    };
}

/**
 * Reads the config file and returns the active program configuration.
 */
fun getConfig(): Configuration
{
    if ( configuration.isOutdated() ) configuration.reload();

    return configuration;
}

/**
 * Writes the `Configuration` to the `ftc.config` file.
 */
fun saveConfig()
{
    writeFile( configFile, configuration.toString() );
}

/**
 * Deletes the configuration file.
 */
fun deleteConfig()
{
    configFile.delete();
}
