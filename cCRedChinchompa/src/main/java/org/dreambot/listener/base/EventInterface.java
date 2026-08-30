package org.dreambot.listener.base;

public interface EventInterface {

    void start();

    void run();

    void stop();

    void fire(Object... params);

}
