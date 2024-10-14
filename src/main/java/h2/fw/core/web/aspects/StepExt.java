package h2.fw.core.web.aspects;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StepExt {
    String value() default "";
    boolean hideParams() default false;
    boolean sceenshotForSubsteps() default false;
    ForcedStepStatus forcedStepStatus() default ForcedStepStatus.NO_FORCE_STATUS;

    enum ForcedStepStatus{
        FAILED,
        BROKEN,
        PASSED,
        SKIPPED,
        NO_FORCE_STATUS
    }
}
