package de.pucktual

import android.app.Application

class PucktualApp : Application() {

    val container by lazy { AppContainer(this) }

    // TODO: Du musst diese Klasse im AndroidManifest.xml registrieren:
    // <application android:name=".MyApplication" ...>
}