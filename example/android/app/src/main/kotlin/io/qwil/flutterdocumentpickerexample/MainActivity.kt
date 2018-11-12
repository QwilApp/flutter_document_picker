package io.qwil.flutterdocumentpickerexample

import android.os.Bundle
import io.flutter.plugins.GeneratedPluginRegistrant
import io.qwil.flutterdocumentpicker.FlutterCompatActivity

class MainActivity : FlutterCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)
    }
}