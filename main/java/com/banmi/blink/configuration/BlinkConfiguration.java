package com.banmi.blink.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BlinkConfiguration {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;

	public static final ModConfigSpec.IntValue BLINK_INTERVAL_MIN;
	public static final ModConfigSpec.IntValue BLINK_INTERVAL_MAX;
	public static final ModConfigSpec.DoubleValue UPPER_LID_PERCENT;

	static {
		BUILDER.comment("眨眼模组配置").push("blink");

		BLINK_INTERVAL_MIN = BUILDER
				.comment("自动眨眼最短间隔（秒）")
				.defineInRange("blinkIntervalMin", 5, 1, 30);

		BLINK_INTERVAL_MAX = BUILDER
				.comment("自动眨眼最长间隔（秒）")
				.defineInRange("blinkIntervalMax", 10, 3, 60);

		UPPER_LID_PERCENT = BUILDER
				.comment("上眼皮占比（0.0-1.0），剩余为下眼皮")
				.defineInRange("upperLidPercent", 0.5, 0.0, 1.0);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}