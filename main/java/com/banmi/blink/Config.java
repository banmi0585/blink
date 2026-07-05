package com.banmi.blink;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    
    public static class Common {
        public final ModConfigSpec.IntValue blinkIntervalMin;
        public final ModConfigSpec.IntValue blinkIntervalMax;
        public final ModConfigSpec.DoubleValue upperLidPercent;
        
        Common(ModConfigSpec.Builder builder) {
            builder.comment("眨眼模组配置")
                   .push("blink");
            
            blinkIntervalMin = builder
                    .comment("自动眨眼最短间隔（秒）")
                    .defineInRange("blinkIntervalMin", 5, 1, 30);
            
            blinkIntervalMax = builder
                    .comment("自动眨眼最长间隔（秒）")
                    .defineInRange("blinkIntervalMax", 10, 3, 60);
            
            upperLidPercent = builder
                    .comment("上眼皮占比（0.0-1.0），剩余为下眼皮")
                    .defineInRange("upperLidPercent", 0.5, 0.0, 1.0);
            
            builder.pop();
        }
    }
    
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    
    static {
        final Pair<Common, ModConfigSpec> specPair = 
            new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }
}