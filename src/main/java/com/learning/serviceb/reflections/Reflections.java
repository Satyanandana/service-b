package com.learning.serviceb.reflections;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility class to work with Reflection Data.
 *
 * @author satyanandana
 */

@Slf4j
public class Reflections {

    /**
     * This return {@link org.reflections.Reflections} object scanning the
     * classes in the given package.
     *
     * @param packageName to scan
     * @return {@link org.reflections.Reflections}
     */
    public static org.reflections.Reflections getReflections(String packageName) {

        if (StringUtils.isBlank(packageName)) {
            packageName = "com.learning";
        }
        return new org.reflections.Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(
                        new ResourcesScanner(),
                        new SubTypesScanner(false),
                        new FieldAnnotationsScanner(),
                        new TypeAnnotationsScanner()));
    }


    /**
     * Can be used to invoke the setter on the provided object with single argument.
     *
     * @param object target object
     * @param setter reflection {@link Method} of setter
     * @param arg    setter method argument
     */
    public static void invokeDefaultSetter(Object object, Method setter, Object arg) {
        try {
            setter.invoke(object, arg);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Invokes the getter method on the provided object and returns the value.
     *
     * @param object     target object
     * @param getter     reflection {@link Method} of getter
     * @param returnType Class of return type
     * @param <T>        Generic type
     * @return value of method
     */
    public static <T> T getMethodValue(Object object, Method getter, Class<T> returnType) {
        Assert.notNull(object, "getter should not be null");
        Assert.notNull(returnType, "returnType should not be null");
        try {
            if (Objects.nonNull(object)) {
                return (T) getter.invoke(object);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * get the ActualArgumentType from the Interface in-case the provided class is implementing it.
     *
     * @param targetClass    target class
     * @param interfaceClass implemented interface with ArgumentTypes
     * @param index          position of the ArgumentType
     * @return
     */
    public static Class<?> getActualTypeArgument(Class<?> targetClass,
                                                 Class<?> interfaceClass,
                                                 int index) {

        Optional<ParameterizedType> optionalType = Arrays
                .stream(targetClass.getGenericInterfaces())
                .filter(type -> type.equals(interfaceClass))
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .findFirst();
        if (optionalType.isPresent()) {
            final Class<?> entityClass =
                    (Class) optionalType.get().getActualTypeArguments()[index];
            return entityClass;
        }
        return null;
    }
}

