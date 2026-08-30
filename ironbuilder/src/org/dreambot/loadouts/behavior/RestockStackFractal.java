package org.dreambot.loadouts.behavior;

import lombok.Getter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.IronFractal;

import java.util.Stack;

/**
 * we're moving away from events in lost client, because events cause such a problem
 * and at the start of all trees we have this, it has a stack of fractals, if the stack is empty we do script logic
 * if the stack is not empty we execute the top fractal until its accept condition is met and then we pop to the next
 * <p>
 * this is a natural way to model Iron Man reqs, ie we need logs, we need an axe, we need 5000 coins to buy axe
 * <p>
 * I see a lot of people generally do an approach like this with a bank task with a state, ill see how it works out.
 */
public class RestockStackFractal extends IronFractal {
    @Getter
    private static Stack<IronFractal> restockTasks = new Stack<>();

    public RestockStackFractal() {
        super(() -> !restockTasks.isEmpty());
        setSimpleName("Restock");
    }

    @Override
    protected int onLoop() {
        IronFractal top = restockTasks.peek();
        if (!top.isValid()) {
            log("Top is valid popping " + top.getSimpleName());
            restockTasks.pop();
            return sleep();
        }

        log("Restock loop");
        return top.run();
    }

    public static void addTask(IronFractal method) {
        if (method == null) {
            Logger.warn("Tried to add a null restock method");
            return;
        }
        restockTasks.add(method);
    }
}
