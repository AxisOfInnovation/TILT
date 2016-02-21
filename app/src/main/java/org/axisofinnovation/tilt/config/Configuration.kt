package org.axisofinnovation.tilt.config

import android.util.Log
import org.axisofinnovation.tilt.utils.FileUtils
import java.io.File
import java.util.*

/**
 * The programmatic representation of the ftc.config file.
 *
 * @param[file]
 *          The configuration file to read.
 */
class Configuration( private val file: File )
{

    //
    // Fields
    //

    /**
     * A map of all loaded autonomous program configurations.
     */
    private val autoConfigs = arrayListOf< ProgramConfig >();

    /**
     * The file size when this object was created. (Can be used to tell if the config
     * is out of date or not).
     */
    private var fileSize = file.length();

    //
    // Constructors
    //

    init
    {
        parseConfig();
    }

    //
    // Actions
    //

    /**
     * Parses the configuration file.
     */
    private fun parseConfig()
    {
        val lines = FileUtils.readFileLines( file );

        var parsingMode = ParsingMode.NONE; // keeps track of what we're currently parsing

        // Kotlin doesn't support modifying the index in a for loop, so we have to make our
        // own while loop where we're able to do so

        for( ( i, line ) in lines.withIndex() )
        {
            // find the correct parsing mode
            if ( parsingMode == ParsingMode.NONE )
            {
                if ( !line.matches( "\\[.*\\].*".toRegex() ) ) continue; // if it doesn't start a new mode, skip it
                else
                {
                    val mode = line.substring( 1, line.lastIndexOf( ']' ) ).toUpperCase(); // cut out the brackets
                    parsingMode = when( mode.toLowerCase() )
                    {
                        "autonomous" -> ParsingMode.AUTONOMOUS;
                        else         -> {
                            Log.i( "tilt.Configuration", "Unknown mode: $mode" );
                            ParsingMode.NONE;
                        }
                    }
                    Log.i( "tilt.Configuration", "Changed ParsingMode to $parsingMode" );
                }
            }
            else // ParsingMode.AUTONOMOUS
            {
                val block = trimBlock( lines.subList( i, lines.size ) ); // get the block to parse
                autoConfigs.addAll( parseProgramBlock( block ) ); // add the autonomous configurations
                Collections.sort( autoConfigs );

                parsingMode = ParsingMode.NONE; // we're no longer parsing anything
            }
        }
    }

    /**
     * @return `true` if the file size when this was read is different from the current one.
     */
    fun isOutdated(): Boolean = file.length() != fileSize;

    /**
     * Reloads the configuration file from the disk.
     */
    fun reload()
    {
        autoConfigs.clear();
        parseConfig();
    }

    //
    // Getters
    //

    /**
     * @return A list of all autonomous program configurations.
     */
    fun getAutoConfigList(): ArrayList< ProgramConfig >
    {
        return autoConfigs;
    }

    /**
     * @return The `ProgramConfig` associated with the program name.
     */
    fun getProgram(name: String ): ProgramConfig
    {
        for ( config in autoConfigs )
        {
            if ( config.name.equals( name ) ) return config; // found a match
        }

        Log.i( "tilt.Configuration", "Failed to find Configuration for program $name, creating a new one" );

        // if we couldn't find one, make a new one for the name
        val new = ProgramConfig( name );
        autoConfigs.add( new );
        return new;
    }

    //
    // Parsing Actions
    //

    /**
     * Gets the key and value out of the given line.
     *
     * @param[line]
     *          The line to get the key and value out of.
     * @return A `Pair` where the first is the key and the second the value.
     */
    private fun getKeyAndValue( line: String ): Pair< String, String >
    {
        val split = line.split( "=" );
        val key = split[ 0 ]; // the key is always the first element

        // if there was an equals sign in the value for some reason, piece that back together
        var value = "";
        for ( i in 1..split.size - 1 )
        {
            value += split[ i ] + "=";
        }
        value = value.substring( 0, value.length - 1 ); // remove the last = that doesn't need to be there

        return Pair( key, value );
    }

    /**
     * Parses the block and creates the ProgramConfigs.
     *
     * @param[block]
     *          The block to process.
     * @return A list of `ProgramConfig`s that were read in this block.
     */
    private fun parseProgramBlock( block: List< String > ): MutableCollection<ProgramConfig>
    {
        val configMap = hashMapOf< String, ProgramConfig>();

        for ( line in block )
        {
            val keyAndValue = getKeyAndValue( line );

            // the line is "<program>.<property>"
            // split it apart into "<program>" and "<property>"
            val split = keyAndValue.first.split( "\\.".toRegex() );

            if ( split.size != 2 )
            {
                Log.e( "tilt.Configuration", "Invalid line in ftc.config: $line" );
                continue;
            }

            val program = split[ 0 ];
            val property = split[ 1 ];

            // there's no ProgramConfig for this program yet
            if ( !configMap.containsKey( program ) )
            {
                Log.i( "tilt.Configuration", "Created new ProgramConfig with name $program" );
                configMap.put( program, ProgramConfig( program ) );
            }

            Log.i( "tilt.Configuration", "Set $program.$property to ${keyAndValue.second}" );
            configMap[ program ]?.putRaw( property, keyAndValue.second );
        }

        return configMap.values;
    }

    /**
     * Trims and returns just the current block.
     *
     * @param[input]
     *          The list of lines of text.
     * @return Just the block to process
     */
    private fun trimBlock( input: List< String > ): List< String >
    {
        val lines = arrayListOf< String >();

        for ( i in 0..input.size )
        {
            if ( input[ i ].isEmpty() ) break; // we found an empty line, denoting the end of a block

            lines.add( input[ i ] ); // add this to the block
        }

        return lines;
    }

    //
    // Overrides
    //

    override fun toString(): String
    {
        val sb = StringBuffer();

        sb.append( "[autonomous]\n" );

        // add all the autonomous programs
        for ( config in autoConfigs )
        {
            sb.append( config.toString() );
        }

        sb.append( '\n' ); // empty line between sections

        return sb.toString();
    }

    //
    // Nested Things
    //

    /**
     * The type of block that is being parsed at a time.
     */
    private enum class ParsingMode
    {
        NONE, AUTONOMOUS
    }

}