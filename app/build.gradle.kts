plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "br.com.amazongas.aulawhatsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.amazongas.aulawhatsapp"
        minSdk = 33
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //Dependencias Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    //Dependencias Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")
    //Dependencias Firebase Auth
    implementation("com.google.firebase:firebase-auth")
    //Dependencias Firebase Cloud Firestore Banco de Dados
    implementation("com.google.firebase:firebase-firestore")
    //Dependencias Firebase Cloud Storage Armazenamento de Images
    implementation("com.google.firebase:firebase-storage")
    //Picasso
    implementation("com.squareup.picasso:picasso:2.8")
}