import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.isel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

val ktlint by configurations.creating

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.testng:testng:7.6.1")
	testImplementation(kotlin("test"))

	implementation("org.slf4j:slf4j-api:2.0.4")
	runtimeOnly("org.slf4j:slf4j-simple:2.0.4")

	// for JDBI
	implementation("org.jdbi:jdbi3-core:3.34.0")
	implementation("org.jdbi:jdbi3-kotlin:3.34.0")
	implementation("org.jdbi:jdbi3-postgres:3.34.0")
	implementation("org.postgresql:postgresql:42.5.0")

	ktlint("com.pinterest:ktlint:0.47.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
