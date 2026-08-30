package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BanReport {
    @SerializedName("account_hash")
    public String accountHash;
    @SerializedName("dreambot_user")
    public String dreambotUser;
    @SerializedName("ban")
    public boolean ban = true;
    @SerializedName("script_name")
    public String scriptName;
}
