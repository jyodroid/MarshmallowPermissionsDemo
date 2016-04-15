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

You will need run on a device or emulator with Android 6 (Marshmallow).

### Use

- In first view you can use speech recognition with no additional permission by pressing the microphone button and you will see your speech as text.
- If you press "RECORD ME" button you will start a new view.
- If you don't have WRITE_EXTERNAL_STORAGE or RECORD_AUDIO permission you will be prompt to grant those permission.
- after you grant the permissions you can touch the microphone button to start recording and press it again when you're done 
- after record something you touch the play button to reproduce the recorded file and press again to stop it.

License
----

MIT


**Free Software, Hell Yeah!**
