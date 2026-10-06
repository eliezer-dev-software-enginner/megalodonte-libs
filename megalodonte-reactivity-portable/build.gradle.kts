plugins { `java-library`; `maven-publish` }
group = "megalodonte"
version = "1.0.0-beta"
repositories { mavenLocal(); mavenCentral() }
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach { options.release.set(17) }
val portableSources = tasks.register<Sync>("preparePortableSources") {
    from("../megalodonte-reactivity/src/main/java") {
        include("megalodonte/ComputedState.java", "megalodonte/ListenerManager.java",
            "megalodonte/ForEachState.java", "megalodonte/v2/ListState.java")
    }
    into(layout.buildDirectory.dir("generated/sources/portable"))
}
sourceSets.main { java.srcDir(portableSources) }
dependencies { api("megalodonte:megalodonte-core:1.0.0-beta") }
tasks.register<JavaExec>("portableTest") {
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("megalodonte.PortableStateTest")
}
tasks.test { enabled = false }
tasks.check { dependsOn("portableTest") }
publishing { publications { create<MavenPublication>("mavenJava") { from(components["java"]) } } }
