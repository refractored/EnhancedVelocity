plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "ir.syrent"
version = findProperty("version")!!
val slug = "enhancedvelocity"
description = "Customize your Velocity network experience"

repositories {
    maven("https://jitpack.io")
    mavenLocal()
    mavenCentral()

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    // Velocity-API
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "sonatype-oss-snapshots1"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    implementation("org.bstats:bstats-velocity:3.0.0")
//    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("commons-io:commons-io:2.6")
    implementation("com.github.Revxrsal.Lamp:common:3.3.3")
    implementation("com.github.Revxrsal.Lamp:velocity:3.3.3")
}
java {
    withSourcesJar()
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching(listOf("**plugin.yml", "**plugin.json")) {
            expand(
                "version" to rootProject.version as String,
                "slug" to slug,
                "name" to rootProject.name,
                "description" to rootProject.description,
            )
        }
    }

    val relocations =
        mutableMapOf(
            "org.bstats" to "ir.syrent.enhancedvelocity.bstats",
            "org.spongepowered" to "ir.syrent.spongepowered",
        )

    shadowJar {
        minimize()

        archiveFileName = (findProperty("plugin-name") as String) + " v" + findProperty("version") + ".jar"
        archiveClassifier.set(null as String?)

        for ((from, to) in relocations) {
            relocate(from, to)
        }
    }

    jar {
        archiveFileName = (findProperty("plugin-name") as String) + " v" + findProperty("version") + " " + "unshaded" + ".jar"
    }

    withType<Jar> {
        destinationDirectory = file("$rootDir/bin/")
    }

    named<Jar>("sourcesJar") {
        relocations.forEach { (from, to) ->
            val filePattern = Regex("(.*)${from.replace('.', '/')}((?:/|$).*)")
            val textPattern = Regex.fromLiteral(from)
            eachFile {
                filter {
                    it.replaceFirst(textPattern, to)
                }
                path = path.replaceFirst(filePattern, "$1${to.replace('.', '/')}$2")
            }
        }
    }
}

configurations {
    "apiElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_API))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
        outgoing.artifact(tasks.named("shadowJar"))
    }
    "runtimeElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
        outgoing.artifact(tasks.named("shadowJar"))
    }
    "mainSourceElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.objects.named(DocsType.SOURCES))
        }
    }
}

fun setPom(publication: MavenPublication) {
    publication.pom {
        name.set("sayanvanish")
        description.set(project.description)
        url.set("https://github.com/syrent/enhancedvelocity")
        developers {
            developer {
                id.set("syrent")
                name.set("abbas")
                email.set("syrent2356@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:github.com/syrent/enhancedvelocity.git")
            developerConnection.set("scm:git:ssh://github.com/syrent/enhancedvelocity.git")
            url.set("https://github.com/syrent/enhancedvelocity/tree/master")
        }
    }
}
