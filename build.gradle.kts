import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

group = "org.olafneumann.regexgenerator"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "2.1.10"
    id("io.gitlab.arturbosch.detekt").version("1.23.6")
    id("org.sonarqube") version "6.0.1.5171"
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers/")
}


kotlin {
    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")

            implementation("org.jetbrains.kotlin:kotlin-stdlib-js")

            implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.156-kotlin-1.5.0")

            implementation("io.ktor:ktor-client-core:3.1.1")

            implementation("io.ktor:ktor-client-js:3.1.1")

            implementation("io.github.petertrr:kotlin-multiplatform-diff:1.0.0")

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

sonar {
    properties {
        property("sonar.projectKey", "regex-generator")
        property("sonar.organization", "noxone")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

// https://kotlinlang.org/docs/js-project-setup.html#installing-npm-dependencies-with-ignore-scripts-by-default
plugins.withType<YarnPlugin> {
    rootProject.the<YarnRootExtension>().ignoreScripts = false
}

