package org.axisofinnovation.tilt.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/**
 * The application context
 */
val Context by lazy()
{
    // get a reference for android.app.ActivityThread.currentApplication()
    // via reflections so we can call it and get the current context
    val currentApplication = Class.forName( "android.app.ActivityThread" ).getMethod( "currentApplication" );
    currentApplication.invoke( null ) as Context;
}

//
// Actions
//

fun hideSoftKeyboard( activity: Activity )
{
    val manager = activity.getSystemService( Activity.INPUT_METHOD_SERVICE ) as InputMethodManager;
    manager.hideSoftInputFromWindow( activity.currentFocus.windowToken, 0 );
}

/**
 * Shows a toast message to the user.
 *
 * @param[text]
 *      The text to show to the user
 */
fun showToast( text: String )
{
    Toast.makeText(Context, text, Toast.LENGTH_LONG ).show();
}