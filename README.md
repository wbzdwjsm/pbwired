# Pbwired
The latest version is 1.4.0, works under springboot3/java17 without modules(i.e. no module-info.java), or works under springboot2/java8, strongly recommended. Versions before v1.2.0 are all deprecated.

Pbwired is an interesting toy which helps to finish dependency injection even constant injection in a very simple way in SpringBoot,
including following annotations: @Pbwired, @Pbvalue, @FinalInject and @ConstantClass. It can also process @Configurable by modifying AST
at compiling-time rather than using javaagent and LTW(LoadTimeWeaving) at runtime, which seems a little more simple.

For details, see READMEs in following pages:   
https://github.com/wbzdwjsm/pbwired/tree/1.4.0-springboot3-java17   
https://github.com/wbzdwjsm/pbwired/tree/1.4.0-springboot2-java8
