package org.fao.gift.rest.application;

import io.swagger.jaxrs.config.BeanConfig;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.rest.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/v1")
public class ServiceRegistry extends Application {
    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
    private static final String DEFAULT_HOST="localhost:8080";
    private static final String DEFAULT_BASEPATH="/gift/v1";

    @Inject MainConfig config;

    public ServiceRegistry() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        log.info("id config != null?? {} ",config);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(config==null || config.size() ==0 ? DEFAULT_HOST: config.get("application.host").toString()+":"+config.get("application.host").toString());
        beanConfig.setBasePath(config==null || config.size() ==0 ? DEFAULT_BASEPATH: config.get("application.basePath"));
        beanConfig.setResourcePackage(ResourceService.class.getPackage().getName());
        beanConfig.setTitle("RESTEasy, Swagger and Swagger UI Example");
        beanConfig.setDescription("Sample RESTful API built using RESTEasy, Swagger and Swagger UI");
        beanConfig.setScan(true);
    }
}
