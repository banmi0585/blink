package com.banmi.blink.recipe.brewing;

import com.banmi.blink.init.BlinkModPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import java.util.Optional;

@EventBusSubscriber
public class FocuspotionBrewingRecipe implements IBrewingRecipe {

    @SubscribeEvent
    public static void init(RegisterBrewingRecipesEvent event) {
        // 1. 注册到 NeoForge 酿造系统（让游戏能酿造）
        event.getBuilder().addRecipe(new FocuspotionBrewingRecipe());

        // 2. 注册到原版 PotionBrewing 列表（让 JEI 能显示）
        // 注意：第二个参数是 Item，不是 Ingredient
        event.getBuilder().addMix(
            Potions.NIGHT_VISION,  // 输入药水
            Items.ENDER_EYE,       // 催化物（直接传 Item，不是 Ingredient）
            BlinkModPotions.FOCUS  // 输出药水
        );

        System.out.println("[Blink] 专注药水配方已注册（酿造 + JEI 显示）");
    }

    @Override
    public boolean isInput(ItemStack input) {
        Item inputItem = input.getItem();
        Optional<Holder<Potion>> optionalPotion = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
        return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION)
            && optionalPotion.isPresent()
            && optionalPotion.get().is(Potions.NIGHT_VISION);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return Ingredient.of(new ItemStack(Items.ENDER_EYE)).test(ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            return PotionContents.createItemStack(input.getItem(), BlinkModPotions.FOCUS);
        }
        return ItemStack.EMPTY;
    }
}