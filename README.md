# Pbwired
The latest version is 1.1.1

Pbwired is an interesting toy which helps to finish dependency injection even constant injection in a very cool way in SpringBoot,
including 3 annotations: @Pbwired, @Pbvalue and @FinalInject.

For @FinalInject, you may need a plugin to clean the red wavy underline, go to <https://github.com/wbzdwjsm/pbwired/releases/tag/v1.1.1> to download the IDEA plugin [FinalInject-Red-Wavy-Underlined-Cleaner-1.0.0.jar](https://github.com/wbzdwjsm/pbwired/releases/download/v1.1.1/FinalInject-Red-Wavy-Underlined-Cleaner-1.0.0.jar) and install it.

## Usage
Maven:
```xml
<dependency>
	<groupId>com.purpblue</groupId>
	<artifactId>pbwired</artifactId>
	<version>1.1.1</version>
</dependency>
```

### Part 1: [@Pbwired](https://github.com/wbzdwjsm/pbwired)

@Pbwired helps inject a bean into a field which can even be static.
You can choose the way for injection: By constructor or by setter,
what you need is only the annotation - @Pbwired, just like using
@Autowired/@Resource.

Assuming current class is named "TestController":

| your code | bytecode (decompiled) |
| :--- | :--- |
|@Pbwired <br> private MyService myService;|private final MyService myService;<br>@Autowired<br>public TestController(MyService myService) {<br>&nbsp;&nbsp;&nbsp;&nbsp;this.myService = myService;<br>}|
|@Pbwired(wireType = WireType.SETTER)<br>private MyService myService;|private MyService myService;<br>@Autowired<br>public void setMyService(MyService myService) {<br>&nbsp;&nbsp;&nbsp;&nbsp;this.myService = myService;<br>}|
|@Pbwired <br> private static MyService myService;|private static MyService myService;<br>@Autowired <br> public TestController(MyService myService) {<br>&nbsp;&nbsp;&nbsp;&nbsp;TestController.myService = myService;<br>}|
|@Pbwired(name = "myService2")<br>private MyService2 myService2;<br><br>private final MyService3 myService3;<br><br>@Autowired<br>public TestController(MyService3 myService3) {<br>&nbsp;&nbsp;&nbsp;&nbsp;this.myService3 = myService3;<br>}|private final MyService2 myService2;<br><br>private final MyService3 myService3;<br><br>@Autowired<br>public TestController(MyService3 myService3, @Qualifier("myService2") MyService2 myService2) {<br>&nbsp;&nbsp;&nbsp;&nbsp;this.myService3 = myService3;<br>&nbsp;&nbsp;&nbsp;&nbsp;this.myService2 = myService2;<br>}|

#### Note: When [@Pbwired](https://github.com/wbzdwjsm/pbwired) and @Autowired/@Resource exist on the same field, [@Pbwired](https://github.com/wbzdwjsm/pbwired) will be ignored.

### Part 2: [@Pbvalue](https://github.com/wbzdwjsm/pbwired)

This annotation can be used for static and non-static value injection.   You don't need to write setters for static value injection.
 
Assuming your class is named "TestController":

| your code | bytecode (decompiled) |
| :--- | :--- |
|@Pbvalue("${me.name}")<br>private static String name;|private static String name;<br>@Value("${me.name}")<br>public void setName(String name) {<br>&nbsp;&nbsp;&nbsp;&nbsp;TestController.name = name;<br>}|
|@Pbvalue("${me.name}")<br>private String name;|private String name;<br>@Value("${me.name}")<br>public void setName(String name) {<br>&nbsp;&nbsp;&nbsp;&nbsp;this.name = name;<br>}|

#### Note: When [@Pbvalue](https://github.com/wbzdwjsm/pbwired) and @Value exist on the same field, [@Pbvalue](https://github.com/wbzdwjsm/pbwired) will be ignored.

### Part 3: [@FinalInject](https://github.com/wbzdwjsm/pbwired).

When you write a constant class, you can also inject value into final or even static final fields.

For constants, just annotate fields with @FinalInject, no matter whether they are "static final" or not.

```java
	@FinalInject(key = "constant.0")
	public static final String MY_CONSTANT_0;  
```

Also accepts a default value just like Spring's annotation - @Value, with a colon as the separator:

```java
	@FinalInject(key = "constant.0:myConstants")
	public static final String MY_CONSTANT_0;
```

#### Note: Like @Value, you should set key-value pairs in .properties/.yml files in advance.
#### Note: You can use @FinalInject in many classes including POJOs, the only restriction is that DO NOT use it in Boot Class(@SpringBootApplication-annotated class) because of forward use of Spring's env which @FinalInject needs.
#### Note: Once again, as described in the beginning, when you annotate your final or static final fields with @FinalInject, your code may be red wavy underlined. Take Intellij IDEA as an example, you need to install the plugin [FinalInject-Red-Wavy-Underlined-Cleaner-1.0.0.jar](https://github.com/wbzdwjsm/pbwired/releases/download/v1.1.1/FinalInject-Red-Wavy-Underlined-Cleaner-1.0.0.jar) to clean it: <https://github.com/wbzdwjsm/pbwired/releases/download/v1.1.1/FinalInject-Red-Wavy-Underlined-Cleaner-1.0.0.jar>.
