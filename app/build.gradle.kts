plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "es.iesagora.actividad_de_seguimiento"
    compileSdk = 36

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }

    dependencies {

        implementation("androidx.room:room-runtime:2.5.0")
        annotationProcessor("androidx.room:room-compiler:2.5.0")

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        implementation("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

        //Firebase
        implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
        // Firebase Authentication
        implementation("com.google.firebase:firebase-auth")

        implementation("com.google.android.gms:play-services-auth:21.0.0")

        // Firebase BoM (Bill of Materials) para gestionar versiones automáticamente
        implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

        // Dependencia de Firestore
        implementation("com.google.firebase:firebase-firestore")

    }


    defaultConfig {
        applicationId = "es.iesagora.actividad_de_seguimiento"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}