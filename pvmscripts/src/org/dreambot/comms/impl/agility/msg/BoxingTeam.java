package org.dreambot.comms.impl.agility.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.utilities.Timer;


@Setter
@Getter
@Accessors(chain = true)
public class BoxingTeam {
    String memberA;
    String memberB;

    public BoxingTeam(String memberA, String memberB) {
        this.memberA = memberA;
        this.memberB = memberB;
    }

    Timer sinceHop = new Timer(30 * 1000); // tracks time since world hop so when both accounts report a pker we dont hop a trillion times
}
