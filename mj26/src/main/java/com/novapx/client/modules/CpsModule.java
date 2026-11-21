package com.novapx.client.modules;

import com.novapx.client.Module;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.Minecraft;

/** Clics por segundo (izquierdo | derecho), contados en el último segundo. */
public class CpsModule extends Module {
    private final Deque<Long> left = new ArrayDeque<>(), right = new ArrayDeque<>();
    private boolean wasLeft, wasRight;

    public CpsModule() { super("cps", "CPS", "Clics por segundo.", true); }

    private static void trim(Deque<Long> q, long now) { while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst(); }

    private void poll(Minecraft client) {
        long now = System.currentTimeMillis();
        boolean l = client.mouseHandler.isLeftPressed();
        boolean r = client.mouseHandler.isRightPressed();
        if (l && !wasLeft) left.addLast(now);
        if (r && !wasRight) right.addLast(now);
        wasLeft = l; wasRight = r;
        trim(left, now); trim(right, now);
    }
    @Override public void tick(Minecraft client) { poll(client); }
    @Override public String line(Minecraft client) { poll(client); return left.size() + " | " + right.size() + " CPS"; }
}
