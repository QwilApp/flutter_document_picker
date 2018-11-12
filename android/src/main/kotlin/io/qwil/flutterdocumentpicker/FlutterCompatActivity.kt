package io.qwil.flutterdocumentpicker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.flutter.app.FlutterActivityDelegate
import io.flutter.app.FlutterActivityEvents
import io.flutter.plugin.common.PluginRegistry
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterView

open class FlutterCompatActivity : AppCompatActivity(), FlutterView.Provider, PluginRegistry, FlutterActivityDelegate.ViewFactory {

    private val delegate = FlutterActivityDelegate(this, this)
    private val eventDelegate: FlutterActivityEvents = delegate
    private val viewProvider: FlutterView.Provider = delegate
    private val pluginRegistry: PluginRegistry = delegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.eventDelegate.onCreate(savedInstanceState)
    }

    override fun getFlutterView(): FlutterView {
        return this.viewProvider.flutterView
    }

    override fun createFlutterView(context: Context): FlutterView? {
        return null
    }

    override fun createFlutterNativeView(): FlutterNativeView? {
        return null
    }

    override fun hasPlugin(key: String): Boolean {
        return this.pluginRegistry.hasPlugin(key)
    }

    override fun <T> valuePublishedByPlugin(pluginKey: String): T {
        return this.pluginRegistry.valuePublishedByPlugin(pluginKey)
    }

    override fun registrarFor(pluginKey: String): PluginRegistry.Registrar {
        return this.pluginRegistry.registrarFor(pluginKey)
    }

    override fun onResume() {
        super.onResume()
        this.eventDelegate.onResume()
    }

    override fun onDestroy() {
        this.eventDelegate.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!this.eventDelegate.onBackPressed()) {
            super.onBackPressed()
        }

    }

    override fun onPause() {
        super.onPause()
        this.eventDelegate.onPause()
    }

    override fun onPostResume() {
        super.onPostResume()
        this.eventDelegate.onPostResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        this.eventDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!this.eventDelegate.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onNewIntent(intent: Intent) {
        this.eventDelegate.onNewIntent(intent)
    }

    public override fun onUserLeaveHint() {
        this.eventDelegate.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        this.eventDelegate.onTrimMemory(level)
    }

    override fun onLowMemory() {
        this.eventDelegate.onLowMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        this.eventDelegate.onConfigurationChanged(newConfig)
    }
}
