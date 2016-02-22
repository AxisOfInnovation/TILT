package org.axisofinnovation.tilt.hardware

/**
 * An interface for toggle-able servos
 */
interface IToggleServo
{

    /**
     * The current state of the servo.
     */
    var Status: ServoStatus;

    /**
     * Toggles between the servo's on and off states.
     *
     * @return If the servo was successfully toggled or not.
     */
    fun toggle(): Boolean;

    /**
     * Toggles to the servo's off position.
     */
    fun toggleOff();

    /**
     * Toggles to the servo's on position.
     */
    fun toggleOn();

}