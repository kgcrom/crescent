plugins {
    java
    kotlin("jvm") version "2.1.0" apply false
    kotlin("plugin.spring") version "2.1.0" apply false
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("net.ltgt.errorprone") version "3.0.1" apply false
    jacoco
}

allprojects {
    group = "org.crescent"
    version = "0.0.1"

    repositories {
        maven {
            url = uri("http://repository.springsource.com/maven/bundles/release")
            isAllowInsecureProtocol = true
        }
        maven {
            url = uri("http://repository.springsource.com/maven/bundles/external")
            isAllowInsecureProtocol = true
        }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "net.ltgt.errorprone")

    // Only apply Spring Boot plugin to crescent_core_web
    if (name == "crescent_core_web") {
        apply(plugin = "org.springframework.boot")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        maxHeapSize = "1G"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    configurations {
        val annotationProcessor by getting
        val compileOnly by getting {
            extendsFrom(annotationProcessor)
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Add errorprone only if the plugin is applied
        if (plugins.hasPlugin("net.ltgt.errorprone")) {
            add("errorprone", "com.google.errorprone:error_prone_core:2.24.0")
        }
    }

    jacoco {
        toolVersion = "0.8.8"
    }

    tasks.jacocoTestReport {
        reports {
            html.required.set(true)
            xml.required.set(true)
            csv.required.set(false)
            xml.outputLocation.set(file("$rootDir/jacoco.xml"))
        }
    }

    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                element = "CLASS"

//        TODO ratio가 0.5 넘으면 limit 설정
//        limit {
//          counter = "BRANCH"
//          value = "COVEREDRATIO"
//          minimum = 0.60
//        }
            }
        }
    }

    // TODO jacoco, errorprone 학습하고 필요한것 적용하기
}
