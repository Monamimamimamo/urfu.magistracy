package urfumagistracy.backend;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}



    @Before("restControllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("Controller calling: "
                + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "restControllerMethods()", returning = "result")
    public void logAfterReturningController(JoinPoint joinPoint, Object result) {
        logger.info("Controller "
                + joinPoint.getSignature().getName()
                + " returns: "
                + result);
    }

    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        logger.info("Calling service: "
                + joinPoint.getSignature().getName()
                + " with args: "
                + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        logger.info("Service "
                + joinPoint.getSignature().getName()
                + " result: "
                + result);
    }
}