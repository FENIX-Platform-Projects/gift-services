Any new template must be added in the Enum 
`org.fao.gift.config.email.MailTemplate` in upper case, 
in order to be used in services.

To retrieve a template content use `org.fao.gift.config.email.MailTemplateManager`:

    @Inject
    private MailTemplateManager mailTemplateManager;
    
    ...
    String templateContent = mailTemplateManager.loadTemplate(MailTemplate.MD_CREATION)
    
Template contents are cached on the first load, so feel free to recall `mailTemplateManager.loadTemplate()` as many times as you need. 