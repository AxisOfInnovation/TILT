package org.axisofinnovation.tilt

import com.qualcomm.robotcore.hardware.Servo

/**
 * A simple class for toggling a 180° servo between two states. If you
 * are looking to toggle a continuous servo, look at ToggleContinuousServo.
 */
open class ToggleServo( servo: Servo, enabled: Double, disabled: Double ) : IToggleServo
{

    //
    // Fields
    //

    override var Status: ServoStatus
        get() = servoStatus;
        set( value )
        {
            throw UnsupportedOperationException( "Do NOT call the setter on ToggleServo.ServoStatus!" );
        }

    /**
     * The current position of the servo.
     */
    protected var servoStatus = ServoStatus.Enabled;

    /**
     * The position of the servo when it's enabled.
     */
    val Enabled = enabled;

    /**
     * The position of the servo when it's disabled.
     */
    val Disabled = disabled;

    /**
     * The servo we're toggling.
     */
    val Servo = servo;

    /**
     * The time (in milliseconds) to wait before a servo can
     * be toggled again.
     */
    var waitTime = 250L;

    /**
     * The last time this servo was toggled.
     */
    private var lastToggle = System.currentTimeMillis();

    //
    // Constructor
    //

    init
    {
        servoStatus = ServoStatus.Enabled;
    }

    //
    // Overrides
    //

    /**
     * Toggles between the states, this is dependent upon the last time
     * it was toggled via this method.
     *
     * @return If the servo was successfully toggled or not
     */
    override fun toggle(): Boolean
    {
        // it hasn't been long enough yet, just stop here
        if ( System.currentTimeMillis() - lastToggle < waitTime ) return false;

        lastToggle = System.currentTimeMillis(); // update the last toggle time

        // toggle the servo
        if ( servoStatus.NextStateIsOff ) toggleOff();
        else toggleOn();

        return true;
    }

    /**
     * Toggles the servo to the off positiozn.
     */
    override fun toggleOff()
    {
        Servo.position = Disabled;
        servoStatus = ServoStatus.Disabled;
    }

    /**
     * Toggles the servo to the on position.
     */
    override fun toggleOn()
    {
        Servo.position = Enabled;
        servoStatus = ServoStatus.Enabled;
    }

}
