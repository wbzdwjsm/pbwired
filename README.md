The Pbwired annotations are used in Springboot/Springframework, including 2 parts: @Pbwired and @Pbvalue, based on APT.

Part 1: @Pbwired.

Many developers like using constructors for injection because of its strong dependency, and many others like setter injection, but they don't
  like the annoying constructor/setter codes. Now they may try {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}.
 
Note: when @Pbwired and @Autowired/@Resource exist on the same field, @Pbwired will be ignored.
 
In the following statements, we assume your class is named "MyController",
  and it has some fields waiting for injection.
 
  By default, @Pbwired uses constructors for injection, you can use "wireType = WireType.SETTER" to tell APT to use setter injection. The typical java code is like:
  <blockquote><pre>
      ...
      @Pbwired
      private MyFirstService myFirstService;
 
      @Pbwired
      private MySecondService mySecondService;
      ...
  </pre></blockquote>
 
  The decompiled .class file is like:
  <blockquote>
      ...
      private MyFirstService myFirstService;
      private MySecondService mySecondService;
 
      @Autowired
      public MyServiceImpl(MyFirstService myFirstService, MySecondService mySecondService) {
          this.myFirstService = myFirstService;
          this.mySecondService = mySecondService;
      }
      ...
  </blockquote>
  Yes, the APT takes effect.
 
  For injection, @Pbwired can work well with @Autowired/@Resource, contructors and setters.
  Java code as follows:
  <blockquote>
      ...
      @Pbwired
      private MyFirstService myFirstService;
 
      @Pbwired(wireType = WireType.SETTER)
      private MySecondService mySecondService;
 
      @Autowired
      private MyThirdService myThirdService;
 
      @Resource
      private MyFourthService myFourthService;
 
      //original constructor injection
      private MyFifthService myFifthService;
 
      //original setter injection
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
  </blockquote>
  
  The decompiled .class file is like:
  <blockquote>
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
  </blockquote>
  Yes! You write your injection code only with @Pbwired, then the APT translates your code into what you want.
 
  Of course, you can also use "name" parameter of @Pbvalue:
  <blockquote>
      ...
      @Pbwired(name = "myService")
      private MyService myService;
      ...
  </blockquote>
  You will get .class file like:
  <blockquote>
      ...
      private MyService myService;
 
      @Autowired
      public MyServiceImpl(@Qulifier("myService") MyService myService) {
          this.myService = myService;
      }
      ...
  </blockquote>

Part 2: @Pbvalue.

This annotation can be used for static and non-static value injection. Yes, for static value injection, it still works!
   You don't need to write setters for static value injection, just write your code with @Pbvalue!
 
Note: when @Pbvalue and @Value exist on the same field, @Pbvalue will be ignored.
 
Assuming your class is named "NameServiceImpl":
  For static value injection:
  You can write code as follows:
  <blockquote>
      @Pbvalue("${me.name}")
      private static String name;
  </blockquote>
 
  When compiled, your .class file looks like:
  <blockquote>
      private static String name;
 
      @Value("${me.name}")
      public void setName(String name) {
          NameServiceImpl.name = name;
      }
  </blockquote>
 
  <p>For non-static fields' injection, @Pbvalue is fully equals to @Value.
  You can write code as follows:
  <blockquote>
      @Pbvalue("${me.name}")
      private String name;
  </blockquote>
 
  When compiled, your .class file looks like:
  <blockquote>
      private String name;
 
      @Value("${me.name}")
      public void setName(String name) {
          this.name = name;
      }
  </blockquote>
