#Pbwired

The .jar file will later uploaded to Maven Central Repository. After that, you only need to add it to your pom.xml:
```xml
<dependency>
	<groupId>com.purpblue</groupId>
	<artifactId>pbwired</artifactId>
	<version>0.1-SNAPSHOT</version>
</dependency>
```

##Summary

The Pbwired annotations are used in Springboot/Springframework to simplify your code for injection, including 2 parts: @Pbwired and @Pbvalue, based on APT.

###Part 1: [@Pbwired](https://github.com/wbzdwjsm/pbwired)

In Spring developing, many developers like using constructors for injection because of its strong dependency, and many others like setter injection, but they don't like the annoying constructor/setter codes. Now they may try [@Pbwired](https://github.com/wbzdwjsm/pbwired).
 
####Note: when [@Pbwired](https://github.com/wbzdwjsm/pbwired) and @Autowired/@Resource exist on the same field, [@Pbwired](https://github.com/wbzdwjsm/pbwired) will be ignored.
 
In the following statements, we assume your class is named "MyController",  and it has some fields waiting for injection.
 
  By default, @Pbwired uses constructors for injection, you can use "wireType = WireType.SETTER" to tell APT to use setter injection. The typical java code is like:
```java
      ...
      @Pbwired
      private MyFirstService myFirstService;
 
      @Pbwired
      private MySecondService mySecondService;
      ...
```
 
  The decompiled .class file is like:
```java
    ...
    private MyFirstService myFirstService;
    private MySecondService mySecondService;
 
      @Autowired
      public MyServiceImpl(MyFirstService myFirstService, MySecondService mySecondService) {
          this.myFirstService = myFirstService;
          this.mySecondService = mySecondService;
      }
      ...
```
 
  For injection, @Pbwired can work well with @Autowired/@Resource, contructors and setters.
  Java code as follows:
```java
      ...
      @Pbwired
      private MyFirstService myFirstService;
 
      @Pbwired(wireType = WireType.SETTER)
      private MySecondService mySecondService;
 
      @Autowired
      private MyThirdService myThirdService;
 
      @Resource
      private MyFourthService myFourthService;

      private MyFifthService myFifthService;

      private MySixthService mySixthService;
 
      @Autowired
      public MyController(MyFifthService myFifthService) {
          this.myFifthService = myFifthService;
      }
 
      @Autowired
      public void setMySixthService(MySixthService mySixthService) {
          this.mySixthService = mySixthService;
      }
      ...
```
  
  The decompiled .class file is like:
```java
      ...
      private MyFirstService myFirstService;
      private MySecondService mySecondService;
  
      @Autowired
      private MyThirdService myThirdService;
      @Resource
      private MyFourthService myFourthService;
      
      private MyFifthService myFifthService;
      private MySixthService mySixthService;

      @Autowired
      public MyController(MyFifthService myFifthService, MyFirstService myFirstService) {
         this.myFifthService = myFifthService;
         this.myFirstService = myFirstService;
      }

      @Autowired
      public void setMySixthService(MySixthService mySixthService) {
         this.mySixthService = mySixthService;
      }
    
      @Autowired
      public void setMySecondService(MySecondService mySecondService) {
         this.mySecondService = mySecondService;
      }
      ...
```
Yes! You write your injection code only with [@Pbwired](https://github.com/wbzdwjsm/pbwired), then the APT translates your code into what you want.
 
  You can also use "name" parameter:
```java
    ...
    @Pbwired(name = "myService")
    private MyService myService;
    ...
```
  You will get .class file like:
```java
    ...
    private MyService myService;
 
    @Autowired
    public MyServiceImpl(@Qulifier("myService") MyService myService) {
        this.myService = myService;
    }
    ...
```

###Part 2: [@Pbvalue](https://github.com/wbzdwjsm/pbwired).

This annotation can be used for static and non-static value injection.   You don't need to write setters for static value injection, just write your code with [@Pbvalue](https://github.com/wbzdwjsm/pbwired)!
 
####Note: when [@Pbvalue](https://github.com/wbzdwjsm/pbwired) and @Value exist on the same field, [@Pbvalue](https://github.com/wbzdwjsm/pbwired) will be ignored.
 
Assuming your class is named "NameServiceImpl":
  For static value injection, You can write code as follows:
```java
    @Pbvalue("${me.name}")
    private static String name;
```
 
  When compiled, your .class file looks like:
```java
    private static String name;
 
    @Value("${me.name}")
    public void setName(String name) {
        NameServiceImpl.name = name;
    }
```
 
For non-static value injection, [@Pbvalue](https://github.com/wbzdwjsm/pbwired) is fully equals to @Value.  You can write code as follows:
```java
    @Pbvalue("${me.name}")
    private String name;
```
 
  When compiled, your .class file looks like:
```java
    private String name;
 
    @Value("${me.name}")
    public void setName(String name) {
        this.name = name;
    }
```
