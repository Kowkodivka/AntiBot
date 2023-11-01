plugins {
    id("java")
}

group = "ru.kowkodivka.mindustry.antibot"
version = "1.0.0"

val mindustryVersion = "v146"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository")
}

dependencies {
    compileOnly("com.github.Anuken.Arc:arc-core:$mindustryVersion")
    compileOnly("com.github.Anuken.Mindustry:core:$mindustryVersion")
}

tasks.jar {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}