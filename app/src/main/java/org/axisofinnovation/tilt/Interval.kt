package org.axisofinnovation.tilt

/**
 * A time interval
 */
class Interval(preceding: Interval?, start: Long, duration: Long )
{

    //
    // Fields
    //

    /** The time the interval starts at, in milliseconds. */
    val StartTime: Long = start +
            if ( preceding == null )
            {
                getTime();
            }
            else
            {
                preceding.EndTime;
            }

    /** The time the interval ends at, in milliseconds. */
    val EndTime: Long = StartTime + duration;

    //
    // Constructors
    //

    /**
     * Constructs a time interval based off of the current system time.
     */
    constructor( start: Long, duration: Long ) : this( null, start, duration );

    /**
     * Constructs a time interval based off a duration (starting at the current time)
     */
    constructor( duration: Long ) : this( null, 0, duration );

    //
    // Getters
    //

    /**
     * @return `true` if and only if the current system time is within the interval.
     */
    fun isActive(): Boolean = StartTime <= getTime() && getTime() <= EndTime;

    companion object
    {

        /**
         * `System.currentTimeMillis()` is not very precise, so this will convert
         * from the more accurate `System.nanoTime()`.
         *
         * @return The current time in milliseconds.
         */
        fun getTime(): Long = System.currentTimeMillis();

    }

}
