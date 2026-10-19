pluginManagement {
  plugins {
    id("org.jetbrains.kotlin.jvm").version("2.1.10")
  }
}
plugins {
  id("org.gradle.toolchains.foojay-resolver").version("1.0.0")
  id("com.gradle.develocity").version("4.0")
}
develocity {
  buildScan {
    termsOfUseUrl.set("https://gradle.com/terms-of-service")
    termsOfUseAgree.set("yes")
  }
}
rootProject.name = "voicevox4j"
include("lib")
