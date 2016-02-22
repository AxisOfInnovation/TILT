package org.axisofinnovation.tilt.hardware

import com.qualcomm.robotcore.hardware.Servo

/**
 * A simple class for toggling a servo between two positions then returning
 * to the rest position
 */
class RestingToggleServo( servo: Servo, enabled: Double, disabled: Double, rest: Double ) : ToggleServo( servo, enabled, disabled )
{

    //
    // Fields
    //

    /**
     * The resting position of the servo.
     */
    val Rest = rest;

    //
    // Constructors
    //

    init
    {
        toggleRest();
    }

    //
    // Overrides
    //

    override fun toggle(): Boolean
    {
        val result = super.toggle();

        // we don't need to set up another thread to deactivate it
        if ( !result ) return false;

        Thread(
                {
                    Thread.sleep( waitTime );
                    toggleRest();
                }
        ).start();

        return true;
    }

    /**
     * Toggles the servo away from it's previous position and to the rest position.
     */
    fun toggleRest()
    {
        Servo.position = Rest;

        // it's coming from the enabled position
        if ( servoStatus.NextStateIsOff )
        {
            servoStatus = ServoStatus.RestEnabled;
        }
        else
        {
            servoStatus = ServoStatus.RestDisabled;
        }
    }

}