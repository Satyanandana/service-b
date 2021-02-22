package com.learning.serviceb.listeners;

import com.learning.serviceb.reflections.Reflections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This Listener handles the cleanup of static Thread-local fields defined in the com.gce.lms
 * package on requestDestroyed event and before returning the thread to the thread pool.
 *
 * @author satyanandana
 */
@Slf4j
@Component
public class ThreadLocalCleanUpListener implements ServletRequestListener {

    public static final String REMOVE = "remove";
    public static final String FAILED_TO_REMOVE_THE_THREAD_LOCAL_AT_THE_END_OF_REQUEST =
            "Failed to remove the ThreadLocal at the end of request";
    private final List<Field> threadLocals = new ArrayList<>();
    private final Method removeMethod = ThreadLocal.class.getDeclaredMethod(REMOVE);

    public ThreadLocalCleanUpListener() throws NoSuchMethodException {
    }

    /**
     * Scans the com.gce.lms package for the thread-local fields.
     */
    @PostConstruct
    public void init() {
        log.info("Scanning for thread-local fields");
        final org.reflections.Reflections reflections = Reflections.getReflections(null);
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        List<Field> threadLocals = classes
                .stream()
                .flatMap(aclass -> Arrays.stream(aclass.getDeclaredFields()))
                .filter(field -> field.getType().equals(ThreadLocal.class)
                        || field.getType().equals(InheritableThreadLocal.class))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .map(field -> {
                    field.trySetAccessible();
                    return field;
                })
                .collect(Collectors.toList());

        log.info("Thread-locals defined in the app");
        threadLocals.forEach(field -> {
            log.info("Class : {} fieldName : {}",
                    field.getDeclaringClass().getName(),
                    field.getName());
        });
        this.threadLocals.addAll(threadLocals);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

        threadLocals
                .stream()
                .forEach(field -> {
                    try {
                        removeMethod.invoke(field.get(null));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(FAILED_TO_REMOVE_THE_THREAD_LOCAL_AT_THE_END_OF_REQUEST, e);
                    }
                });

    }

}
