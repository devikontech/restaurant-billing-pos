/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
import com.google.gms.googleservices.GoogleServicesPlugin.GoogleServicesPluginConfig
import com.niyaj.samples.apps.popos.PoposBuildType
import com.niyaj.samples.apps.popos.cleanedVersion

plugins {
    alias(libs.plugins.popos.android.application)
    alias(libs.plugins.popos.android.application.compose)
    alias(libs.plugins.popos.android.application.flavors)
    alias(libs.plugins.popos.android.application.jacoco)
    alias(libs.plugins.popos.hilt)
    alias(libs.plugins.popos.android.application.firebase)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.sentry)
    alias(libs.plugins.sentry.kotlin)
    alias(libs.plugins.compiler.report)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = libs.versions.namespace.get()

    defaultConfig {
        applicationId = libs.versions.namespace.get()
        versionName = project.cleanedVersion
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"

        manifestPlaceholders["sentryRelease"] = "$applicationId@$versionName"
        manifestPlaceholders["sentryDsn"] = System.getenv("SENTRY_DSN") ?: ""
        manifestPlaceholders.putAll(mapOf("sentryEnvironment" to "production"))
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "debug_keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "popos1234"
            keyAlias = System.getenv("KEYSTORE_ALIAS") ?: "popos-room"
            keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD") ?: "popos1234"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = PoposBuildType.DEBUG.applicationIdSuffix
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = PoposBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("release")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("DebugProbesKt.bin")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.feature.account)
    implementation(projects.feature.addonitem)
    implementation(projects.feature.address)
    implementation(projects.feature.cart)
    implementation(projects.feature.cartSelected)
    implementation(projects.feature.cartorder)
    implementation(projects.feature.category)
    implementation(projects.feature.charges)
    implementation(projects.feature.customer)
    implementation(projects.feature.employee)
    implementation(projects.feature.employeePayment)
    implementation(projects.feature.employeeAbsent)
    implementation(projects.feature.expenses)
    implementation(projects.feature.expenses)
    implementation(projects.feature.home)
    implementation(projects.feature.market)
    implementation(projects.feature.order)
    implementation(projects.feature.printOrder)
    implementation(projects.feature.product)
    implementation(projects.feature.profile)
    implementation(projects.feature.settings)
    implementation(projects.feature.printerInfo)
    implementation(projects.feature.reports)

    implementation(projects.core.analytics)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.worker)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)

    // Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    implementation(libs.navigation.bar)

    // Timber
    implementation(libs.timber)

    testImplementation(projects.core.testing)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.work.testing)

    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
    testDemoImplementation(projects.core.screenshotTesting)

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.databaseTest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)

    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath") {
        modules = true
        tree = true
    }
}

sentry {
    debug = false
    org = "skniyajali"
    projectName = "popos-room"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
    includeSourceContext = true
    telemetry = false
}

firebaseAppDistribution {
    serviceCredentialsFile = "app/firebaseAppDistributionServiceCredentialsFile.json"
    releaseNotesFile = "./app/build/outputs/changelogBeta"
    groups = "continuous-deployment"
}

// Disable to fix memory leak and be compatible with the configuration cache.
configure<GoogleServicesPluginConfig> {
    disableVersionCheck = true
}