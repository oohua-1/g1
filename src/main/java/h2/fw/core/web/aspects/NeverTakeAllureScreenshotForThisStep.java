package h2.fw.core.web.aspects;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeverTakeAllureScreenshotForThisStep {
}
