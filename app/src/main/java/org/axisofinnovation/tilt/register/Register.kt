package org.axisofinnovation.tilt.register

@Target( AnnotationTarget.CLASS )
@Retention( AnnotationRetention.RUNTIME )
@MustBeDocumented
annotation class Register( val value: String )