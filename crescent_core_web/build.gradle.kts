import net.ltgt.gradle.errorprone.errorprone

val luceneVersion = "8.9.0"
val slf4jVersion = "1.7.25"
val logbackVersion = "1.2.3"

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.apache.lucene:lucene-core:${luceneVersion}")
    implementation("org.apache.lucene:lucene-analyzers-common:${luceneVersion}")
    implementation("org.apache.lucene:lucene-analyzers-nori:${luceneVersion}")
    implementation("org.apache.lucene:lucene-queries:${luceneVersion}")
    implementation("org.apache.lucene:lucene-queryparser:${luceneVersion}")
    implementation("org.apache.lucene:lucene-highlighter:${luceneVersion}")
    implementation("org.apache.lucene:lucene-test-framework:${luceneVersion}")
    implementation("org.apache.lucene:lucene-misc:${luceneVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("jaxen:jaxen:1.1.4")
    implementation("dom4j:dom4j:1.6.1")
    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("ch.qos.logback:logback-core:${logbackVersion}")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    implementation("com.thoughtworks.xstream:xstream:1.4.19")
    implementation("net.htmlparser.jericho:jericho-html:3.0")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


tasks.withType<JavaCompile>().configureEach {
    options.errorprone.disableWarningsInGeneratedCode.set(true)
}

tasks {
    compileTestJava {
        options.errorprone.isEnabled.set(false)
    }
}