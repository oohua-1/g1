package h2.fw.core.web.aspects;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SameStep {
}
