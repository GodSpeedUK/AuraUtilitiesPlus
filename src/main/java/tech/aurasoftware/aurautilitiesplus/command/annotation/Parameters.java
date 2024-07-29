package tech.aurasoftware.aurautilitiesplus.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    Class<?>[] value();
    boolean[] optional();

}
