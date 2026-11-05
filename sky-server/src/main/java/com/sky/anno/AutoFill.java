package com.sky.anno;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 放在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface AutoFill {
    // 数据库操作 update insert
    OperationType value();
}
