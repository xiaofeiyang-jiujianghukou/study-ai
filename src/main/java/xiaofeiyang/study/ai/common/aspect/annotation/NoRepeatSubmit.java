package xiaofeiyang.study.ai.common.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {

    // 防重复提交的时间窗口（秒）
    int interval() default 5;

    // 关键字段，若不传递则默认为全量请求参数
    String keyword() default "";

    // 是否将用户信息作为key的一部分
    boolean includeUser() default false; // 默认为 false
}