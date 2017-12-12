package org.fao.gift.upload;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.commons.dto.MainConfig;
import org.fao.gift.upload.future.GiftMetadataAttachment;
import org.fao.gift.commons.utils.DataSource;
import org.junit.Before;
import org.junit.Test;

public class GiftMetadataAttachmentTest {
    GiftMetadataAttachment giftMetadataAttachmentManager;

    @Before
    public void setUp() throws Exception {
        MainConfig config = CDISupport.getInstance(MainConfig.class);
        config.init(this.getClass().getResourceAsStream("/org/fao/gift/config/main.properties"));
        CDISupport.getInstance(UploaderConfig.class).init(this.getClass().getResourceAsStream("/org/fao/gift/config/upload.properties"));
        CDISupport.getInstance(DataSource.class).init(config.get("gift.db.url"),config.get("gift.db.usr"),config.get("gift.db.psw"));

        giftMetadataAttachmentManager = CDISupport.getInstance(GiftMetadataAttachment.class);
    }

    @Test
    public void mainLogic1() throws Exception {
        giftMetadataAttachmentManager.mainLogic("000042BUR201001", "burkina_test1.zip", this.getClass().getResourceAsStream("/test/burkina_test1.zip"));
    }

}