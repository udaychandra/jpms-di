package ud.jpms.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

public class IOC {

    private static final Set<Class> USES_SET = new HashSet<>();

    public static <C> C newService(Class<C> componentClass) {

        var components = newComponentList(componentClass);

        return components.size() > 0 ? components.get(0) : null;
    }

    public static <C> List<C> newComponentList(Class<C> componentClass) {

        if (!USES_SET.contains(componentClass)) {
            Module module = IOC.class.getModule();
            module.addUses(componentClass);

            USES_SET.add(componentClass);
        }

        List<C> componentList = new ArrayList<>();

        var componentIterator = ServiceLoader.load(componentClass);

        componentIterator.forEach(component -> {
            processComponent(component);

            componentList.add(component);
        });

        return componentList;
    }

    private static <C> void processComponent(C component) {
        var methods = component.getClass().getMethods();

        for (var method : methods) {

            if (!isPublicInstanceMethod(method) || !method.isAnnotationPresent(Reference.class)) {
                continue;
            }

            validateReferenceMethodParameter(method);

            Reference reference = method.getAnnotation(Reference.class);

            invokeReferenceMethod(component, method, reference);
        }
    }

    private static <C> void invokeReferenceMethod(C component,
                                                  Method method,
                                                  Reference reference) {

        Class<?> parameterClass = getParameterClass(method);
        var referenceComponents = newComponentList(parameterClass);

        try {
            switch (reference.cardinality()) {

                case ZERO_OR_ONE:
                    if (referenceComponents.size() > 0) {
                        method.invoke(component, referenceComponents.get(0));
                    }

                    break;

                case ONE:
                    if (referenceComponents.size() > 0) {
                        method.invoke(component, referenceComponents.get(0));

                    } else {
                        throw new RuntimeException(String.format("Unable to find a component for parameter type %s in reference method %s",
                                parameterClass.getTypeName(),
                                method.getName()));
                    }
                    break;

                case ZERO_OR_MORE:
                    method.invoke(component, referenceComponents);
                    break;

                case ONE_OR_MORE:
                    if (referenceComponents.size() > 0) {
                        method.invoke(component, referenceComponents);

                    } else {
                        throw new RuntimeException(String.format("Unable to find one or more components for parameter type %s in reference method %s",
                                parameterClass.getTypeName(),
                                method.getName()));
                    }

                    break;
            }

        } catch (IllegalAccessException |InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Class<?> getParameterClass(Method method) {

        Parameter parameter = method.getParameters()[0];

        if (parameter.getType() == List.class) {

            Type[] types = method.getGenericParameterTypes();
            ParameterizedType parameterizedType = (ParameterizedType) types[0];

            return (Class<?>) parameterizedType.getActualTypeArguments()[0];


        } else {
            return parameter.getType();
        }
    }

    private static void validateReferenceMethodParameter(Method method) {
        if (method.getParameterCount() != 1) {
            throw new RuntimeException(String.format("%s is annotated with @Reference. It must have a single parameter",
                    method.getName()));
        }

        Parameter parameter = method.getParameters()[0];

        if (parameter.getType().isArray()) {
            throw new RuntimeException(String.format("%s is annotated with @Reference. It does not support array parameter %s",
                    method.getName(),
                    parameter.getName()));
        }
    }

    private static boolean isPublicInstanceMethod(Method method) {
        int mod = method.getModifiers();

        return Modifier.isPublic(mod) && !Modifier.isStatic(mod);
    }
}

