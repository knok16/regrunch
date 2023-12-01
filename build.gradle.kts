import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    kotlin("multiplatform") version "1.9.20"
}

group = "com.github.knok16.regrunch"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(11)
        tasks.withType<KotlinJvmTest> {
            useJUnitPlatform()
        }
    }

    linuxX64 {
        binaries {
            executable {
                entryPoint = "com.github.knok16.regrunch.main"
            }
        }
    }

    mingwX64 {
        binaries {
            executable {
                entryPoint = "com.github.knok16.regrunch.main"
                linkerOpts("libs/CRT_noglob.o")
            }
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmTest {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
        nativeMain {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:3.5.2")
            }
        }
    }
}
