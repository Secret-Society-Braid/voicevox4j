plugins {
  `java-library`
  alias(libs.plugins.moduleplugin)
}

group = "org.braid.society.secret"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.logback)
  implementation(libs.jakarta.annotation)
  implementation(libs.jna)
  implementation(libs.guava)

  compileOnly(libs.lombok)
  testCompileOnly(libs.lombok)

  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation(libs.bundles.tests.bundled)

  annotationProcessor(libs.lombok)
  annotationProcessor(libs.jakarta.annotation)
}

// force UTF-8 encoding both for source files and for the output
tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.test {
  useJUnitPlatform()

//  // UTF-8エンコーディングを強制
//  systemProperty("file.encoding", "UTF-8")
//  systemProperty("jna.encoding", "UTF-8")
//
//  // JVMの文字エンコーディング設定
//  jvmArgs(
//    "-Dfile.encoding=UTF-8",
//    "-Djna.encoding=UTF-8",
//    "-Dsun.jnu.encoding=UTF-8"
//  )
}
