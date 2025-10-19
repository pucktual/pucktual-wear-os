package de.florianostertag.coffeehelper

import android.app.Application

class CoffeeHelperApp : Application() {

    val container by lazy { AppContainer(this) }

    // TODO: Du musst diese Klasse im AndroidManifest.xml registrieren:
    // <application android:name=".MyApplication" ...>
}