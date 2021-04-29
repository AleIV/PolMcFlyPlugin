package net.noobsters.core.paper.mechanics;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class TimerTickEvent extends Event{
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;

    private final @Getter int second;


    public TimerTickEvent(int second, boolean async) {
        super(async);
        this.second = second;
    }

    public TimerTickEvent(int second) {
        this(second, false);
    }
}
