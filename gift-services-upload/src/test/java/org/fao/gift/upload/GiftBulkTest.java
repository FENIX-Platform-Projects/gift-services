package org.fao.gift.upload;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.commons.dto.MainConfig;
import org.fao.gift.commons.utils.DataSource;
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
    public void testBUR() throws Exception {
        giftBulkManager.mainLogic("000042BUR201001", this.getClass().getResourceAsStream("/test/burkina_test1.zip"));
    }
    @Test
    public void testPHI() throws Exception {
        //giftBulkManager.mainLogic("000196PHI201001", this.getClass().getResourceAsStream("/test/philippines_test1.zip"));
        giftBulkManager.mainLogic("D3S_32453091774832789368582605865561866810", this.getClass().getResourceAsStream("/test/Philippines.zip"));
    }
    @Test
    public void testBGD() throws Exception {
        giftBulkManager.mainLogic("000023BGD201001", this.getClass().getResourceAsStream("/test/bangladesh_test1.zip"));
    }
    @Test
    public void testUGA() throws Exception {
        giftBulkManager.mainLogic("000253UGA201001", this.getClass().getResourceAsStream("/test/uganda_test1.zip"));
    }

}