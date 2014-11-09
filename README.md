MiniDIFramework
===============

A mini DI framework

1. DI is implement with @Injector and @Injectee annotations.
   @Injector indicates this class be be injected to other classes, 
    all @injector marked class should inplement a interface which is dervied from "InjectableObject" interface. 
   @Injectee indicates this field shoud be injected with @Injector marked classes.
    "wireMethod" of @Injectee can indicate injecting with name or type. 
   
2. Main class is "UserInterface.java", and is a command line system.
   Type 'help' to see all instructions.

3. Future Improvements maybe:
   1) Use database to storage input. (use H2?)
   2) Now all inject operations are done on start up, need a generic factory class to inject objects when creating beans(like shapes). 
   3) A mini ORM to save beans.
   
