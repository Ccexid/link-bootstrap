package me.link.bootstrap.core.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.core.annotation.IdGenerator;
import me.link.bootstrap.util.IdUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Component
public class DefaultDBFieldHandler implements MetaObjectHandler {

    /**
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Field[] fields = metaObject.getOriginalObject().getClass().getDeclaredFields();
        Arrays.asList(fields).forEach(field -> {
            if (field.isAnnotationPresent(IdGenerator.class)) {
                var anno = field.getAnnotation(IdGenerator.class);
                Object currentValue = metaObject.getValue(field.getName());
                if (ObjectUtil.isEmpty(currentValue)) {
                    String code = IdUtils.getNextId(anno.prefix(), anno.digit(), anno.daily());
                    this.setFieldValByName(field.getName(), code, metaObject);
                }
            }
        });
    }

    /**
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
