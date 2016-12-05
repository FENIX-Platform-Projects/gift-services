package org.fao.gift.upload;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.dto.MainConfig;
import org.fao.gift.utils.DataSource;
import org.junit.Before;
import org.junit.Test;

public class GiftBulkTest {
    GiftBulk giftBulkManager;

    @Before
    public void setUp() throws Exception {
        MainConfig config = CDISupport.getInstance(MainConfig.class);
        config.init(this.getClass().getResourceAsStream("/org/fao/gift/config/main.properties"));
        CDISupport.getInstance(UploaderConfig.class).init(this.getClass().getResourceAsStream("/org/fao/gift/config/upload.properties"));
        CDISupport.getInstance(DataSource.class).init(config.get("gift.db.url"),config.get("gift.db.usr"),config.get("gift.db.psw"));

        giftBulkManager = CDISupport.getInstance(GiftBulk.class);
    }

    @Test
    public void mainLogic1() throws Exception {
        giftBulkManager.mainLogic("000042BUR201001", this.getClass().getResourceAsStream("/test/burkina_test1.zip"));
        //giftBulkManager.mainLogic("000196PHI201001", this.getClass().getResourceAsStream("/test/philippines_test1.zip"));
    }

}