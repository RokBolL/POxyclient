package ru.shun.arasakafabric.modules;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInform {
    String name();
    String description() default "";
    Category category();
    int keybind() default -1; 
}
