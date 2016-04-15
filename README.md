# Android Marshmallow permissions Demo  

For android Marshmallow (API version 23) the user can change permissions of any application at any 
moment and the permissions are not acquired on installation as in early versions. if you are using 
feature that require of type [Dangerous permissions](http://developer.android.com/intl/es/guide/topics/security/permissions.html) your application will crash with a SecurityException.

In this demo is recording and playing audio [as on example](http://developer.android.com/intl/es/guide/topics/media/audio-capture.html) but obtaining required permissions at runtime.

### Version
1.0.0

### Tech

Build for minSdkVersion and targetSdkVersion 23

### Installation

You will need run in a device or emulator with Android 6 (Marshmallow).

### Todos

 - Add [SpeechRecognizer](http://developer.android.com/intl/es/reference/android/speech/SpeechRecognizer.html) so we can test that implicit intents doesn't require any permission from this app.

License
----

MIT


**Free Software, Hell Yeah!**