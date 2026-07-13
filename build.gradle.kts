// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
//    id("com.google.dagger.hilt.android") version "2.52" apply false
//    id("com.google.gms.google-services") version "4.5.0" apply false
//    id("com.google.firebase.crashlytics") version "3.0.7" apply false
//}