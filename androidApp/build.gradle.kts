plugins {
    id("com.android.application")
    kotlin("android")
}

val accompanistVersion = "0.24.13-rc"
val composeVersion = "1.2.0-rc03"
val lifecycleVersion = "2.6.0-alpha01"
val coroutinesVersion = "1.6.3"
val composeMaterial3Version = "1.0.0-alpha14"
val navigationVersion = "2.5.0"
val koinVersion = "3.2.0"

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.lduboscq.envolee.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 3
        versionName = "0.3"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    flavorDimensions.add("environment")

    val properties = org.jetbrains.kotlin.konan.properties.Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("String", "base_url", "\"${properties.getProperty("production_base_url")}\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        create("development") {
            dimension = "environment"
            buildConfigField("String", "base_url", "\"${properties.getProperty("development_base_url")}\"")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.runtime:runtime-rxjava2:$composeVersion")

    implementation("androidx.activity:activity-compose:1.6.0-alpha05")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    implementation("androidx.navigation:navigation-compose:$navigationVersion")

    implementation("androidx.compose.material3:material3:$composeMaterial3Version")

    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-navigation:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    implementation("com.revenuecat.purchases:purchases:5.0.0-rc3")

    testImplementation("androidx.test:core:1.4.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.8.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3")
   // testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-core:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit4:$koinVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
}
