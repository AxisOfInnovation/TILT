# tilt

# Purpose
Do you have variables in autonomous or teleop OpModes that you need to tweak every once in a while? Don't you just hate how long of a process it takes to do that, you have to make the change, build the project, download it to the phone, then the app has to reinstall. If you're in a time crunch (like maybe, in between back-to-back matches and need to make a small tweak to something to make your program work better), this takes _forever_.

This project aims to fix that, as well as provide other, useful frameworks to use. This is intended to be reusable for as long as this field management system goes largely unchanged.

# Set Up
Setting up the library is not actually too difficult. For starters, download the latest `tilt.aar` and `aoiconfig.apk` files from the [latest relase](https://github.com/AxisOfInnovation/ftc_app/releases) page. Download the .apk file onto the _Robot Controller_ (NOT the driver station!) and install it. It will most likely say something about "unknown sources", to circumvent this, simply disable that prevention under the android security settings. If you know how to use the adb command line, this should work:
```batch
adb install aoiconfig.apk
```

The `tilt.aar` file goes in the `ftc_app/FtcRobotController/libs` folder, (there should also be a few other aar files already there). Now that the file is in the correct space, open up the `build.gradle` file for the `FtcRobotController` module (n.b. *not* the `build.gradle` for `ftc_app`!) and add the build statement as shown below.

```gradle
dependencies {
   // the other stuff in the section
   compile(name: 'tilt', ext: 'aar')
}
```

The project now depends on the `tilt.aar` file and you will be able to access all the classes and their methods!

But wait! There's more! We have to modify one line in another file to fully hook into the `tilt` APIs.

Open up the `FtcRobotControllerActivity.java` file in the `com/qualcomm/ftcrobotcontroller` directory and change the following line:
```java
private void requestRobotSetup() {
    // other stuff, not important :)
    eventLoop = new FtcEventLoop(factory, new FtcOpModeRegister(), callback, this);
    // more unimportant stuff
}
```
to this:
```java
private void requestRobotSetup() {
    // other stuff, not important :)
    eventLoop = new FtcEventLoop(factory, new org.axisofinnovation.ftc.tilt.OpModeRegistrar(), callback, this);
    // more unimportant stuff
}
```
This will allow you to use the `@RegisterOpMode` and `@Configurable` annotations, both of which are required to use the configuration framework, so this line is _very_ important!

# Configurables
The most important use of tilt is its configuration framework. Before this can be used, all `OpMode`s must be registered with the `OpModeRegistrar`, which, unlike the `FtcOpModeRegister` class it replaces, does not need to be modified. Simply annotated your class with the `@RegisterOpMode( "Name of OpMode" )` (also, make sure you class is `public`) then rest assured that the class _will_ be registered automatically.
For example:
```java
@RegisterOpMode( "A/Blocking" )
public class BlockingAutonomous extends OpMode
{
    // stuff
}
```
(Sidenote: we use a specific format to name our opmodes, we write "A/" for autonomous and "T/" for teleops and then describe it with only one word; however, this is not necessary and spaces _are_ allowed.)
Please know that there are only two things you can _not_ name your OpMode: "StopRobot" and "" (an empty string). If you do this, then tilt will automatically just grab the name of your class instead (in this case it would pull `BlockingAutonomous`) and yell at you in the logs. Please don't do that.

Now that the OpMode is registered, we can mark variables to be configured (these variables will henceforth be referred to as "properties"). There are a couple of requirements for these variables, such as
1. A property must be `public`
2. A property must be `static`
3. A property must be non-`final`
4. A property must be an `int`, `double`, `boolean`, or `String`
Now, the first three imply that a property must be a static field, if you don't know what that means, it means that your variables cannot be in a method, like this
```java
@Override
public void loop()
{
    @Configurable int waitTime = 5000;
    // stuff
}
```
But rather, they must be like this:
```java
@Configurable
public static long WaitTime = 5000L;

@Override
public void loop()
{
    // stuff
}
```
All properties _should_ start with an uppercase letter and be without spaces or underscores. Each subsequent word in the name should have its first letter capitalized. If it is not an acronymn or does not start a word, the letter is uncapitalized.

Now, once you have valid properties in your OpMode, all you have to do is run the robot controller app one time and let it get all the way to the stage where it says "OpMode: Stop Robot", and then you can open the configuration app and start configuring.

# Hardware
Another useful feature is the ```@Hardware``` annoatation for Hardware fields. Any type of hardware (motors, servos, sensors, etc.) that is connected to the robot that you will need access to
is automatically filled in provided you extend the ```org.axisofinnovation.tilt.OpMode``` class instead of ```com.qualcomm.robotcore.eventloop.opmode.OpMode```.

e.g. instead of:
```java
protected DcMotor left;

@Override
public void init()
{
    left = hardwareMap.dcMotors.get( "left" );
    // other init stuff
}
```
you could do
```java
@Hardware
protected DcMotor left;

@Override
public void init()
{
    // other init stuff
}
```

which isn't a massive trouble, but has caused problems for us when we forget to do that and get a `NullPointerException` when the OpMode runs.

If the name of the hardware is different than its name in the code, simple define the `configName` parameter of the annoatation to the name in the robot config. Like so:
```java
@Hardware( configName = "leftMotor" )
protected DcMotor left;
```

# Modifications

Please do not modify the `aoiconfig.apk` app in any way that would remove our branding from it. We have worked hard on the app and would like to receive some credit for having created and maintained it, and rebranding it as your own removes from that. Please be considerate of our feelings :^)

Going along with that, you are free to fork this repository or simply start your own project from the source; however, please give us credit in some way.

--Austin Donovan;
