package org.dreambot.settings.discordwebhook.pojo;

public class WebHookPojo {
    private String username;
    private String content;
    private String avatar_url;
    private EmbedPojo[] embeds;

    public WebHookPojo() {
    }

    public WebHookPojo(EmbedPojo[] embeds) {
        this.embeds = embeds;
    }


    public EmbedPojo[] getEmbeds() {
        return embeds;
    }

    public WebHookPojo setEmbeds(EmbedPojo... embeds) {
        this.embeds = embeds;
        return this;
    }

    public String getContent() {
        return content;
    }

    public WebHookPojo setContent(String content) {
        this.content = content;
        return this;
    }
    public String getUsername() {
        return username;
    }

    public WebHookPojo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public WebHookPojo setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
        return this;
    }
}
