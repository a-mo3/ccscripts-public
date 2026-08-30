package org.dreambot.fractals.util;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * exists because i dont know how to make gson seralize a list directly
 */
@AllArgsConstructor
@Setter
@Getter
public class BuyLimitStore {
    @SerializedName("data")
    List<BuyLimitData> data;
}
