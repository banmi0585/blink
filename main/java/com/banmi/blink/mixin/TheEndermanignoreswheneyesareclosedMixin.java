package com.banmi.blink.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import com.banmi.blink.EyeStateProvider;

@Mixin(EnderMan.class)
public abstract class TheEndermanignoreswheneyesareclosedMixin {
    
    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void onIsLookingAtMe(Player player, CallbackInfoReturnable<Boolean> cir) {
        // 检查南瓜头
        if (player.getInventory().getArmor(3).getItem() == Items.CARVED_PUMPKIN) {
            cir.setReturnValue(false);
            return;
        }
        
        // 检查闭眼状态
        if (EyeStateProvider.isPlayerEyeClosed()) {
            cir.setReturnValue(false);
        }
    }
}