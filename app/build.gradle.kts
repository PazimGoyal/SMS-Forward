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

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
        }
    }
}
dependencies {
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
}
