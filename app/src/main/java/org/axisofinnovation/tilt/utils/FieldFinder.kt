package org.axisofinnovation.tilt.utils

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

/**
 * Utility class for located Configurable fields by their properties
 * in a class.
 */
class FieldFinder( val clazz: Class< out OpMode> )
{

    //
    // Fields (literally lol)
    //

    /**
     * The public, static fields in this class and all the way up the hierarchy
     */
    private val fields by lazy()
    {
        // Why do we use fields instead of declaredFields?
        // well, simple. For maximum code reuse, we have a chain of inheritance
        // in our OpMode layout, so we don't want to lose the configurability of
        // the super classes by only looking at the lowest class's configurations.
        val allFields = clazz.fields;

        val fields = LinkedList< Field >();

        for ( field in allFields )
        {
            if ( Modifier.isStatic( field.modifiers ) )
            {
                fields.add( field );
            }
        }

        fields;
    }

    //
    // Filtering
    //

    /**
     * Removes all fields which don't have the specified annotation.
     *
     * @param[annotation]
     *          The annotation to filter for.
     * @return The object to chain calls.
     */
    fun with( annotation: Class< out Annotation > ): FieldFinder
    {
        val iter = fields.iterator();
        while ( iter.hasNext() )
        {
            val field = iter.next();

            if ( !field.isAnnotationPresent( annotation ) ) iter.remove();
        }

        return this;
    }

    //
    // Getters
    //

    /**
     * Searches all the remaining fields (after filtration) and builds a list
     * of the Fields that have the same class type as the one passed and returns
     * the list.
     *
     * @param[type]
     *          The type to search for.
     * @return A list of all the fields that have the same class as `type`.
     */
    fun getFieldsOfType( type: Class< * > ): LinkedList< Field >
    {
        val list = LinkedList< Field >();

        for ( field in fields )
        {
            if ( field.type.equals( type ) )
            {
                list.add( field );
            }
        }

        return list;
    }

}