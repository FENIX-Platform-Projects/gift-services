package org.fao.ess.uploader.gift.bulk;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.dto.HostProperties;
import org.fao.gift.dto.MainConfig;
import org.fao.gift.utils.DataSource;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class PolicyBulkTest {
    MainConfig config;
    UploaderConfig uploaderConfig;
    DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        config = CDISupport.getInstance(MainConfig.class);
        uploaderConfig = CDISupport.getInstance(UploaderConfig.class);
        dataSource = CDISupport.getInstance(DataSource.class);

        config.init(this.getClass().getResourceAsStream("/org/fao/amis/policy/config/main.properties"));
        uploaderConfig.init(this.getClass().getResourceAsStream("/org/fao/amis/policy/config/upload.properties"));
        dataSource.init(config.get("gift.db.url"),config.get("gift.db.usr"),config.get("gift.db.psw"));
    }

    @Test
    public void mainLogic1() throws Exception {
    }



    //Utils
    private HostProperties getAttachmentsRemoteFolderHostProperties(Map<String,String> config) {
        return new HostProperties(
                null,
                config.get("gift.remote.host"),
                new Integer(config.get("gift.remote.port")),
                config.get("gift.remote.usr"),
                config.get("gift.remote.psw"),
                config.get("gift.remote.path.bulk.download")
        );
    }


}