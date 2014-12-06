# Play dependency injection

- [Why this project was created?](#whyWasCreated)


##Why this project was created?

When we want to include dependency injection in a Play project, the most important choices we have are:

* [Google Guice](https://github.com/google/guice/wiki/GettingStarted): the main problem is having to include one to one the equivalence between the interface and its implementation.
* [Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-factory-collaborators): in this case is in the hands of Spring the instance creation of each controller, instead of following the philosophy of Play, where there is only one instance of the controller that acts as a multiplexer (through static methods).

So, with this alternative is possible to use dependency injection without having to specify one by one the equivalence between interface and its implementation, or break the Play philosophy of have a single instance of each controller.




 