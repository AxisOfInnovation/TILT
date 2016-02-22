package org.axisofinnovation.tilt.hardware

/**
 * The positions of a servo.
 */
enum class ServoStatus(description: String, nextStateIsOff: Boolean )
{

    /** Signifies a servo in it's "off" or disabled state */
    Disabled( "disabled", false ),

    /** Signifies a servo in it's rest state, but it was previously disabled */
    RestDisabled( "rest", false ),

    /** Signifies a servo in it's rest state, but it was previously enabled */
    RestEnabled( "rest", true ),

    /** Signifies a servo in it's "on" or enabled state */
    Enabled( "enabled", true );

    //
    // Fields
    //

    /**
     * The description of the servo as text (enabled, disabled, or at rest). Used in telemetry mostly.
     */
    val Description = description;

    /**
     * `true` if the next position the servo will go to is the off position.
     */
    val NextStateIsOff = nextStateIsOff;

}
