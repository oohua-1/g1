package h2.fw.core.web.aspects;

import h2.fw.core.web.aspects.AssertExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SoftAssertionAspect {

    @Around("execution(* *(..)) && (@annotation(io.cucumber.java.en.Given) || @annotation(io.cucumber.java.en.Then) || @annotation(io.cucumber.java.en.And) || @annotation(h2.fw.core.web.aspects.StepExt))")
    public Object step(final ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isSoftAssertModeActive = AssertExt.getSoftAssertMode();
        boolean isConditionalBlockActive = ConditionalBlockManager.isConditionalBlockActive();

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            if (isSoftAssertModeActive || isConditionalBlockActive) {
                // Add to both the real-time error list and the global intercepted error list
                AssertExt.addRealTimeError(throwable);  // For real-time status updates
                AssertExt.addInterceptedError(throwable);  // For global error collection

                System.out.println("Soft Assertion Failure: " + throwable.getMessage());

                return null;  // Continue execution without stopping
            } else {
                throw throwable;  // Fail the test immediately if not in soft assertion mode
            }
        }
    }
}
