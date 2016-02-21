package org.axisofinnovation.tilt

/**
 * A wheel assembly for a motor and wheel.
 */
class WheelAssembly( val Wheel: WheelType, val Motor: MotorType )
{

    /**
     * Converts the number of encoder ticks on the motor to a distance based off
     * of the circumference of the wheel from the WheelType.
     *
     * @param[ticks]
     *          The current motor ticks.
     */
    fun toDistance( ticks: Int ): Double
    {
        return ( ticks / Motor.TicksPerRotation.toDouble() ) * Wheel.Circumference;
    }

    /**
     * Converts the distance (in cm) to the number of ticks for this wheel assembly.
     *
     * @param[distance]
     *          The distance to convert (in cm).
     */
    fun toTicks( distance: Double ): Int
    {
        return Motor.toTicks( distance / Wheel.Circumference );
    }

}
