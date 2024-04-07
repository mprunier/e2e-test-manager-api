package fr.njj.galaxion.endtoendtesting.lib.logging;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;

@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {

    private static String getArgumentsMessage(InvocationContext context) {
        var arguments = context.getParameters();
        var sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            var parameter = context.getMethod().getParameters()[i];
            var parameterName = parameter.getName();
            var parameterValue = arguments[i];
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(parameterName).append(": ").append(parameterValue);
        }
        return StringUtils.isNotBlank(sb.toString()) ? sb.toString() : "No argument";
    }

    @AroundInvoke
    public Object logMethodInvocation(InvocationContext context) throws Exception {
        var logger = Logger.getLogger(context.getMethod().getDeclaringClass());

        var sw = new StopWatch();
        logger.debugf("[%s] --> %s", context.getMethod().getName(), getArgumentsMessage(context));
        try {
            sw.start();
            var result = context.proceed();
            sw.stop();
            logger.debugf("[%s] <-- %s", context.getMethod().getName(), result != null ? result : "Nothing");
            return result;
        } catch (CustomException exception) {
            sw.stop();
            var status = Response.Status.fromStatusCode(exception.getStatus());
            if (status == null || Response.Status.Family.SERVER_ERROR.equals(status.getFamily())) {
                logger.errorf("[%s] EXCEPTION ↓", context.getMethod().getName());
            } else if (Response.Status.Family.CLIENT_ERROR.equals(status.getFamily())) {
                logger.warnf("[%s] EXCEPTION ↓", context.getMethod().getName());
            }
            throw exception;
        } catch (ResteasyReactiveViolationException exception) {
            sw.stop();
            logger.errorf("[%s] EXCEPTION <-!-> %s", context.getMethod().getName(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            sw.stop();
            logger.errorf("[%s] EXCEPTION ↓", context.getMethod().getName());
            throw exception;
        }
    }
}
