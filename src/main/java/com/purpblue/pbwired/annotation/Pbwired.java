/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Many people like using constructors for injection because of its strong dependency, but they don't
 * like the annoying constructor codes. Now they may try {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}.
 *
 *  Note: when @Pbwired and @Autowired/@Resource exist on the same field, @Pbwired will be ignored.
 *
 * In the following statements, we assume your class is named "MyController",
 * and it has some fields waiting for injection.
 *
 * By default, @Pbwired uses constructors for injection. The typical java code is like:
 * <blockquote>
 *     ...
 *     @Pbwired
 *     private MyFirstService myFirstService;
 *
 *     @Pbwired
 *     private MySecondService mySecondService;
 *     ...
 * </blockquote>
 *
 * The decompiled .class file is like:
 * <blockquote>
 *     ...
 *     private MyFirstService myFirstService;
 *     private MySecondService mySecondService;
 *
 *     @Autowired
 *     public MyServiceImpl(MyFirstService myFirstService, MySecondService mySecondService) {
 *         this.myFirstService = myFirstService;
 *         this.mySecondService = mySecondService;
 *     }
 *     ...
 * </blockquote>
 *
 * For injection, @Pbwired can work well with @Autowired/@Resource, contructors and setters.
 * Java code as follows:
 * <blockquote>
 *     ...
 *     @Pbwired
 *     private MyFirstService myFirstService;
 *
 *     @Pbwired
 *     private MySecondService mySecondService;
 *
 *     @Autowired
 *     private MyThirdService myThirdService;
 *
 *     @Resource
 *     private MyFourthService myFourthService;
 *
 *     //constructor injection
 *     private MyFifthService myFifthService;
 *
 *     //setter injection
 *     private MySixthService mySixthService;
 *
 *     @Autowired
 *     public MyController(MyFifthService myFifthService) {
 *         this.myFifthService = myFifthService;
 *     }
 *
 *     @Autowired
 *     public void setMySixthService(MySixthService mySixthService) {
 *         this.mySixthService = mySixthService;
 *     }
 *     ...
 * </blockquote>
 * The decompiled .class file is like:
 * <blockquote>
 *     ...
 *     private MyFirstService myFirstService;
 *     private MySecondService mySecondService;
 *     @Autowired
 *     private MyThirdService myThirdService;
 *     @Resource
 *     private MyFourthService myFourthService;
 *     private MyFifthService myFifthService;
 *     private MySixthService mySixthService;
 *
 *     @Autowired
 *     public MyController(MyFifthService myFifthService, MyFirstService myFirstService, MySecondService mySecondService) {
 *         this.myFifthService = myFifthService;
 *         this.myFirstService = myFirstService;
 *         this.mySecondService = mySecondService;
 *     }
 *
 *     @Autowired
 *     public void setMySixthService(MySixthService mySixthService) {
 *         this.mySixthService = mySixthService;
 *     }
 *     ...
 * </blockquote>
 *
 * Of course, you can also use "name" parameter of @Pbvalue:
 * <blockquote>
 *     ...
 *     @Pbwired(name = "myService")
 *     private MyService myService;
 *     ...
 * </blockquote>
 * You will get .class file like:
 * <blockquote>
 *     ...
 *     private MyService myService;
 *
 *     @Autowired
 *     public MyServiceImpl(@Qulifier("myService") MyService myService) {
 *         this.myService = myService;
 *     }
 *     ...
 * </blockquote>
 *
 * Furthermore, you can use "wireType = WireType.SETTER" for injection. Java code is like:
 * <blockquote>
 *     ...
 *     @Pbwired(wireType = WireType.SETTER)
 *     private MyService myService;
 *     ...
 * </blockquote>
 * The compiled .class file is like:
 * <blockquote>
 *     ...
 *     private MyService myService;
 *
 *     @Autowired
 *     public void setMyService(MyService myService) {
 *       this.myService = myService;
 *     }
 *     ...
 * </blockquote>
 *
 * For value injection, you can use @Pbvalue.
 *
 * @see Pbvalue
 * @author Purpblue
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Pbwired {
    /**
     * To indicate in which way for injection
     * Kindly note: because of java's mechanism, using constructors for injection
     * cannot solve the classic circular reference problem! In this situation,
     * you can use setters for injection, or use @Autowired/@Resource.
     * @return
     */
    WireType wireType() default WireType.CONSTRUCTOR;

    /**
     * Bean name to be used for injection
     * @return
     */
    String name() default "";
}
