/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.banmi.blink.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.registries.Registries;

import com.banmi.blink.BlinkMod;

public class BlinkModPotions {
	public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(Registries.POTION, BlinkMod.MODID);
	public static final DeferredHolder<Potion, Potion> FOCUS = REGISTRY.register("focus", () -> new Potion(new MobEffectInstance(BlinkModMobEffects.FOCUSING, 3600, 0, false, true)));
}