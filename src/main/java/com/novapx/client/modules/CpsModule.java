package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Clics por segundo (izquierdo | derecho), contados en el último segundo. */
public class CpsModule extends Module {
    private final Deque<Long> left = new ArrayDeque<>();
    private final Deque<Long> right = new ArrayDeque<>();
    private boolean wasLeft, wasRight;

    public CpsModule() { super("cps", "CPS", "Clics por segundo.", true); }

    private static void trim(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
    }

    /** Mira los botones del ratón y cuenta cada clic nuevo. Se llama en cada tick y al dibujar, para no perder clics rápidos. */
    private void poll(MinecraftClient client) {
        long now = System.currentTimeMillis();
        long handle = client.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (l && !wasLeft) left.addLast(now);
        if (r && !wasRight) right.addLast(now);
        wasLeft = l;
        wasRight = r;
        trim(left, now);
        trim(right, now);
    }

    @Override
    public void tick(MinecraftClient client) { poll(client); }

    @Override
    public String line(MinecraftClient client) {
        poll(client);
        return left.size() + " | " + right.size() + " CPS";
    }
}
