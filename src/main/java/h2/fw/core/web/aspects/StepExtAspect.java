package h2.fw.core.web.aspects;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.step.StepRequestUtils;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import io.cucumber.java.PendingException;
import io.reactivex.Maybe;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.AssumptionViolatedException;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Pointcut;

import java.util.*;

import static java.util.Optional.ofNullable;
//
@Aspect
public class StepExtAspect {

    // Define a pointcut for methods annotated with @StepExt
    @Pointcut("execution(* *(..)) && @annotation(h2.fw.core.web.aspects.StepExt)")
    public void stepExtMethods() {
        // Pointcut to match StepExt annotated methods
    }

    @Around("stepExtMethods()")
    public Object step(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final StepExt step = methodSignature.getMethod().getAnnotation(StepExt.class);
        final String stepName = step.value().isEmpty() ? methodSignature.getName()
                : processNameTemplate(step.value(), getParametersMap(methodSignature, joinPoint.getArgs()));

        Launch launch = Launch.currentLaunch();
        if (launch == null) {
            throw new IllegalStateException("Launch instance is not initialized for the current thread.");
        }
        AssertExt.setSubstep(true);  // Set current state
        if (AssertExt.isCurrentStepSubstep()){
            // Start a nested step
            Optional.ofNullable(launch).ifPresent(l -> {
                StartTestItemRQ startStepRequest = StepRequestUtils.buildStartStepRequest(stepName, null);
                l.getStepReporter().startNestedStep(startStepRequest);
            });

            boolean isSoftAssertModeActive = AssertExt.getSoftAssertMode();
            Object proceed = null;
            try {
                proceed = joinPoint.proceed();

                Optional.ofNullable(launch).ifPresent(l -> {
                    l.getStepReporter().setStepStatus(ItemStatus.PASSED);
                });

            } catch (Throwable throwable) {
                if (isSoftAssertModeActive) {
                    // In soft assertion mode, capture the error but do not fail the test immediately
                    AssertExt.addRealTimeError(throwable);
                    AssertExt.addInterceptedError(throwable);  // Collect the error

                    // Mark the substep as failed without stopping execution
                    Optional.ofNullable(launch).ifPresent(l -> {
                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);  // Mark substep as failed
                    });
                } else {
                    Optional.ofNullable(launch).ifPresent(l -> {
                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);  // Mark substep as failed
                    });
                    throw throwable;  // Rethrow the exception
                }
            } finally {
                if (!AssertExt.getRealTimeErrors().isEmpty()) {
                    Optional.ofNullable(launch).ifPresent(l -> {
                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);  // Set the step status to failed
                    });
                }

                // Handle soft assertion deactivation
                if (AssertExt.isSoftAssertModeDeactivating()) {
                    if (AssertExt.getCountInterceptedErrors() > 0) {
                        StringBuilder aggregatedErrorMessage = new StringBuilder("Soft assertion errors found:\n");
                        for (List<Throwable> errorList : AssertExt.getInterceptedErrors()) {
                            for (Throwable error : errorList) {
                                aggregatedErrorMessage.append(error.getMessage()).append("\n");
                            }
                        }
                        Optional.ofNullable(launch).ifPresent(l -> {
                            l.getStepReporter().finishNestedStep(ItemStatus.PASSED);;  // Mark substep as failed
                        });
                        throw new RuntimeException(aggregatedErrorMessage.toString());  // Fail the parent step
                    }


                }
                Optional.ofNullable(launch).ifPresent(l -> {
                    l.getStepReporter().finishNestedStep();
                });

            }
            return proceed;
        }else {
            return joinPoint.proceed();
        }

    }

    private Map<String, Object> getParametersMap(MethodSignature methodSignature, Object[] args) {
        Map<String, Object> params = new LinkedHashMap<>();
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], args[i]);
        }
        return params;
    }

    private String processNameTemplate(String template, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return template;
    }
}



//@Aspect
//public class StepExtAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(StepExtAspect.class);
//
//    @Pointcut("execution(* *(..)) && @annotation(h2.fw.core.web.aspects.StepExt)")
//    public void stepExtMethods() {}
//
//    @Around("stepExtMethods()")
//    public Object step(final ProceedingJoinPoint joinPoint) throws Throwable {
//        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        final StepExt step = methodSignature.getMethod().getAnnotation(StepExt.class);
//        final String stepName = step.value().isEmpty() ? methodSignature.getName()
//                : processNameTemplate(step.value(), getParametersMap(methodSignature, joinPoint.getArgs()));
//
//        String methodName = methodSignature.getMethod().getName(); // Retrieve the method name
//
//        Launch launch = Launch.currentLaunch();
//        if (launch == null) {
//            throw new IllegalStateException("Launch instance is not initialized for the current thread.");
//        }
//
//        boolean isConditionalBlockActive = ConditionalBlockManager.isConditionalBlockActive();
//        boolean isIfBlockFailed = ConditionalBlockManager.isIfBlockFailed();
//        boolean isElseBlockExecuting = ConditionalBlockManager.isElseBlockExecuting(); // Track else block execution
//
//        // If the conditional block is active and the 'if' block has failed, skip all non-'else' steps
//        if (isConditionalBlockActive) {
//            logger.info("Conditional block is active for step: {}", methodName);
//
//            // Skip any step in the conditional block if the 'if' block has failed and it's not an 'else' step
//            if (isIfBlockFailed && !methodName.startsWith("conditionElse")) {
//                logger.info("Skipping step '{}' because 'if' block has failed.", methodName);
//
//                // Mark the step as skipped in the report but prevent the execution of the step logic
//                Optional.ofNullable(launch).ifPresent(l -> {
//                    l.getStepReporter().setStepStatus(ItemStatus.SKIPPED);
//                });
//
//                // Do not call joinPoint.proceed(); simply return to bypass the step execution
//               throw new PendingException("aaa");
//            }
//
//            // Proceed with 'if' block execution if not failed
//            if (methodName.startsWith("conditionIf")) {
//                logger.info("Checking 'if' block execution for method: {}", methodName);
//                if (!isIfBlockFailed) {
//                    Object result = null;
//                    try {
//                        result = joinPoint.proceed();  // Execute the step logic
//                    } catch (Throwable throwable) {
//                        logger.error("'If' block failed due to exception: {}", throwable.getMessage());
//                        ConditionalBlockManager.setIfBlockFailed(true); // Mark 'if' block as failed
//                        return null;  // Properly return null as a skipped step
//                    } finally {
//                        // Handle failures after the step execution
//                        if (AssertExt.getCountInterceptedErrors() > 0) {
//                            logger.error("Soft assertion failed within 'if' block for step: {}", methodName);
//                            ConditionalBlockManager.setIfBlockFailed(true); // Mark 'if' block as failed
//                        }
//
//                        Optional.ofNullable(launch).ifPresent(l -> {
//                            l.getStepReporter().finishNestedStep(ItemStatus.FAILED);
//                        });
//                    }
//
//                    // Mark 'if' block as passed if there were no failures
//                    ConditionalBlockManager.setIfBlockSatisfied(true);
//                    logger.info("'If' block passed for step: {}", methodName);
//                    return result;
//                } else {
//                    logger.info("Skipping 'if' block step because it has already failed.");
//                    return null;  // Properly return null as a skipped step
//                }
//            }
//
//            // Proceed with 'else' block execution
//            if (methodName.startsWith("conditionElse")) {
//                logger.info("Checking 'else' block execution for method: {}", methodName);
//                if (isIfBlockFailed && !isElseBlockExecuting) {
//                    // Execute 'else' block since 'if' block failed
//                    logger.info("Executing 'else' block since 'if' block failed.");
//                    ConditionalBlockManager.setElseBlockExecuting(true);
//                    try {
//                        return joinPoint.proceed();  // Execute the else block step
//                    } finally {
//                        // Clear 'else' block execution flag
//                        ConditionalBlockManager.setElseBlockExecuting(false);
//                    }
//                } else {
//                    logger.info("Skipping 'else' block because 'if' block passed or 'else' block is already executing.");
//                    return null;  // Properly return null as a skipped step
//                }
//            }
//        }
//        return proceedWithSoftAssertion(joinPoint, stepName, launch);
//    }
//
//    // Helper method to handle soft assertions
//    private Object proceedWithSoftAssertion(ProceedingJoinPoint joinPoint, String stepName, Launch launch) throws Throwable {
//        AssertExt.setSubstep(true);  // Set current state for substep
//        if (AssertExt.isCurrentStepSubstep()) {
//            Optional.ofNullable(launch).ifPresent(l -> {
//                StartTestItemRQ startStepRequest = StepRequestUtils.buildStartStepRequest(stepName, null);
//                l.getStepReporter().startNestedStep(startStepRequest);
//            });
//
//            boolean isSoftAssertModeActive = AssertExt.getSoftAssertMode();
//            Object result = null;
//            try {
//                result = joinPoint.proceed();  // Proceed with the step execution
//
//                Optional.ofNullable(launch).ifPresent(l -> {
//                    l.getStepReporter().setStepStatus(ItemStatus.PASSED);
//                });
//
//            } catch (Throwable throwable) {
//                if (isSoftAssertModeActive) {
//                    logger.error("Soft assertion failure for step '{}': {}", stepName, throwable.getMessage());
//                    AssertExt.addRealTimeError(throwable);
//                    AssertExt.addInterceptedError(throwable);
//
//                    // Mark the conditional block's 'if' as failed
//
//                    Optional.ofNullable(launch).ifPresent(l -> {
//                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);
//                    });
//                    return null; // Continue execution
//                } else {
//                    logger.error("Step '{}' failed with error: {}", stepName, throwable.getMessage());
//                    Optional.ofNullable(launch).ifPresent(l -> {
//                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);
//                    });
//                    throw throwable;
//                }
//            } finally {
//                if (AssertExt.getCountInterceptedErrors() > 0) {
//                    Optional.ofNullable(launch).ifPresent(l -> {
//                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);
//                    });
//                    // Mark the if block as failed if soft assertion errors occurred
//                    ConditionalBlockManager.setIfBlockFailed(true);
//                }
//
//                Optional.ofNullable(launch).ifPresent(l -> {
//                    l.getStepReporter().finishNestedStep();
//                });
//                if (!AssertExt.getRealTimeErrors().isEmpty()) {
//                    Optional.ofNullable(launch).ifPresent(l -> {
//                        l.getStepReporter().setStepStatus(ItemStatus.FAILED);
//                    });
//                }
//
//                if (AssertExt.isSoftAssertModeDeactivating()) {
//                    if (AssertExt.getCountInterceptedErrors() > 0) {
//                        StringBuilder aggregatedErrorMessage = new StringBuilder("Soft assertion errors found:\n");
//                        for (List<Throwable> errorList : AssertExt.getInterceptedErrors()) {
//                            for (Throwable error : errorList) {
//                                aggregatedErrorMessage.append(error.getMessage()).append("\n");
//                            }
//                        }
//                        Optional.ofNullable(launch).ifPresent(l -> {
//                            l.getStepReporter().finishNestedStep(ItemStatus.FAILED);
//                        });
//                        throw new RuntimeException(aggregatedErrorMessage.toString());
//                    }
//                }
//
//                Optional.ofNullable(launch).ifPresent(l -> {
//                    l.getStepReporter().finishNestedStep();
//                });
//            }
//
//            return result;
//        } else {
//            return joinPoint.proceed();
//        }
//    }
//
//    // Helper method to get parameters map
//    private Map<String, Object> getParametersMap(MethodSignature methodSignature, Object[] args) {
//        Map<String, Object> params = new LinkedHashMap<>();
//        String[] parameterNames = methodSignature.getParameterNames();
//        for (int i = 0; i < parameterNames.length; i++) {
//            params.put(parameterNames[i], args[i]);
//        }
//        return params;
//    }
//
//    // Helper method to process name template
//    private String processNameTemplate(String template, Map<String, Object> parameters) {
//        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
//            template = template.replace("{" + entry.getKey() + "}", entry.getValue().toString());
//        }
//        return template;
//    }
//}
