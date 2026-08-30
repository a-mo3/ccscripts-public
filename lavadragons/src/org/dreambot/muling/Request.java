package org.dreambot.muling;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreambot.muling.messages.client.MuleRequestMessage;

@Getter
@RequiredArgsConstructor
public class Request {
    @SerializedName("client")
    private final Client client;
    @SerializedName("mule")
    private final Client mule;
    @SerializedName("muleRequest")
    private final MuleRequestMessage muleRequest;

    @Setter
    private boolean completed;
}
