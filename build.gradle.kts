plugins {
    id("java")
    id("io.freefair.aspectj") version "8.10"
    id("io.qameta.allure") version "2.12.0"
}

group = "com.h2.fw"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal() // This tells Gradle to look at the local Maven repository

}

allure {
    version = "2.29.0"
}


val aspectjVersion = "1.9.22.1"
val junitVersion = "5.11.0"
val junitPlatform = "1.11.0"
val cucumberVersion = "7.18.1"
val allureVersion = "2.29.0"
val playwrightVersion = "1.46.0"
val log4jVersion = "2.23.1"
val testngVersion = "7.10.2"
val gherkinVersion = "29.0.0"
val okHttpVersion = "4.12.0"
val springfluxVersion = "6.1.12"
val reflectionsVersion = "0.10.2"
val jacksonVersion = "2.17.2"
val hikariVersion = "5.1.0"
val mvel2Version = "2.5.2.Final"
val mysqlConnectorVersion = "9.0.0"
val postgresqlVersion = "42.7.3"
val oracleVersion = "23.5.0.24.07"
val jsonpathVersion = "2.9.0"
val apacheCommonsLangVersion = "3.16.0"
val hamcrestVersion = "3.0"
val snakeVersion = "2.2"
val browserStackSDKVersion = "1.21.1"
val appiumVersion="9.3.0"
val orgJsonVersion="20240303"
dependencies {

//    implementation(fileTree(mapOf("dir" to "$projectDir/libs", "include" to listOf("*.jar"))))
//    implementation(files(layout.buildDirectory.dir("classes/main").get().asFile))
//    implementation(files(layout.buildDirectory.dir("classes/test").get().asFile))
    //aspectJ
    implementation("org.aspectj:aspectjrt:$aspectjVersion")
    implementation("org.aspectj:aspectjweaver:$aspectjVersion")
    implementation("org.aspectj:aspectjtools:$aspectjVersion")

    //junit
//    implementation(platform("org.junit:junit-bom:$junitVersion"))
    implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    implementation("org.junit.vintage:junit-vintage-engine:$junitVersion")
    //junit platform launcher
    implementation("org.junit.platform:junit-platform-launcher:$junitPlatform")
    implementation("org.junit.platform:junit-platform-engine:$junitPlatform")
    implementation("org.junit.platform:junit-platform-engine:$junitPlatform")
    implementation("io.cucumber:cucumber-core:7.20.1-SNAPSHOT")
    implementation ("org.fusesource.jansi:jansi:2.4.0")

    //testng
    implementation("org.testng:testng:$testngVersion")
    //cucumber
    implementation("io.cucumber:cucumber-java:$cucumberVersion")
//    implementation("io.cucumber:cucumber-core:$cucumberVersion")
    implementation("io.cucumber:cucumber-core:7.20.1-SNAPSHOT")

    implementation("io.cucumber:cucumber-testng:$cucumberVersion")
    implementation("io.cucumber:cucumber-picocontainer:$cucumberVersion")
    implementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
    //gherkin
    implementation("io.cucumber:gherkin:$gherkinVersion")
    //allure
    implementation("io.qameta.allure:allure-cucumber7-jvm:$allureVersion")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")
    implementation("io.qameta.allure:allure-junit5:$allureVersion")
    implementation("io.qameta.allure:allure-testng:$allureVersion")
    //playwright
    implementation("com.microsoft.playwright:playwright:$playwrightVersion")
    //log4j
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")

    //okhttp3
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    //spring flux
    implementation("org.springframework:spring-webflux:$springfluxVersion")
    //reflections
    implementation("org.reflections:reflections:$reflectionsVersion")
    //jackson
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    //hikari
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    //mvel2
    implementation("org.mvel:mvel2:$mvel2Version")
    //mysql connector
    implementation("com.mysql:mysql-connector-j:$mysqlConnectorVersion")
    //postgresql connector
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    //oracle connector
    implementation("com.oracle.database.jdbc:ojdbc11:$oracleVersion")
    //json path
    implementation("com.jayway.jsonpath:json-path:$jsonpathVersion")
    //apache common lang
    implementation("org.apache.commons:commons-lang3:$apacheCommonsLangVersion")
    //hamcrest
    implementation("org.hamcrest:hamcrest:$hamcrestVersion")
    //snakeYML
    implementation("org.yaml:snakeyaml:$snakeVersion")
    //browserstackSDK
    implementation("com.browserstack:browserstack-java-sdk:$browserStackSDKVersion")
    //appium
    implementation("io.appium:java-client:$appiumVersion")
    //orgjson
    implementation("org.json:json:$orgJsonVersion")
    //apache-commonio
    implementation("commons-io:commons-io:2.16.1")
    //everit-json
    implementation("com.github.erosb:everit-json-schema:1.14.4")
    implementation("org.im4java:im4java:1.4.0")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("com.github.lalyos:jfiglet:0.0.9")

    // https://mvnrepository.com/artifact/org.springframework/spring-core
    implementation("org.springframework:spring-core:6.1.12")

    // https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("io.github.techgnious:IVCompressor:2.0.2")
    implementation("io.github.ashwithpoojary98:appium_flutterfinder_java:1.0.9")
    implementation("com.github.saikrishna321:flutter:1.0")
    implementation("com.epam.reportportal:agent-java-cucumber6:5.3.2")
    implementation("com.epam.reportportal:client-java:5.1.26")
    implementation("com.epam.reportportal:commons-model:5.11.1")
    implementation("com.epam.reportportal:logger-java-log4j:5.2.2")
    implementation("com.epam.reportportal:agent-java-testng:5.4.2")
    implementation("com.epam.reportportal:commons:5.12.1")
}

tasks.withType<Test> {
    enabled = false
}
//tasks.test {
//    exclude("**/*")
//}

configurations {
    create("cucumberRuntime") {
        extendsFrom(configurations["implementation"])
    }
}
tasks.register<JavaExec>("runMobileRunner") {
    group = "application"
    description = "Runs the MobileRunner class"
    mainClass.set("h2.fw.core.mobile.AppiumFlutterExample") // Replace with the full path to your MobileRunner class
    classpath = sourceSets["main"].runtimeClasspath
    systemProperty("Platform", "android") // Add more properties as needed

    jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5002")

}

//val cucumberRuntime by configurations.creating {
//    extendsFrom(configurations["testImplementation"])
//    extendsFrom(configurations["implementation"])
//}
//
//
tasks.register<JavaExec>("run") {
    mainClass.set("h2.fw.runner.TestRunnerMain")
    classpath = configurations["cucumberRuntime"] + sourceSets["main"].output + sourceSets["test"].output
    // Dynamic args based on provided properties
    systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()

    val config = System.getProperty("CONFIG", "dev_local")
    val platform = System.getProperty("PLATFORM", "web")
    val feature = System.getProperty("FEATURE", "")
    val folder = System.getProperty("FOLDER", "")
    val parallel = System.getProperty("PARALLEL", "1")
    val debug = System.getProperty("DEBUG", "false")
    val browser = System.getProperty("BROWSER", "chrome")
    val tag = System.getProperty("TAG", "")
    val isParallelScenario = System.getProperty("PARALLEL_SCENARIO", "false")
    val CI = System.getProperty("CI", "false")
    val mobileOs = System.getProperty("MOBILE_OS", "")
    val caps = System.getProperty("CAPS", "")

    systemProperties["CONFIG"] = config
    systemProperties["PLATFORM"] = platform
    systemProperties["FEATURE"] = feature
    systemProperties["FOLDER"] = folder
    systemProperties["MOBILE_OS"] = mobileOs
    systemProperties["PARALLEL"] = parallel
    systemProperties["DEBUG"] = debug
    systemProperties["BROWSER"] = browser
    systemProperties["TAG"] = tag
    systemProperties["PARALLEL_SCENARIO"] = isParallelScenario
    systemProperties["CI"] = CI
    systemProperties["CAPS"] = caps


    args = listOf(
        "CONFIG=$config",
        "PLATFORM=$platform",
        "MOBILE_OS=$mobileOs",
        "FEATURE=$feature",
        "FOLDER=$folder",
        "PARALLEL=$parallel",
        "DEBUG=$debug",
        "BROWSER=$browser",
        "TAG=$tag",
        "PARALLEL_SCENARIO=$isParallelScenario",
        "CI=$CI",
        "CAPS=$caps",
    )

    environment("CONFIG", config)
    environment("PLATFORM", platform)
    environment("MOBILE_OS", mobileOs) // Add to environment
    environment("FEATURE", feature)
    environment("FOLDER", folder)
    environment("PARALLEL", parallel)
    environment("DEBUG", debug)
    environment("BROWSER", browser)
    environment("TAG", tag)
    environment("CI", CI)
    environment("CAPS", caps)
    environment("PLAYWRIGHT_BROWSER_PATH", "/Users/h2/Library/Caches/ms-playwright")


    if (debug.toBoolean()) {
        println("In debug mode")
        jvmArgs = listOf("-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5002")
    }
    doFirst {
        // Check for required parameters based on the platform
        if ((platform == "web" && browser.isEmpty()) ||
            (platform == "mobile" && (mobileOs.isEmpty() || caps.isEmpty())) ||
            (folder.isEmpty() && feature.isEmpty())) {

            val missingParams = mutableListOf<String>()

            if (platform == "web" && browser.isEmpty()) {
                missingParams.add("BROWSER")
            }
            if (platform == "mobile") {
                if (mobileOs.isEmpty()) {
                    missingParams.add("MOBILE_OS")
                }
                if (caps.isEmpty()) {
                    missingParams.add("CAPS")
                }
            }
            if (folder.isEmpty() && feature.isEmpty()) {
                missingParams.add("either FOLDER or FEATURE")
            }

            throw GradleException("Error: ${missingParams.joinToString(", ")} must be provided.")
        }

        println("PLAYWRIGHT_BROWSER_PATH: " + environment["PLAYWRIGHT_BROWSER_PATH"])
        println("Running tests with config: $config, platform: $platform, mobile OS: $mobileOs, feature: $feature, folder: $folder, parallel: $parallel, debug: $debug, tag: $tag, isParallelScenario: $isParallelScenario, CI: $CI, CAPS: $caps")
    }

//    doFirst {
//        if ((platform == "web" && browser.isEmpty()) || (platform == "mobile" && mobileOs.isEmpty() && caps.isEmpty()) || (folder.isEmpty() && feature.isEmpty())) {
//            throw GradleException("Error: BROWSER must be provided for web platform, MOBILE_OS must be provided for mobile platform, and either FOLDER or FEATURE must be provided.")
//        }
//
//        println("PLAYWRIGHT_BROWSER_PATH: " + environment["PLAYWRIGHT_BROWSER_PATH"])
//        println("Running tests with config: $config, platform: $platform, mobile OS: $mobileOs, feature: $feature, folder: $folder, parallel: $parallel, debug: $debug, tag: $tag, isParallelScenario: $isParallelScenario, CI: $CI, CAPS: $caps")
//    }
}

//
//tasks.withType<Test> {
//    useJUnitPlatform()
//
//}