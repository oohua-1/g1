//package h2.fw.core.web.aspects;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//
//@Aspect
//public class ConditionalBlockAspect {
//
//    @Pointcut("execution(* *(..)) && @annotation(io.cucumber.java.en.When)")
//    public void whenAnnotatedMethods() {}
//
//    @Around("whenAnnotatedMethods()")
//    public Object aroundWhenMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String methodName = methodSignature.getMethod().getName();
//
//        // Check if we are in a conditional block
//        if (ConditionalBlockManager.isConditionalBlockActive()) {
//            if (methodName.startsWith("conditionIf")) {
//                // Handle the 'if' block logic
//                if (!ConditionalBlockManager.isIfBlockFailed()) {
//                    try {
//                        Object result = joinPoint.proceed();  // Execute the step
//                        ConditionalBlockManager.setIfBlockSatisfied(true);  // Mark 'if' block as satisfied
//                        return result;
//                    } catch (Throwable throwable) {
//                        ConditionalBlockManager.setIfBlockFailed(true);  // Mark 'if' block as failed
//                        return null;  // Skip remaining 'if' block steps
//                    }
//                } else {
//                    // If 'if' block already failed, skip its steps
//                    return null;
//                }
//            }
//
//            if (methodName.startsWith("conditionElse")) {
//                // Handle the 'else' block logic, execute only if 'if' block failed
//                if (ConditionalBlockManager.isIfBlockFailed()) {
//                    return joinPoint.proceed();  // Execute the 'else' block step
//                } else {
//                    // Skip the 'else' block if 'if' block was successful
//                    return null;
//                }
//            }
//        }
//
//        // Proceed normally if not in a conditional block
//        return joinPoint.proceed();
//    }
//}
