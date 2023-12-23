plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.21"
    `maven-publish`
}

val aLibVersion = rootProject.property("aLibVersion")

android {
    namespace = "com.fonrouge.library"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("com.fonrouge.fsLib:fsLib:2.1.0")
    implementation("androidx.paging:paging-compose:3.2.1")
    /* scanner service provided by Google Play */
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    api("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    /* camerax */
    val cameraxVersion = "1.4.0-alpha03"
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    /* ML Kit */
    api("com.google.mlkit:barcode-scanning:17.2.0")
    /* permission*/
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    /* replacement for pullRefresh that doesn't exist in Material3 */
    api("eu.bambooapps:compose-material3-pullrefresh:1.0.1")
    /* multi-button floating action button */
    api("com.github.iamageo:MultiFab:1.0.6")
//    api("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-auth:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-serialization:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.fonrouge.android"
            artifactId = "aLib"
            version = "$aLibVersion"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "myrepo"
            url = uri("${project.buildDir}/repo")
        }
    }
}
