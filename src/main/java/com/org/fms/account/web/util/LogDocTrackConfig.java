package com.org.fms.account.web.util;

import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * Loads Swagger resources, adds RequestTrackingId, Adds a CORS filter for swagger to run properly
 * @author Kshitiz Garg
 */
@Configuration
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
public class LogDocTrackConfig extends WebMvcConfigurerAdapter {
	
	@Value("${serviorgaseUrl}")
	private String serviorgaseUrl;
	@Value("${serviceTitle}")
	private String serviceTitle;
	@Value("${serviceDescription}")
	private String serviceDescription;
	@Value("${serviceVersion}")
	private String serviceVersion;
	
	@Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).
    		useDefaultResponseMessages(false).select().
    		apis(Predicates.not(RequestHandlerSelectors.withClassAnnotation(Deprecated.class))).
    		paths(PathSelectors.regex(serviorgaseUrl)).build()
            .apiInfo(apiInfo());
	}
	
	@Bean
	public RequestLoggingFilter requestLoggingFilter() {
		RequestLoggingFilter rlf = new RequestLoggingFilter();
		rlf.setIncludeClientInfo(true);
		rlf.setIncludeQueryString(true);
		rlf.setIncludePayload(true);
	    return rlf;
	}
	
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(serviceTitle)
            .description(serviceDescription)
            .version(serviceVersion)
            .build();
    }
    
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("swagger-ui.html")
	      .addResourceLocations("classpath:/META-INF/resources/");
	 
	    registry.addResourceHandler("/webjars/**")
	      .addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Do nothing
    }

}

@Component
@EnableAspectJAutoProxy
@Aspect
class PerfLogManager {

	private static final String PERF_LOG_SCOPE = "within(com.org.fms.account.controller..*) || within(com.org.fms.account.service..*)";

	private final Logger logger = LoggerFactory.getLogger("app-perf");
	
    private static final String DOTS = "..";
	private static final String DOT = ".";
	private static final String EMPTY = "";
	private static final String OP_BRACK = "(";
	private static final String COMMA = ",";
	private static final String MILLIS = " millis";
	private static final String ARROW = " -> ";
	private static final String CLO_BRACK = ")";
	/**
	 * Advice for perf logs
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around(PERF_LOG_SCOPE)
	public Object logExceutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object retVal = joinPoint.proceed();

		stopWatch.stop();

		StringBuilder logMessage = new StringBuilder();
		logMessage.append(joinPoint.getTarget().getClass().getSimpleName()).append(DOT).append(joinPoint.getSignature().getName()).append(OP_BRACK);
		Object[] args = joinPoint.getArgs();
		String prefix = EMPTY;
		for (int i = 0; i < args.length; i++) {
			logMessage.append(prefix);
			if(args[i]!=null && args[i].toString().length()>15){
				logMessage.append(args[i].toString().substring(0, 15)).append(DOTS);
			}
			else{
				logMessage.append(args[i]);
			}
			prefix = COMMA;
		}
		logMessage.append(CLO_BRACK).append(ARROW).append(stopWatch.getTotalTimeMillis()).append(MILLIS);
		getLogger().debug(logMessage.toString());
		return retVal;
	}
	
	// For UTCs
	Logger getLogger() {
		return logger;
	}

}

class RequestLoggingFilter extends AbstractRequestLoggingFilter {
	
	private static Pattern swaggerPattern = Pattern.compile("/v2/api-docs|springfox-swagger-ui|swagger-resources|swagger-ui");
	
	public static final String REQUEST_TRACK_ID = "requestTrackId";
	
	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		return logger.isDebugEnabled();
	}

	/**
	 * Adds REQUEST_TRACK_ID and writes a log message before the request is processed.
	 */
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		if(swaggerPattern.matcher(request.getRequestURI()).find()){
			return;
		}
		String requestTrackId = request.getHeader(REQUEST_TRACK_ID);
		if (requestTrackId == null) {
			requestTrackId = UUID.randomUUID().toString();
			MDC.put(REQUEST_TRACK_ID, requestTrackId);
		} 
		else {
			MDC.put(REQUEST_TRACK_ID, requestTrackId);
		}
		logger.debug(message);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		MDC.remove(REQUEST_TRACK_ID);
	}
	
}