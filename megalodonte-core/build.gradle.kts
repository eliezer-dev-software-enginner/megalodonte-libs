plugins { `java-library`; `maven-publish` }
group = "megalodonte"
version = "1.0.0-beta"
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach { options.release.set(17); options.encoding = "UTF-8" }
tasks.register<JavaExec>("contractTest") {
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("megalodonte.contracts.CoreContractTest")
}
tasks.test { enabled = false }
tasks.check { dependsOn("contractTest") }
val verifyBoundary = tasks.register("verifyPortableBoundary") {
    inputs.files(sourceSets.main.get().allJava)
    dependsOn(tasks.compileJava)
    doLast {
        sourceSets.main.get().allJava.forEach { file ->
            check(!Regex("(?m)^\\s*(import|requires)\\s+(transitive\\s+)?(javafx|android|java\\.awt|java\\.desktop|org\\.slf4j)[.;]").containsMatchIn(file.readText())) {
                "Platform dependency in shared core: $file"
            }
        }
        sourceSets.main.get().output.classesDirs.asFileTree.matching { include("**/*.class") }.forEach { file ->
            val bytes = file.readBytes()
            val major = ((bytes[6].toInt() and 255) shl 8) or (bytes[7].toInt() and 255)
            check(major <= 61) { "Shared core bytecode exceeds Java 17: $file" }
        }
    }
}
tasks.check { dependsOn(verifyBoundary) }
publishing { publications { create<MavenPublication>("mavenJava") { from(components["java"]) } } }
