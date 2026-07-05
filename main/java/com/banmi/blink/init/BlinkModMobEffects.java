/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.banmi.blink.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import com.banmi.blink.potion.FocusingMobEffect;
import com.banmi.blink.BlinkMod;

public class BlinkModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, BlinkMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> FOCUSING = REGISTRY.register("focusing", () -> new FocusingMobEffect());
}