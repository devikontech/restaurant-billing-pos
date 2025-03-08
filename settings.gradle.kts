/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

import org.ajoberstar.reckon.gradle.ReckonExtension

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("org.ajoberstar.reckon.settings") version ("0.18.3")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

extensions.configure<ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("beta", "final")
    setScopeCalc { java.util.Optional.of(org.ajoberstar.reckon.core.Scope.PATCH) }
    setStageCalc(calcStageFromProp())
    setTagWriter { it.toString() }
}

rootProject.name = "restaurant-billing-pos"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:data")
include(":core:analytics")
include(":core:domain")
include(":core:model")
include(":core:database")
include(":core:datastore-proto")
include(":core:datastore")
include(":core:ui")
include(":core:common")
include(":core:designsystem")
include(":core:testing")
include(":core:screenshot-testing")
include(":core:database-test")
include(":core:worker")
include(":core:notifications")

include(":benchmarks")
include(":ui-test-hilt-manifest")

include(":lint")
include(":feature:addonitem")
include(":feature:address")
include(":feature:cart")
include(":feature:cartorder")
include(":feature:category")
include(":feature:charges")
include(":feature:customer")
include(":feature:employee")
include(":feature:employee_payment")
include(":feature:employee_absent")
include(":feature:expenses")
include(":feature:home")
include(":feature:account")
include(":feature:order")
include(":feature:product")
include(":feature:print_order")
include(":feature:profile")
include(":feature:cart_selected")
include(":feature:settings")
include(":feature:printer_info")
include(":feature:reports")
include(":feature:chart")
include(":feature:market")
include(":feature:printer")
