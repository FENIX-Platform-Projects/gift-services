package org.fao.gift.init;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.dto.HostProperties;
import org.fao.gift.dto.MainConfig;
import org.fao.gift.utils.DataSource;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.WebApplicationException;
import java.util.Map;

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
