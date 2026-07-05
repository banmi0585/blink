package com.banmi.blink;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY = "key.category.blink";
    public static final String KEY_BLINK = "key.blink.blink";
    
    public static KeyMapping blinkKey = new KeyMapping(
        KEY_BLINK,
        GLFW.GLFW_KEY_C,
        KEY_CATEGORY
    );
}