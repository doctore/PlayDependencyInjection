# Play dependency injection

- [Why this project was created?](#why-this-project-was-created?)
- [Elements included in this project](#elements-included-in-this-project)
    - [Annotations](#annotations)
    - [Classes](#classes)
- [Basic use case](#basic-use-case)

## Why this project was created?

When we want to include dependency injection in a Play project, the most important choices we have are:

* [Google Guice](https://github.com/google/guice/wiki/GettingStarted): the main problem is having to include one to one the equivalence between the interface and its implementation.
* [Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-factory-collaborators): in this case is in the hands of Spring the instance creation of each controller, instead of following the philosophy of Play, where there is only one instance of the controller that acts as a multiplexer (through static methods).

So, with this alternative is possible to use dependency injection without having to specify one by one the equivalence between interface and its implementation, or break the Play philosophy of have a single instance of each controller.

## Elements included in this project

Below is shown a brief introduction to the components included in this project:

### Annotations

* **Injectable**: specifies those interfaces to be implemented by a class (simulating dependency injection).
* **WithDependencyInjection**: specifies which properties they should inject dependency.

### Classes

* **DependencyInjectionResolver**: main class that manages the dependency injection.
* **DependencyInjectionControllersResolver**: manages the dependency injection inside the {@link Controller} objects.
* **DependencyInjectionPool**: pool that manages all dependency injection resolvers.

## Basic use case

Then we will show a simple example where we will see how to prepare a Play project to inject dependencies (using DAO layer). First we define the interface we want to inject (adding **@Injectable** annotation):

```java
package daos.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestDao {

   /**
    * Checks the dependency injection on {@link ITestDao}
   */
   public String enteringAtTestDao();
}
```

Now its implementation:

```java
package daos.impl;

import daos.spi.ITestDao;

public class TestDao implements ITestDao {

   @Override
   public String enteringAtTestDao() {
      return "Test dao";
   }
}
```

Now we could include the DAO as an attribute of a controller:

```java
package controllers;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import daos.spi.ITestDao;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

   @WithDependencyInjection
   private static ITestDao iTestDao;
   
   public static Result index() {
      Logger.info (iTestDao.enteringAtTestDao());
      return ok (index.render ("Your new application is ready."));
   }
}
```

Note the use of **@WithDependencyInjection** annotation to indicate that to solve the *iTestDao* attribute must use dependency injection. Finally, we have to tell Play how to resolve dependencies:

```java
import org.play.dependencyinjection.DependencyInjectionPool;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resolvers.DependencyInjectionResolver;
import configuration.Constants;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Controller;

public class Global extends GlobalSettings {

   @Override
   public void onStart (Application app) {
   
      try {
         // Initializes the dependency injection
         DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver ("daos.spi", "daos.impl"))
                                           .initializeControllersResolver (controllers, Controller.class);

      } catch (DependencyInjectionException e) {
         Logger.error ("Error when initializes the dependency injection", e);
      }
   }
}
```
Now we have everything you need to use dependency injection in our Play projects.

You can find a more complex example that includes a layer of services, at the following [address](https://github.com/doctore/PlayDependencyInjectionExample)
 