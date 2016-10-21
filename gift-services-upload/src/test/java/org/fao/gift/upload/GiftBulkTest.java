package org.fao.gift.upload;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.dto.MainConfig;
import org.junit.Before;
import org.junit.Test;

public class GiftBulkTest {
    GiftBulk giftBulkManager;

    @Before
    public void setUp() throws Exception {
        CDISupport.getInstance(MainConfig.class).init(this.getClass().getResourceAsStream("/org/fao/gift/config/main.properties"));
        CDISupport.getInstance(UploaderConfig.class).init(this.getClass().getResourceAsStream("/org/fao/gift/config/upload.properties"));
        giftBulkManager = CDISupport.getInstance(GiftBulk.class);
    }

    @Test
    public void mainLogic1() throws Exception {
        giftBulkManager.mainLogic("000042BUR201001", this.getClass().getResourceAsStream("/test/burkina_test1.zip"));
    }

}