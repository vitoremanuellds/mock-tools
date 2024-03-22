package com.mocktools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MockTools {

    private static final Map<Class<?>, Object> defaultValues = new HashMap<>() {{
        this.put(int.class, 0);
        this.put(byte.class, 0);
        this.put(char.class, '\u0000');
        this.put(boolean.class, false);
        this.put(double.class, 0.0d);
        this.put(float.class, 0.0f);
        this.put(long.class, 0L);
        this.put(short.class, 0);
        this.put(Integer.class, 0);
        this.put(Byte.class, 0);
        this.put(Character.class, '\u0000');
        this.put(Boolean.class, false);
        this.put(Double.class, 0.0d);
        this.put(Float.class, 0.0f);
        this.put(Long.class, 0L);
        this.put(Short.class, 0);
    }};

    public static <T> T populateUntilLevel(Class<T> instanceClass, int untilLevel)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (untilLevel < 1) {
            if (instanceClass.isPrimitive() || defaultValues.containsKey(instanceClass)) {
                return (T) defaultValues.get(instanceClass);
            }
            if (instanceClass.equals(String.class)) {
                return (T) "String";
            }
            return null;
        }

        Constructor<?> constructor = Arrays.stream(instanceClass.getConstructors())
                .min(Comparator.comparingInt(Constructor::getParameterCount))
                .orElse(null);

        if (constructor == null) {
            throw new RuntimeException("Unable to create an instance of this class");
        }

        Object[] args = generateArgs(constructor, untilLevel);

        T instance = (T) constructor.newInstance(args);

        populateFields(instanceClass, instance, untilLevel);

        return instance;
    }

    private static <T> Object[] generateArgs(Constructor<T> constructor, int untilLevel) {
        return Arrays.stream(constructor.getParameters()).map(p -> {
            if (p.getType().isPrimitive() || defaultValues.containsKey(p.getType())) {
                return defaultValues.get(p.getType());
            }
            if (p.getType().equals(String.class)) {
                return p.getName();
            }
            try {
                return populateUntilLevel(p.getType(), untilLevel - 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toArray();
    }

    private static <T> void populateFields(
            Class<T> instanceClass,
            T instance,
            int untilLevel
    ) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Field[] fields = instanceClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.trySetAccessible() && field.get(instance) == null) {
                Class fieldClass = field.getType();
                if (fieldClass.isPrimitive() || defaultValues.containsKey(fieldClass)) {
                    field.set(instance, defaultValues.get(fieldClass));
                } else if (fieldClass.equals(String.class)) {
                    field.set(instance, field.getName());
                } else {
                    field.set(instance, populateUntilLevel(fieldClass, untilLevel - 1));
                }
                field.setAccessible(false);
            }
        }
    }

}
