package org.axisofinnovation.tilt.utils

import dalvik.system.DexFile
import java.lang.reflect.Modifier
import java.util.*

/**
 * A Utility for finding classes in the system that have
 * certain annotations.
 */
class ClassFinder()
{

    //
    // Fields
    //

    /** The classes to narrow down. */
    private val classes = LinkedList< Class< * > >();

    //
    // Constructors
    //

    init
    {
        classes.addAll( publicClasses ); // we'll start with all public classes in the library
    }

    //
    // Get
    //

    /**
     * @return The classes that have been filtered and match all requirements.
     */
    fun get(): LinkedList<Class<*>> = classes;

    //
    // Filtering
    //

    /**
     * Removes every class that does not have the annotation passed.
     *
     * @return The object so you can chain calls.
     */
    fun with( annotation: Class< out Annotation > ): ClassFinder
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
    fun subclassOf( clazz: Class< * > ): ClassFinder
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

    //
    // Companion Object for getting an annotation utils
    //

    companion object
    {

        //
        // Fields
        //

        /**
         * A reference to the .dex files so we can pull classes out of it.
         */
        private val dexFile = DexFile( Context.packageCodePath );

        /**
         * Packages and classes to ignore.
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
         * All the classes in the .apk file.
         */
        private val publicClasses = findPublicClasses();

        //
        // Actions
        //

        /**
         * @return All the public classes in this project that aren't blacklisted.
         */
        private fun findPublicClasses(): LinkedList<Class<*>>
        {
            // this is what we'll set the value of allClasses to
            val result = LinkedList<Class<*>>();
            val classNames = LinkedList<String>(Collections.list(dexFile.entries()));

            for ( name in classNames )
            {
                // skip it if we're supposed to
                if ( isBlacklisted( name ) ) continue;

                try
                {
                    val c = Class.forName( name, false, Context.classLoader ); // find the class

                    // the class must be public
                    if ( !isPublic( c ) ) continue;
                    // this is an instantiable, public class
                    else result.add( c );
                }
                catch ( e: Exception )
                {
                    // ignore future anonymous classes
                    if ( name.contains( "$" ) )
                    {
                        blacklist += name.substring( 0, name.lastIndexOf( '$' ) );
                    }
                }
            }

            return result;
        }

        /**
         * @return If the class is on the blacklist or not
         */
        private fun isBlacklisted( name: String ): Boolean
        {
            for ( blacklisted in blacklist )
            {
                if ( name.startsWith( blacklisted ) )
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return `true` if the class is public or not
         */
        private fun isPublic( c: Class< * > ): Boolean
        {
            return Modifier.isPublic( c.modifiers );
        }

    }

}