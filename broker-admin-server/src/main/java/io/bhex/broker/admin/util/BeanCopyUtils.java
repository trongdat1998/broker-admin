package io.bhex.broker.admin.util;

import com.google.protobuf.ProtocolMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @Description:
 * @Date: 2018/9/29 下午4:22
 * @Author: xuming
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
public class BeanCopyUtils extends BeanUtils {
    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(PropertyDescriptor pd : pds) {
            try {
                Object srcValue = src.getPropertyValue(pd.getName());
                if (srcValue == null) emptyNames.add(pd.getName());
            } catch (NotReadablePropertyException e) { //builder里的list 没有对应的get方法
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target){
        copyProperties(src, target, null, getNullPropertyNames(src));
    }

    public static void copyProperties(Object src, Object target) {
        copyProperties(src, target, null, getNullPropertyNames(src));
    }

    /**
     * Copy the property values of the given source bean into the given target bean.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * @param source the source bean
     * @param target the target bean
     * @param editable the class (or interface) to restrict property setting to
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    private static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
                                       @Nullable String... ignoreProperties) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName()
                        + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();

                    if (readMethod != null) {
                        boolean sameType = ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType());
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (sameType) {
                                writeMethod.invoke(target, value);
                            } else {
                                BiFunction<Class, Class, Boolean> function = (leftClass, rightClass) ->
                                        ClassUtils.isAssignable(leftClass, readMethod.getReturnType())
                                                && ClassUtils.isAssignable(rightClass, writeMethod.getParameterTypes()[0]);

                                if (function.apply(String.class, BigDecimal.class)) {
                                    writeMethod.invoke(target, value.equals("") ? BigDecimal.ZERO : new BigDecimal(String.valueOf(value)));
                                } else if (function.apply(BigDecimal.class, String.class)) {
                                    writeMethod.invoke(target, new BigDecimal(String.valueOf(value)).toPlainString());
                                } else if (function.apply(Timestamp.class, Long.class)) {
                                    writeMethod.invoke(target, ((Timestamp) value).getTime());
                                }  else if (function.apply(ProtocolMessageEnum.class, Integer.class)) {
                                    writeMethod.invoke(target, ((ProtocolMessageEnum) value).getNumber());
                                }
//                                else if (function.apply(Integer.class, ProtocolMessageEnum.class)) {
//                                    //io.bhex.broker.grpc.capital.TradeSideEnum.valueOf(side_);
//                                    log.info(writeMethod.getName());
//                                    log.info(target.getClass()+" "+writeMethod.getName()+"Value"
//                                            +" "+ClassUtils.hasMethod(target.getClass(), writeMethod.getName()+"Value", Integer.class));
//                                    if (ClassUtils.hasMethod(target.getClass(), writeMethod.getName()+"Value", Integer.class)) {
//                                        log.info("ok");
//                                    }
//
//                                    writeMethod.invoke(target, ((ProtocolMessageEnum) value).getNumber());
//                                }
                                else if (function.apply(ProtocolMessageEnum.class, Short.class)) {
                                    writeMethod.invoke(target, (short)((ProtocolMessageEnum) value).getNumber());
                                } else if (function.apply(Long.class, Timestamp.class)) {
                                    Long v = Long.valueOf(value.toString());
                                    if (v > 0) {
                                        writeMethod.invoke(target, new Timestamp(v));
                                    }
                                }
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }


}
