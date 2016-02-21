package org.axisofinnovation.tilt

/**
 * The types of motors (andymark or tetrix). Useful for differentiating between the
 * two in some small-difference situations.
 */
enum class MotorType( ticks: Int )
{

    Tetrix( 1440 ),
    Andymark( 1220 );

    //
    // Fields
    //

    /**
     * The number of encoder ticks the motor measures in a single rotation.
     */
    val TicksPerRotation = ticks;

    //
    // Methods
    //

    /**
     * Converts a number of `rotations` (in revolutions) to ticks.
     *
     * @param rotations
     *          The number of rotations to calculate.
     */
    fun toTicks( rotations: Double ): Int
    {
        return Math.round( 1.0 * rotations * TicksPerRotation ).toInt();
    }

}