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
        versionCode = 1
        versionName = "1.0"
    }
}
