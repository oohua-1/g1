package h2.fw.stepdefinitions;

import h2.fw.stepdefinitions.FieldSteps.MobileFieldSteps;
import h2.fw.stepdefinitions.FieldSteps.WebFieldSteps;
import h2.fw.stepdefinitions.PageSteps.MobilePageSteps;
import h2.fw.stepdefinitions.PageSteps.WebPageSteps;

import java.lang.reflect.Method;
import java.util.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
//
//public class StepDispatcher {
//    private final Map<String, Object> webStepsRegistry = new HashMap<>();
//    private final Map<String, Object> mobileStepsRegistry = new HashMap<>();
//
//    public StepDispatcher(BaseStep baseStep, MobileBaseStep mobileBaseStep) {
//        // Register all web step classes
//        webStepsRegistry.put("PageSteps", new WebPageSteps(baseStep));
//        mobileStepsRegistry.put("PageSteps", new MobilePageSteps(mobileBaseStep));
//
//        webStepsRegistry.put("FieldSteps", new WebFieldSteps(baseStep));
//        mobileStepsRegistry.put("FieldSteps", new MobileFieldSteps(mobileBaseStep));
//    }
//
//    public void dispatchStep(String stepClass, String methodName, Object... args) {
//        try {
//            Object targetObject = resolveTargetObject(stepClass);
//
//            // Use reflection to invoke the correct method
//            Method method = targetObject.getClass().getMethod(methodName, getParameterTypes(args));
//            method.invoke(targetObject, args);
//        } catch (Exception e) {
//            throw new RuntimeException("Error dispatching step", e);
//        }
//    }
//
//    private Object resolveTargetObject(String stepClass) {
//        String env = System.getProperty("PLATFORM");
//
//        if ("mobile".equalsIgnoreCase(env)) {
//            return mobileStepsRegistry.get(stepClass);
//        } else {
//            return webStepsRegistry.get(stepClass);
//        }
//    }
//
//    private Class<?>[] getParameterTypes(Object[] args) {
//        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
//    }
//}
//
//import org.springframework.util.ReflectionUtils;
//
//import java.lang.reflect.Method;
//
//public class StepDispatcher {
//    private final Map<String, Object> webStepsRegistry = new HashMap<>();
//    private final Map<String, Object> mobileStepsRegistry = new HashMap<>();
//
//    public StepDispatcher(BaseStep baseStep, MobileBaseStep mobileBaseStep) {
//        // Register all web step classes
//        webStepsRegistry.put("PageSteps", new WebPageSteps(baseStep));
//        mobileStepsRegistry.put("PageSteps", new MobilePageSteps(mobileBaseStep));
//
//        webStepsRegistry.put("FieldSteps", new WebFieldSteps(baseStep));
//        mobileStepsRegistry.put("FieldSteps", new MobileFieldSteps(mobileBaseStep));
//    }
//
//    public void dispatchStep(String stepClass, String methodName, Object... args) {
//        try {
//            Object targetObject = resolveTargetObject(stepClass);
//
//            // Use Spring's ReflectionUtils to find and invoke the method
//            Method method = ReflectionUtils.findMethod(targetObject.getClass(), methodName, getParameterTypes(args));
//            if (method != null) {
//                ReflectionUtils.invokeMethod(method, targetObject, args);
//            } else {
//                throw new NoSuchMethodException("Method " + methodName + " not found in class " + targetObject.getClass());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error dispatching step", e);
//        }
//    }
//
//    private Object resolveTargetObject(String stepClass) {
//        String env = System.getProperty("PLATFORM");
//
//        if ("mobile".equalsIgnoreCase(env)) {
//            return mobileStepsRegistry.get(stepClass);
//        } else {
//            return webStepsRegistry.get(stepClass);
//        }
//    }
//
//    private Class<?>[] getParameterTypes(Object[] args) {
//        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
//    }
//}

import org.apache.commons.beanutils.MethodUtils;

import java.lang.reflect.InvocationTargetException;

public class StepDispatcher {
    private final Map<String, Object> webStepsRegistry = new HashMap<>();
    private final Map<String, Object> mobileStepsRegistry = new HashMap<>();

    public StepDispatcher(BaseStep baseStep, MobileBaseStep mobileBaseStep) {
        // Register all web step classes
        webStepsRegistry.put("PageSteps", new WebPageSteps(baseStep));
        mobileStepsRegistry.put("PageSteps", new MobilePageSteps(mobileBaseStep));

        webStepsRegistry.put("FieldSteps", new WebFieldSteps(baseStep));
        mobileStepsRegistry.put("FieldSteps", new MobileFieldSteps(mobileBaseStep));
    }

    public void dispatchStep(String stepClass, String methodName, Object... args) {
        try {
            Object targetObject = resolveTargetObject(stepClass);

            // Use Apache Commons BeanUtils to dynamically invoke the method
            MethodUtils.invokeMethod(targetObject, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error dispatching step", e);
        }
    }

    private Object resolveTargetObject(String stepClass) {
        String env = System.getProperty("PLATFORM");

        if ("mobile".equalsIgnoreCase(env)) {
            return mobileStepsRegistry.get(stepClass);
        } else {
            return webStepsRegistry.get(stepClass);
        }
    }
}
