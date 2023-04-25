plugins {
    kotlin("multiplatform") version "1.8.20"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("org.jetbrains.dokka") version "1.8.10"
    `maven-publish`
    signing
}

group = "org.example.nexuspublish"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        browser()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}

// Create Javadoc from Dokka's output
val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaHtml)
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
}

nexusPublishing {
    // This compiles, but at least in my IDE shows an error:
    repositories {
        sonatype()
    }
    // This fixes the issue above:
    // repositories(action = {
    //     sonatype()
    // })
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        afterEvaluate {
            artifact(javadocJar)

            pom {
                name.set(project.name)
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

// The following fixes the publication:
// tasks.withType<AbstractPublishToMaven>().configureEach {
//     mustRunAfter(tasks.withType<Sign>())
// }
