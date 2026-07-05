package com.banmi.blink.procedures;

import net.minecraft.client.Minecraft;
import java.lang.reflect.Method;

public class Manual_blink_triggerProcedure {
    
    // 仅作为外部调用入口（如指令、其他模组调用）
    public static void execute() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gui != null) {
                Method method = mc.gui.getClass().getMethod("manualBlink");
                method.invoke(mc.gui);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}