import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

group = "org.olafneumann.regexgenerator"
version = "1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.detekt)
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers/")
}


kotlin {
    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlinx.html)
            implementation(kotlin("stdlib-js"))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.kotlin.multiplatform.diff)
            implementation(npm("driver.js", "0.9.8"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
    js {
        useCommonJs()
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    //enabled.set(true)
                }
            }

            webpackTask {
                cssSupport {
                    enabled.set(true)
                }
            }

            runTask {
                cssSupport {
                    enabled.set(true)
                }
            }

            testTask {
                useKarma {
                    useFirefoxHeadless()
                    webpackConfig.cssSupport {
                        enabled.set(true)
                    }
                }
            }
        }
    }
}

configurations {
    all {
        resolutionStrategy {
            disableDependencyVerification()
        }
    }
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

detekt {
    // Define the detekt configuration(s) you want to use.
    config.from(file("$projectDir/.config/detekt.yml"))
    source.from(file("$projectDir/src/"))

    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = true

    // Turns on all the rules. `false` by default.
    allRules = false

    ignoreFailures = true
}

tasks.withType(Detekt::class.java).configureEach {
    jvmTarget = "17"
}

// https://kotlinlang.org/docs/js-project-setup.html#installing-npm-dependencies-with-ignore-scripts-by-default
plugins.withType<YarnPlugin> {
    rootProject.the<YarnRootExtension>().ignoreScripts = false
}

