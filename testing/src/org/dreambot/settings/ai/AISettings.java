package org.dreambot.settings.ai;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.script.Unobfuscated;

@Getter @Setter
public class AISettings {
    @Unobfuscated
    public boolean useAIMouse = true;
    @Unobfuscated
    public boolean useAIReactionTimes = true;
    @Unobfuscated
    public boolean useAISkillTraining = false;
}
