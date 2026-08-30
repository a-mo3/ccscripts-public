package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.core.Instance;
import org.dreambot.fractals.FractalAPI;

@Data
@Accessors(chain = true)
public class BanReport {
    @SerializedName("time")
    public long time = System.currentTimeMillis();
    @SerializedName("token")
    public String token = Client.getForumUser().getAuthenticationCode();
    @SerializedName("account_hash")
    public String accountHash = AnalyticsReporter.hashStringSHA256(ScriptManager.getScriptManager().getAccountNickname());
    @SerializedName("dreambot_user")
    public String dreambotUser = Client.getForumUser().getUsername();
    @SerializedName("script_name")
    public String scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase();
    @SerializedName("minutes")
    public int minutes = 15;
    @SerializedName("no_click_walk")
    public boolean noClickWalk = Walking.isNoClickWalkEnabled();
    @SerializedName("menu_manip")
    public boolean menuManip = Menu.isMenuManipulationActive();
    @SerializedName("active_fractal")
    public String activeFractal = FractalAPI.hierarchy.toString();
    @SerializedName("is_breaking")
    public boolean breaking = Client.getInstance().getRandomManager().getBreakSolver().isBreakRunning();
    @SerializedName("is_member")
    public boolean isMember = Client.isMembers();
    @SerializedName("is_covert")
    public boolean isCovert = Instance.isCovertEnabled();
}
