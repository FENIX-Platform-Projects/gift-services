package org.fao.gift.rest.application;

import io.swagger.jaxrs.config.BeanConfig;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.rest.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Map;


@ApplicationPath("/v1")
public class ServiceRegistry extends Application {
    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
    private static final String DEFAULT_HOST="localhost:8080";
    private static final String DEFAULT_BASEPATH="/gift/v1";

    private static final String DEFAULT_MAIN_PATH="/org/fao/gift/config/main.properties";

    @Inject MainConfig config;

    public ServiceRegistry() {
        try {
            testEnv();
            if(config !=null) {
                config.init(this.getClass().getResourceAsStream(DEFAULT_MAIN_PATH));
            }
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
        beanConfig.setHost(config==null || config.size() ==0 ?
                DEFAULT_HOST:
                checkAndQueries("application.host","application.port") ?
                        DEFAULT_HOST:
                        config.get("application.host").toString()+":"+config.get("application.port").toString());
        beanConfig.setBasePath(config==null || config.size() ==0 ? DEFAULT_BASEPATH: config.get("application.basePath"));
        beanConfig.setResourcePackage(ResourceService.class.getPackage().getName());
        beanConfig.setTitle("RESTEasy, Swagger and Swagger UI Example");
        beanConfig.setDescription("Sample RESTful API built using RESTEasy, Swagger and Swagger UI");
        beanConfig.setScan(true);
    }


    private boolean checkAndQueries (String... parameters) {
        if (config==null || parameters == null || parameters.length==0)
            return false;
        // return true if all parameters are true
        for(String parameter : parameters)
            if(config.get(parameter) == null)
                return false;
        return true;
    }

    private void testEnv () {
        Map<String, String> env = System.getenv();

        if(env!= null && !env.isEmpty() && env.size()>0)
            for (String envName : env.keySet())
                    if(envName.equals("GIFT_SERVICES_ENV"))
                        System.out.format("%s=%s%n",envName,env.get(envName));
            }

}
