package io.specialrooter.context.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.specialrooter.context.ifc.IEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class EnumUtility {

    @SuppressWarnings("unchecked")
    public static <K, T extends IEnum<K, ?>> T getEnumValue(Class<T> enumType, String code) {
        try {
            if (StringUtils.isBlank(code)) {
                return null;
            }

            if (enumType.isEnum()) { //如果是枚举类型
                return Arrays.stream(enumType.getEnumConstants()).filter(x -> {
                    if (((Enum<?>) x).name().equals(code)) {
                        return true;
                    }
                    if (Objects.equals(x.getCode().getClass(), Integer.class)) {
                        return x.getCode().equals(Integer.valueOf(code));
                    } else if (Objects.equals(x.getCode().getClass(), String.class)) {
                        return x.getCode().equals(code);
                    }
                    return false;
                }).findFirst().orElse(null);
            }
        } catch (Exception e) {
            log.error("getEnumValue error", e);
        }
        return null;
    }
}
