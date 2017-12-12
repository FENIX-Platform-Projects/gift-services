package org.fao.gift.init;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.commons.dto.MainConfig;
import org.fao.gift.commons.utils.DataSource;

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

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            config.init(this.getClass().getResourceAsStream("/org/fao/gift/config/main.properties"));
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
