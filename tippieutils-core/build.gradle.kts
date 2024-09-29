plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21")
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.h2database:h2:2.1.214")
    compileOnly("com.mysql:mysql-connector-j:8.0.31")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications.create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = "tippieutils"
        version = project.version.toString()
        from(components["java"])
    }

    repositories {
        maven {
            credentials {
                username = findProperty("tippieRepoUsername") as String?
                password = findProperty("tippieRepoPassword") as String?
            }
            url = if(project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.tippie.me/repository/maven-public-snapshots/")
            } else {
                uri("https://repo.tippie.me/repository/maven-public-releases/")
            }
        }
    }
}