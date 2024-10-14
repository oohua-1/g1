package h2.fw.core.web.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

@Aspect
public class MainStepAspect {
    @Pointcut("execution(* *(..)) && (@annotation(io.cucumber.java.en.Given) || " +
            "@annotation(io.cucumber.java.en.Then) || " +
            "@annotation(io.cucumber.java.en.And))")
    public void mainStepMethods() {
        // Pointcut to match main step methods
    }


    @Around("mainStepMethods()")
    public Object handleMainStep(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // Determine if the screenshot or attachment should be skipped
        boolean skipScreenshot = shouldSkipScreenshot(methodSignature);

        try {
            Object proceed = joinPoint.proceed();

            if (!skipScreenshot) {
                // Mark that the parent step needs an attachment if it passes without errors
                AssertExt.setAttachmentNeededForParent(true);
            }

            return proceed;
        } catch (Throwable throwable) {
            if (!skipScreenshot) {
                // Mark that the parent step needs an attachment if it fails
                AssertExt.setAttachmentNeededForParent(true);
            }
            throw throwable;
        }
    }

    private boolean shouldSkipScreenshot(MethodSignature methodSignature) {
        // Check if the method has the @NeverTakeAllureScreenshotForThisStep annotation
        return methodSignature.getMethod().getAnnotation(NeverTakeAllureScreenshotForThisStep.class) != null;
    }
}
