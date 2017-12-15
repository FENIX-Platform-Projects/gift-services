package org.fao.gift.config.email;

import org.fao.gift.common.dto.MainConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@ApplicationScoped
public class MailTemplateManager {

    private static final Logger log = LoggerFactory.getLogger(MailTemplateManager.class);
    private static final String DEFAULT_TEMPLATE_EXT = ".htm";
    private static final Map<MailTemplate, String> templates = new HashMap<>();

    @Inject
    private MainConfig config;

    /**
     * Retrieves and caches a template file
     *
     * @return the template file as a {@link String} object
     */
    public String loadTemplate(MailTemplate mailTemplate) {
        if (templates.get(mailTemplate) == null) {
            try (InputStream resourceStream = MailTemplateManager.class.getClassLoader().getResourceAsStream(config.get("gift.mail.template.path").concat(mailTemplate.name().concat(DEFAULT_TEMPLATE_EXT)))) {
                if (resourceStream == null) throw new IOException();
                templates.put(mailTemplate, new Scanner(resourceStream).useDelimiter("\\A").next());

            } catch (IOException e) {
                log.error("Could not read email file for template: {}", mailTemplate, e);
                throw new RuntimeException("Missing email template file ".concat(mailTemplate.name()));
            }
        }

        return templates.get(mailTemplate);
    }
}
