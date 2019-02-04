# Play dependency injection

- [Why was this project created?](#why-was-this-project-created)
- [Elements included in this project](#elements-included-in-this-project)
    - [Annotations](#annotations)
    - [Classes](#classes)
- [Basic use case](#basic-use-case)
- [Different implementations of the same interface](#different-implementations-of-the-same-interface)
- [Using a list of preinitialized objects](#using-a-list-of-preinitialized-objects)

## Why was this project created?

When we want to include dependency injection in a Play project, the most important choices we have are:

* [Google Guice](https://github.com/google/guice/wiki/GettingStarted): the main problem is having to include one to one the equivalence between the interface and its implementation.
* [Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-factory-collaborators): in this case is in the hands of Spring the creation of controller instances, instead of following the philosophy of Play, where there is only one instance of each controller that acts as a multiplexer (through static methods).

So, with this alternative is possible to use dependency injection without having to specify one by one the equivalence between interface and its implementation, or break the Play philosophy of have a single instance of each controller.

## Elements included in this project

Below is shown a brief introduction to the components included in this project:

### Annotations

* **Injectable**: specifies those interfaces to be implemented by a class (simulating dependency injection).
* **DependencyInjectionQualifier**: identifies the current implementation of a particular interface.
* **WithDependencyInjection**: specifies which properties they should inject dependency.

### Classes

* **DependencyInjectionResolver**: main class that manages the dependency injection.
* **DependencyInjectionControllersResolver**: manages the dependency injection inside a controller class.
* **DependencyInjectionPool**: pool that manages all dependency injection resolvers.

## Basic use case

Then we will show a simple example where we will see how to prepare a Play project to inject dependencies (using DAO layer). First we define the interface we want to inject (adding **@Injectable** annotation):

```java
package daos.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestDao {

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
                                           .initializeControllersResolver ("controllers", Controller.class);

      } catch (DependencyInjectionException e) {
         Logger.error ("Error when initializes the dependency injection", e);
      }
   }
}
```

## Different implementations of the same interface

How can we distinguish between two implementations of the same interface? Let's see how to do it by the following
example:

```java
package daos.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestQualifierDao {

   public String enteringAtTestQualifierDao();
}
```

Now let's define two different implementations of the above interface:

```java
package daos.impl;

import daos.spi.ITestQualifierDao;

public class FirstTestQualifierDao implements ITestQualifierDao {


	@Override
	public String enteringAtTestQualifierDao() {

		return "First test qualifier dao";
	}

}
```

```java
package daos.impl;

import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;

import daos.spi.ITestQualifierDao;

@DependencyInjectionQualifier("secondTestQualifierDao")
public class SecondTestQualifierDao implements ITestQualifierDao {


	@Override
	public String enteringAtTestQualifierDao() {

		return "Second test qualifier dao";
	}

}
```

Note the use of **@DependencyInjectionQualifier** annotation to resolve the conflicts between the implementations.
And finally, as shown below, we may use both implementations:

```java
package controllers;

import org.play.dependencyinjection.annotations.WithDependencyInjection;

import daos.spi.ITestQualifierDao;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	@WithDependencyInjection
	private static ITestQualifierDao firstTestQualifierDao;

	@WithDependencyInjection("secondTestQualifierDao")
	private static ITestQualifierDao secondTestQualifierDao;

    public static Result index() {
    	Logger.info (firstTestQualifierDao.enteringAtTestQualifierDao());
    	Logger.info (secondTestQualifierDao.enteringAtTestQualifierDao());

       return ok (index.render ("Your new application is ready."));
    }

}
```

## Using a list of preinitialized objects

By default, the process used to get the equivalence between interfaces and implementations creates instances of
"implementation objects" using its default constructor (without parameters). However, from now is possible to use
a list of objects that we have built previously because, for example, we need to used a custom construct for those
objects.

```java
package daos.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterface {

   public String enteringAtTestInterface();
}
```

Now let's define a implementations of the above interface with a custom constructor:

```java
package daos.impl;

import daos.spi.ITestInterface;

public class CustomConstructTestInterface implements ITestInterface {

	private String privateStringProperty;
	
	public CustomConstructTestInterface (String privateStringProperty) {

		this.privateStringProperty = privateStringProperty;
	}

	@Override
	public String enteringAtTestInterface() {

		return "Custom construct test interface" + privateStringProperty;
	}

}
```

Finally, we have to tell Play how to resolve dependencies and preinitialized the object:

```java
import java.util.ArrayList;
import java.util.List;
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
         // Creates our custom object instance of CustomConstructTestInterface
         List<Object> preInitializedObjects = new ArrayList<Object>();
         preInitializedObjects.add (new CustomConstructTestInterface ("My string"));
      
         // Initializes the dependency injection
         DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver ("daos.spi", "daos.impl"
                                                                                            ,preInitializedObjects))
                                           .initializeControllersResolver ("controllers", Controller.class);

      } catch (DependencyInjectionException e) {
         Logger.error ("Error when initializes the dependency injection", e);
      }
   }
}
```
 
Now you know everything you need to use dependency injection in your Play projects.

You can find a more complex example that includes a layer of services, at the following [address](https://github.com/doctore/PlayDependencyInjectionExample)
 
