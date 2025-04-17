package xiaofeiyang.study.ai.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xiaofeiyang.study.ai.common.Result;
import xiaofeiyang.study.ai.common.aspect.annotation.NoRepeatSubmit;
import xiaofeiyang.study.ai.common.exception.BizException;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class NoRepeatSubmitAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(xiaofeiyang.study.ai.common.aspect.annotation.NoRepeatSubmit)")
    public void pointcut() {}

    @Around("pointcut() && @annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            throw new BizException("此请求不支持防重复提交");
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        String key;

        // 取得关键参数的值
        String keyParam = extractKeyParam(joinPoint, noRepeatSubmit.keyword());

        if (noRepeatSubmit.includeUser()) {
            // 如果注解指定了includeUser为true，则使用用户信息（如token）
            key = "no_repeat:user_and_" + DigestUtils.md5DigestAsHex((token + uri + keyParam).getBytes());
        } else {
            // 如果没有includeUser，则忽略用户信息
            key = "no_repeat:" + DigestUtils.md5DigestAsHex((uri + keyParam).getBytes());
        }

        // 设置时间窗口
        int interval = noRepeatSubmit.interval();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "", interval, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            log.warn("重复请求拦截: {}", key);
            throw new BizException("请勿重复提交");
            //return Result.fail("请勿重复提交");
        }

        // 正常执行方法
        return joinPoint.proceed();
    }

    /**
     * 提取关键参数值
     * @param joinPoint
     * @param keyword
     * @return
     */
    private String extractKeyParam(ProceedingJoinPoint joinPoint, String keyword) {
        // 如果keyword为空，表示使用全量请求参数
        if (StringUtil.isBlank(keyword)) {
            return Arrays.toString(joinPoint.getArgs());
        }

        // 从方法参数中提取
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object argValue = args[i];

            // 1. 参数名直接匹配
            if (keyword.equals(parameter.getName()) && argValue != null) {
                return argValue.toString();
            }

            // 2. 参数是对象类型，尝试通过字段名提取
            if (argValue != null && !(argValue instanceof String) && !(argValue instanceof Number)) {
                try {
                    Field field = getFieldRecursive(argValue.getClass(), keyword);
                    if (field != null) {
                        field.setAccessible(true);
                        Object fieldValue = field.get(argValue);
                        if (fieldValue != null) {
                            return fieldValue.toString();
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }

        return null; // 如果还找不到，就返回 null，调用方处理异常逻辑
    }

    private Field getFieldRecursive(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // 继续在父类找
            }
        }
        return null;
    }
}
