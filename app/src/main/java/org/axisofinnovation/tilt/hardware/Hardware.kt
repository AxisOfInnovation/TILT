package org.axisofinnovation.tilt.hardware

@Target( AnnotationTarget.CLASS )
@Retention( AnnotationRetention.RUNTIME )
@MustBeDocumented
annotation class Hardware( val configName: String = "" )