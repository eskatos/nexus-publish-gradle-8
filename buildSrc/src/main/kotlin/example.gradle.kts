plugins {
    id("io.github.gradle-nexus.publish-plugin")
    `maven-publish`
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
