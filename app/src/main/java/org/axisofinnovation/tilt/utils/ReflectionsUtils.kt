package org.axisofinnovation.tilt.utils

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import dalvik.system.DexFile
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

//
// Fields
//

/**
 * A reference to the .dex files so we can pull all classes from it.
 */
private val dex = DexFile(Context.packageCodePath );

/**
 * Packages to ignore.
 */
private val blacklist: LinkedList< String > by lazy()
{
    val list = LinkedList< String >();
    list += "com.google";
    list += "org.axisofinnovation.tilt";
    list += "com.qualcomm";
    list;
}

/**
 * All the public (and instantiable) classes in the .apk file.
 */
private val publicClasses by lazy()
{
    findInstantiableClasses();
}

//
// Actions
//

/**
 * Sorts through a list of all classes in the .apk and adds it to the returned list
 * if and only if:
 * 1) the class is public. A class must be public if it will be instantiated from
 *    another package.
 * 2) the class is not abstract. Abstract classes cannot be instantiated.
 * 3) the class is not an interface. Interfaces are also non-instantiable.
 *
 * @return A list of all instantiable classes in the apk.
 */
private fun findInstantiableClasses(): LinkedList< Class< * > >
{
    val result = LinkedList< Class< * > >();
    val classNames = LinkedList< String >( Collections.list( dex.entries() ) );

    for ( name in classNames )
    {
        // skip blacklisted files
        if ( isBlacklisted( name ) ) continue;

        try
        {
            val c = Class.forName( name, false, Context.classLoader ); // find the class

            // ignore non-instantiable classes
            if ( !Modifier.isPublic( c.modifiers ) ) continue;
            if ( Modifier.isAbstract( c.modifiers ) ) continue;
            if ( Modifier.isInterface( c.modifiers ) ) continue;


            result.add( c );
        }
        catch ( e: Exception )
        {
            // non instantiable anyways
        }
    }

    return result;
}

/**
 * @return iff the `name` begins with a blacklisted package name.
 */
private fun isBlacklisted( name: String ): Boolean
{
    for ( blacklisted in blacklist )
    {
        if ( name.startsWith( blacklisted ) ) return true;
    }
    return false;
}

//
// Classes
//

/**
 * Filters class files by annotations or class types.
 */
class ClassFilter
{

    //
    // Fields
    //

    /**
     * The filtered classes.
     */
    private val classes by lazy()
    {
        // start off with all the public classes.
        val list = LinkedList< Class< * > >();
        list.addAll( publicClasses );
        list;
    }

    //
    // Getter
    //

    /**
     * @return A copy of the current list of classes.
     */
    fun get(): LinkedList< Class< * > >
    {
        return LinkedList( classes );
    }

    //
    // Filters
    //

    /**
     * Removes every class that does not have the annotation passed.
     *
     * @return The object so you can chain calls.
     */
    fun with( annotation: Class< out Annotation > ): ClassFilter
    {
        val iter = classes.iterator();
        while ( iter.hasNext() )
        {
            val c = iter.next();

            // if it doesn't have the annotation, remove it
            if ( !c.isAnnotationPresent( annotation ) ) iter.remove();
        }

        return this;
    }

    /**
     * Removes every class that is not a subclass of the passed class.
     *
     * @return The object so you can chain calls.
     */
    fun subclassOf( clazz: Class< * > ): ClassFilter
    {
        val iter = classes.iterator();
        while ( iter.hasNext() )
        {
            val c = iter.next();

            // if it isn't a subclass, remove it
            if ( !clazz.isAssignableFrom( c ) ) iter.remove();
        }

        return this;
    }

}

/**
 * Utility class for locating Configurable properties in a class.
 */
class FieldFilter( val clazz: Class< * >, val reference: Any? = null )
{

    //
    // Constructors
    //

    constructor( clazz: Class< out OpMode > ) : this( clazz, null );

    constructor( reference: Any ) : this( reference.javaClass, reference );

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
        val list = LinkedList< Field >();
        list.addAll( clazz.fields );
        list;
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
    fun with( annotation: Class< out Annotation > ): FieldFilter
    {
        val iter = fields.iterator();
        while ( iter.hasNext() )
        {
            val field = iter.next();

            if ( !field.isAnnotationPresent( annotation ) ) iter.remove();
        }

        return this;
    }

    /**
     * Removes all fields which don't have the specified modifier.
     *
     * @param[modifier]
     *          The modifier to filter for (see Modifier class)
     * @return The object to chain calls.
     */
    fun with( modifier: Int ): FieldFilter
    {
        val iter = fields.iterator();
        while ( iter.hasNext() )
        {
            val field = iter.next();

            if ( field.modifiers and modifier == 0 ) iter.remove();
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
    fun getFieldsOfType( type: Class< * > ): LinkedList<Field>
    {
        val list = LinkedList<Field>();

        for ( field in fields )
        {
            if ( type.isAssignableFrom( field.type ) )
            {
                list.add( field );
            }
        }

        return list;
    }

}