package ru.pyatkinmv.aop;

import com.vk.api.sdk.exceptions.ApiTooManyException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class VkRequestWrappingAspect {
    private static final int BETWEEN_REQUESTS_DELAY_MILLIS = 1000;

    @Around(value = "execution(public * ru.pyatkinmv.vk..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (ApiTooManyException ex) {
            // TODO: What with ApiCaptchaException?
            log.error(ex.getMessage());
            Thread.sleep(BETWEEN_REQUESTS_DELAY_MILLIS);
            log.info("Trying to execute again...");
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred: ", throwable);
            throw throwable;
        }

        return result;
    }
}
