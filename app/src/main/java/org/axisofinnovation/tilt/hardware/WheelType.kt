package org.axisofinnovation.tilt.hardware

/**
 * Information regarding various wheel types.
 *
 * @param[diameter]
 *          The diameter of the wheel, in centimeters.
 */
enum class WheelType( val diameter: Double )
{

    STEALTH_WHEEL( 10.16 );

    //
    // Fields
    //

    /**  The circumference of the wheel, in centimeters. */
    val Circumference = Math.PI * diameter;

}