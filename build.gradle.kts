plugins {
    java
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    jacoco
    id("net.ltgt.errorprone") version "3.0.1"
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
    apply(plugin = "jacoco")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "net.ltgt.errorprone")

    tasks.withType<Test> {
        useJUnitPlatform()
        maxHeapSize = "1G"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configurations {
        val annotationProcessor by getting
        val compileOnly by getting {
            extendsFrom(annotationProcessor)
        }
    }

    dependencies {
        errorprone("com.google.errorprone:error_prone_core:2.24.0")
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
