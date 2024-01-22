package fr.njj.galaxion.endtoendtesting.lib.logging;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

@Provider
@RegisterForReflection
public class LoggingFilter implements ContainerRequestFilter {

    @Context
    UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        MDC.put("requestMethod", containerRequestContext.getMethod());
        MDC.put("requestPath", uriInfo.getPath());
        MDC.put("requestUri", uriInfo.getRequestUri().toString());
        MDC.put("userAgent", containerRequestContext.getHeaderString("User-Agent"));
        MDC.put("queryString", getQueryString());
    }

    private String getQueryString() {
        var queryParams = uriInfo.getQueryParameters();
        var queryStringBuilder = new StringBuilder();
        for (var key : queryParams.keySet()) {
            var values = queryParams.get(key);
            for (String value : values) {
                queryStringBuilder.append(key).append("=").append(value).append("&");
            }
        }
        var queryString = queryStringBuilder.toString();
        if (!queryString.isEmpty()) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }
        return queryString;
    }
}
