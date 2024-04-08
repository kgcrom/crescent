val slf4jVersion = "1.7.25"

dependencies {
    implementation("commons-dbcp:commons-dbcp:1.4")
    implementation("com.thoughtworks.xstream:xstream:1.4.19")
    implementation("commons-pool:commons-pool:1.6")
    implementation("com.fasterxml.jackson.core:jackson-core:2.2.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.2.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.2.3")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")

    testImplementation("commons-dbcp:commons-dbcp:1.4")
    testImplementation("com.thoughtworks.xstream:xstream:1.4.19")
    testImplementation("commons-pool:commons-pool:1.6")
    testImplementation("com.fasterxml.jackson.core:jackson-core:2.2.3")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.2.3")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:2.2.3")
    testImplementation("javax.xml.bind:jaxb-api:2.3.1")
}
