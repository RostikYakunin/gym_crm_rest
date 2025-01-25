package com.crm;

import com.crm.commons.LoggingFilter;
import com.crm.config.WebConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
public class SpringWebApp {
    @SneakyThrows
    public static void run() {
        var tomcat = createServer();
        var webContext = createWebContext(tomcat);
        createServlet(webContext);

        log.info("SWAGGER UI DOCs - http://localhost:8080/swagger-ui/index.html");
        log.info("SWAGGER SPECIFICATION - http://localhost:8080/v3/api-docs");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Tomcat createServer() {
        var tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        return tomcat;
    }

    private static Context createWebContext(Tomcat tomcat) {
        var webContext = tomcat.addContext("", null);
        webContext.addFilterDef(createFilter());
        webContext.addFilterMap(createFilterMap());
        return webContext;
    }

    private static FilterDef createFilter() {
        var filterDef = new FilterDef();
        filterDef.setFilterName("LoggingFilter");
        filterDef.setFilterClass(LoggingFilter.class.getName());
        return filterDef;
    }

    private static FilterMap createFilterMap() {
        var filterMap = new FilterMap();
        filterMap.setFilterName("LoggingFilter");
        filterMap.addURLPattern("/*");
        return filterMap;
    }

    private static void createServlet(Context ctx) {
        var servlet = Tomcat.addServlet(ctx, "myServlet", new DispatcherServlet(createWebContext()));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }

    private static AnnotationConfigWebApplicationContext createWebContext() {
        var appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(WebConfig.class);
        return appContext;
    }
}
