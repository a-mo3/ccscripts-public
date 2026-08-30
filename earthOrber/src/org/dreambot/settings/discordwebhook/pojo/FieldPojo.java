package org.dreambot.settings.discordwebhook.pojo;

public class FieldPojo {
    private String name = "field name";
    private String value = "value";
    private boolean inline = true;

    public FieldPojo(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    public FieldPojo() {
    }

    public boolean isInline() {
        return inline;
    }

    public FieldPojo setInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FieldPojo setValue(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public FieldPojo setName(String name) {
        this.name = name;
        return this;
    }
}
