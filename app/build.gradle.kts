plugins {
    id("com.android.application")
}

android {
    namespace = "com.pazim.smsemailforwarder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pazim.smsemailforwarder"
        minSdk = 23
        targetSdk = 36
        versionCode = 5
        versionName = "5.0"
    }
}

dependencies {
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
}
