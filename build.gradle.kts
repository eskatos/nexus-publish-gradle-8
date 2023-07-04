plugins {
    kotlin("multiplatform") version "1.8.22"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

nexusPublishing {
    this.repositories {
        sonatype()
    }
}

publishing {
    publications.withType<MavenPublication> {
        // Use stub javadoc
        artifact(javadocJar)

        pom {
            name.set(project.name)
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
