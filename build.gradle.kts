import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    id("java")
    id("maven-publish")
}

val gitHash = ByteArrayOutputStream().use {
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = it
    }
    it.toString().trim()
}

group = "no.nav"
version = gitHash

apply(plugin = "org.jetbrains.kotlin.jvm")
java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

val githubUser: String? by project
val githubPassword: String? by project

repositories {
    mavenCentral()
}

configure<PublishingExtension> {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/su-meldinger")
            credentials {
                username = githubUser ?: System.getenv("GITHUB_USERNAME")
                password = githubPassword ?: System.getenv("GITHUB_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("su-meldinger")
                description.set("Inneholder funksjonalitet knyttet til meldinger som utveksles mellom su-applikasjoner")
                url.set("https://github.com/navikt/su-meldinger")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/navikt/su-meldinger.git")
                    developerConnection.set("scm:git:https://github.com/navikt/su-meldinger.git")
                    url.set("https://github.com/navikt/su-meldinger")
                }
            }
            from(components["java"])
        }
    }
}

val junitJupiterVersion = "5.6.0-M1"
val orgJsonVersion = "20180813"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:5.2")
    implementation("org.json:json:$orgJsonVersion")


    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = "12"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.0.1"
}
