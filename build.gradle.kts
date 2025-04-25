// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false

    //Añadir la dependecian de Google Services Gradle
    id("com.google.gms.google-services") version "4.4.2" apply false
}