import com.android.build.api.dsl.Packaging
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.cs407.snapnourish"
    compileSdk = 35

    val secretsPropertiesFile = rootProject.file("app/secrets.properties")
    if (secretsPropertiesFile.exists()) {
        val secretsProperties = Properties().apply {
            load(FileInputStream(secretsPropertiesFile))
        }
        buildTypes.forEach {
            it.buildConfigField(
                "String",
                "GOOGLE_APPLICATION_CREDENTIALS",
                "\"${secretsProperties["GOOGLE_APPLICATION_CREDENTIALS"]}\""
            )
        }
    }

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
        }
    }

    defaultConfig {
        applicationId = "com.cs407.snapnourish"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.findProperty("GEMINI_API_KEY")}\""
        )
    }

    buildFeatures {
        buildConfig =true
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

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Dependencies for Gemini Chatbot
    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")
    // for coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    // from chatgpt after youtube tutorial
    implementation("com.google.cloud:google-cloud-vertexai:0.4.0") // Check for latest version
    implementation("com.google.auth:google-auth-library-oauth2-http:1.7.0")
    implementation("com.google.api-client:google-api-client:1.32.1")

    // for HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.8.8")

    // for parsing response from Vertex AI
    implementation("org.json:json:20210307")




}