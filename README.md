# Pbwired
The latest version is 1.3.0, strongly recommended. Versions before v1.2.0 are deprecated.

Pbwired is an interesting toy which helps to finish dependency injection even constant injection in a very simple way in SpringBoot,
including following annotations: @Pbwired, @Pbvalue, @FinalInject and @ConstantClass. It can also process @Configurable by modifying AST
at compiling-time rather than using javaagent and LTW(LoadTimeWeaving) at runtime, which seems a little more simple.

#### For @FinalInject and @ConstantClass, you may need a plugin to clean the red wavy underline, go to <https://github.com/wbzdwjsm/pbwired/releases/download/1.2.0/pbwired-plugin.jar> to download the IDEA plugin [pbwired-plugin.jar](https://github.com/wbzdwjsm/pbwired/releases/download/1.2.0/pbwired-plugin.jar) and install it.

## Usage
e.g. Maven:
```xml
<dependency>
	<groupId>com.purpblue</groupId>
	<artifactId>pbwired</artifactId>
	<version>1.3.0</version>
</dependency>
```

### In followings, Part 1 ~ Part 4 is since v1.2.0, Part 5 is new in v1.3.0.

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

### Part 3: [@FinalInject](https://github.com/wbzdwjsm/pbwired)

#### Warning: @FinalInject in v1.2.0 is not compatible with that v1.1.1, the latter is deprecated.   
#### Note: @FinalInject needs Springboot 2.1.x at least.

#### From v1.2.0, @FinalInject uses the same key style as that of Spring's @Value, i.e. the "key" or "value" attributes are wrapped by ${}, #{}, etc.

Just annotate constant fields with @FinalInject, no matter whether they are "static final" or not.

```java
	@FinalInject("${constant.0}")
	public static final String MY_CONSTANT_0;  
```

Also accepts a default value just like Spring's @Value, with a colon as the separator:

```java
	@FinalInject("${constant.0:myConstants}")
	public static final String MY_CONSTANT_0;
```

Except for primitives and their wrapper types and String, @FinalInject can also be used for annotating List, Set, Map, etc. EL expressions are available.
```java
    @FinalInject("${strings.0}")
    private final List<String> STRINGS_0; // Only final modifier is OK.
 
    @FinalInject("${ints.0:1,2,3}")
    private static Set<Integer> INTS_0; // Only static modifier is OK.
    
    @FinalInject("#{${students.map:{'No1':'James', 'No2':'Kate'}}}")
    private static final Map<String, String> STUDENTS_MAP;
    
    @FinalInject("#{T(java.lang.Math).random() * 100}")
    private static final Double RANDOM_DOUBLE;
```

You can use @FinalInject without setting the "key" or "value" attribute, in which @FinalInject will find the fields' qualified name as the key attribute automatically.
Assuming current class's qualified name is "com.purpblue.util.Constants":
```java
    ...
    @FinalInject
    public static final double[] MATH_NUMS;
    ...
```
When compiled, the .class file looks like follows:
```java
    ...
    @FinalInject("${com.purpblue.util.Constants.MATH_NUMS}")
    public static final double[] MATH_NUMS;
    ...
```

### Part 4: [@ConstantClass](https://github.com/wbzdwjsm/pbwired)
If you have lots of constants to be injected, would you like to use @FinalInject one by one? No! 
Like @ConfigurationProperties in Spring, you can use @ConstantClass to annotate a class to indicate that "All suitable fields in this class will be injected".
Fields in this class are injected by their names(Setter methods are NOT needed). Also like @ConfigurationProperties, a "prefix" attribute is acceptable.
Assuming you have set key-value pairs in .properties/.yml config files in advance, if not, you can use @DefaultValue instead.

#### Note: @ConstantClass needs Springboot 2.1.x at least.

.yml config:
```yml
Constant:
  STRING_0: myString
  MAP_0: "{'a': \"A1\", 'b':\"A2\"}"
  LIST_0: X, Y, Z
  DOUBLES_0: 1.23, 3.45, 3.14
```
```java
@ConstantClass(prefix = "Constant")
public class Constants0 {
    public static final String STRING_0;
    
    public static final Map<String, String> MAP_0;
    
    public static final List<String> LIST_0;
    
    public static final double[] DOUBLES_0;
    
    @DefaultValue("1, 2, 3")
    public static final Set<Integer> SETS_0;
}
```
By default, @ConstantClass affects "static final" fields in this class, but you can set "modifiersInclude" attribute to change the rule, and also you can set "fieldsExclude" to exclude some fields.
```java
@ConstantClass(prefix = "Constant1", modifiersInclude = {"public", "static"}, fieldsExclude = {"DOUBLES_1"}) // All "static"(not only "static final") fields will be injected, except DOUBLES_1.
public class Constants1 {
    public static String string1; // public static, OK
    
    public static final Map<String, String> MAP_1; // public static final, OK
    
    public static final List<String> LIST_1; // public static final, OK
    
    public static final double[] DOUBLES_1 = {3.14, 2.71}; // This field will NOT be injected! See "fieldsExclude".
    
    @DefaultValue("1, 2, 3")
    public static Set<Integer> sets1; // public static, OK
    
    private static boolean boolean1 = true; // private static, will NOT be injected. "modifiersInclude" indicates that "public static" is OK.

    static double double1 = 3.14; // Only "static", will NOT be injected. "modifiersInclude" indicates that "public static" is OK.
}
```

#### Note: Once again, you should set config key-value pairs in .properties/.yml files in advance(e.g. application.properties), and of course these key-value pairs are shared by @Value, @FinalInject and @ConstantClass.
#### Note: You can use @FinalInject or @ConstantClass in many classes including POJOs, the only restriction is that DO NOT use them in Boot Class(@SpringBootApplication-annotated class) because of forward use of Spring's BeanFactory which @FinalInject/@ConstantClass need.

### Part 5: Simply process @Configurable, this is new in v1.3.0
When you process @Configurable in Spring, you may use LTW(LoadTimeWeaving), which means you have to set javaagent into VM options, but in some groups or companies this is prohibited.
Now you can process @Configurable as follows instead: put @EnableSimpleProcessorForConfigurableAnnotate on any configuration class, that's all!
Then you can use @Configurable freely, as following:  


a) Configuration class:
```java
@Configuration
@EnableSimpleProcessorForConfigurableAnnotate
public class PbwiredConfiguration { }
```
b) Class with @Configurable:
```java
@Configurable
public class MyPojo {
    //------------User-defined beans----------------------
    @Autowired
    private PbwiredConfiguration config;

    //------------System-level objects--------------------
    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private BeanFactory beanFactory;

    @Resource
    private Environment env;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private HttpServletRequest request;
    //------------Method for Testing--------------------
    public void print() {
        System.out.println(config);
        System.out.println(applicationContext);
        System.out.println(beanFactory);
        System.out.println(env);
        System.out.println(applicationEventPublisher);
        System.out.println(request);
    }
}
```
c) Without javaagent(Also without @EnableLoadTimeWeaving or @EnableSpringConfigured, of course), use "new" option directly:
```java
@Component
public class TestRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        new MyPojo().print();
    }
}
```
Start the application, then you can find that all fields in class MyPojo are injected.
#### This mechanism can work well with LTW. When you use LTW for @Configurable(And set javaagent to VM options), you need to modify Nothing.

#### More information about @Configurable, see Google or other SE.
