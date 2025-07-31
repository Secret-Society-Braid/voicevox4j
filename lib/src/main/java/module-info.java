module voicevox4j.lib.api {
  requires com.sun.jna;
  requires java.desktop;
  requires static lombok;
  requires jakarta.annotation;
  requires org.slf4j;
  opens org.braid.secret.society.voicevox4j.internal to com.sun.jna;
  exports org.braid.secret.society.voicevox4j;
  exports org.braid.secret.society.voicevox4j.api;
  exports org.braid.secret.society.voicevox4j.exception;
}