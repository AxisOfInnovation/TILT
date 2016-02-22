package org.axisofinnovation.tilt.utils

/**
 * @return If `number` is within the range [`lower`, `upper`]
 */
fun inInterval( number: Int, lower: Int, upper: Int ) = lower <= number && number <= upper;