package org.fao.gift.init;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.common.utils.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.WebApplicationException;

@WebListener
public class Initializer implements ServletContextListener {
    @Inject MainConfig config;
    @Inject UploaderConfig uploaderConfig;
    @Inject DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(Initializer.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            if(config == null)
                config.init(this.getClass().getResourceAsStream("/org/fao/gift/config/main.properties"));
            log.info("config init!!!");
            uploaderConfig.init(this.getClass().getResourceAsStream("/org/fao/gift/config/upload.properties"));
            dataSource.init(config.get("gift.db.url"),config.get("gift.db.usr"),config.get("gift.db.psw"));
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}
