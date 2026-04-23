package com.airquality.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限注解
 * 标记需要管理员权限的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminRequired {
}
