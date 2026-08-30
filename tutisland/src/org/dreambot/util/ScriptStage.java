package org.dreambot.util;
/*
singleton to hold active leaf information
 */
public class ScriptStage {
    private ScriptStage() {}
    private static final ScriptStage scriptStage = new ScriptStage();
    private String activeLeaf = "";
    public static ScriptStage getScriptStage() {
        return scriptStage;
    }

    public String getActiveLeaf() {
        return activeLeaf;
    }

    public void setActiveLeaf(String activeLeaf) {
        this.activeLeaf = activeLeaf;
    }
}
