package com.banmi.blink.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import com.banmi.blink.KeyBindings;
import com.banmi.blink.EyeStateProvider;
import com.banmi.blink.configuration.BlinkConfiguration;

import java.util.Random;

@Mixin(Gui.class)
public abstract class BlinkingMixin implements EyeStateProvider {
    
    @Unique
    private float blinkProgress = 0.0f;
    
    @Unique
    private long nextBlinkTime = 0;
    
    @Unique
    private boolean isBlinking = false;
    
    @Unique
    private boolean isPaused = false;
    
    @Unique
    private final Random random = new Random();
    
    @Unique
    private float sleepProgress = 0.0f;
    
    @Unique
    private boolean wasSleeping = false;
    
    @Unique
    private static final int BLINK_DURATION = 150;
    
    @Unique
    private static final int FEATHER_SIZE = 30;
    
    @Unique
    private static final float SLEEP_SPEED = 0.02f;
    
    @Unique
    private boolean isEyeClosed = false;
    
    @Unique
    private static final float CLOSE_SPEED = 0.1f;
    
    @Unique
    private static final float OPEN_SPEED = 0.15f;
    
    @Unique
    private boolean shouldOpenEye = false;
    
    // ========== 按键状态变量 ==========
    @Unique
    private boolean wasKeyDown = false;
    
    @Unique
    private long keyPressTime = 0;
    
    @Unique
    private boolean keyHandled = false;
    
    @Unique
    private static final long LONG_PRESS_MS = 300;
    
    // ========== 死亡状态变量 ==========
    @Unique
    private boolean wasDead = false;
    
    // ========== 配置获取方法 ==========
    @Unique
    private int getBlinkIntervalMin() {
        return BlinkConfiguration.BLINK_INTERVAL_MIN.get();
    }
    
    @Unique
    private int getBlinkIntervalMax() {
        return BlinkConfiguration.BLINK_INTERVAL_MAX.get();
    }
    
    @Unique
    private float getUpperLidPercent() {
        return BlinkConfiguration.UPPER_LID_PERCENT.get().floatValue();
    }
    
    @Unique
    private long getRandomBlinkInterval() {
        int min = getBlinkIntervalMin();
        int max = getBlinkIntervalMax();
        if (min >= max) {
            return min * 1000L;
        }
        return (min + random.nextInt(max - min)) * 1000L;
    }
    
    @Unique
    private boolean hasFocusEffect(Player player) {
        if (player == null) return false;
        
        var effectHolder = player.level().registryAccess()
            .registry(Registries.MOB_EFFECT)
            .flatMap(r -> r.getHolder(ResourceLocation.fromNamespaceAndPath("blink", "focusing")))
            .orElse(null);
        
        if (effectHolder == null) return false;
        return player.hasEffect(effectHolder);
    }
    
    // ========== 公开方法 ==========
    @Unique
    public void manualBlink() {
        if (isEyeClosed || isBlinking) return;
        
        isBlinking = true;
        blinkProgress = 0.0f;
        nextBlinkTime = System.currentTimeMillis();
    }
    
    @Unique
    public void startEyeClose() {
        if (!isBlinking) {
            isEyeClosed = true;
            shouldOpenEye = false;
            nextBlinkTime = System.currentTimeMillis() + getRandomBlinkInterval();
            EyeStateProvider.EyeStateHolder.isEyeClosed = true;
        }
    }
    
    @Unique
    public void stopEyeClose() {
        if (isEyeClosed) {
            shouldOpenEye = true;
            isEyeClosed = false;
            nextBlinkTime = System.currentTimeMillis() + getRandomBlinkInterval();
        }
    }
    
    @Unique
    public void setBlinkProgress(float progress) {
        this.blinkProgress = progress;
    }
    
    @Unique
    public void setBlinking(boolean blinking) {
        this.isBlinking = blinking;
    }
    
    @Unique
    public void resetEyeState() {
        this.isEyeClosed = false;
        this.isBlinking = false;
        this.blinkProgress = 0.0f;
        this.shouldOpenEye = false;
        EyeStateProvider.EyeStateHolder.isEyeClosed = false;
        nextBlinkTime = System.currentTimeMillis() + getRandomBlinkInterval();
    }
    
    // ========== EyeStateProvider 接口实现 ==========
    @Override
    @Unique
    public boolean isEyeClosed() {
        return this.isEyeClosed || this.shouldOpenEye;
    }
    
    // ========== 按键处理 ==========
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        handleBlinkKey();
    }
    
    @Unique
    private void handleBlinkKey() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        boolean isKeyDown = KeyBindings.blinkKey.isDown();
        
        if (isKeyDown && !wasKeyDown) {
            wasKeyDown = true;
            keyPressTime = System.currentTimeMillis();
            keyHandled = false;
            
        } else if (isKeyDown && wasKeyDown) {
            long holdTime = System.currentTimeMillis() - keyPressTime;
            
            if (holdTime >= LONG_PRESS_MS && !keyHandled) {
                if (!isEyeClosed && !isBlinking) {
                    startEyeClose();
                    blinkProgress = 0.0f;
                    keyHandled = true;
                } else if (isBlinking) {
                    isBlinking = false;
                    startEyeClose();
                    blinkProgress = 0.0f;
                    keyHandled = true;
                }
            }
            
        } else if (!isKeyDown && wasKeyDown) {
            long holdTime = System.currentTimeMillis() - keyPressTime;
            
            if (holdTime < LONG_PRESS_MS && !keyHandled) {
                manualBlink();
                keyHandled = true;
            } else if (holdTime >= LONG_PRESS_MS) {
                if (isEyeClosed) {
                    stopEyeClose();
                }
            }
            
            wasKeyDown = false;
            keyPressTime = 0;
            keyHandled = false;
        }
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        // ========== 检测死亡状态 ==========
        boolean isDead = player == null || player.isDeadOrDying();
        
        if (isDead && !wasDead) {
            resetEyeState();
            wasDead = true;
            drawSleepMask(guiGraphics);
            return;
        }
        
        if (isDead) {
            drawSleepMask(guiGraphics);
            return;
        }
        
        if (wasDead) {
            wasDead = false;
            resetEyeState();
        }
        
        // ========== 暂停检测 ==========
        if (mc.isPaused()) {
            isPaused = true;
            drawSleepMask(guiGraphics);
            return;
        }
        
        if (isPaused) {
            isPaused = false;
            nextBlinkTime = System.currentTimeMillis() + getRandomBlinkInterval();
        }
        
        // ========== 正常更新 ==========
        updateSleepState(player);
        
        if (sleepProgress < 0.9f) {
            boolean hasFocus = hasFocusEffect(player);
            
            if (hasFocus) {
                if (!isBlinking && !isEyeClosed && !shouldOpenEye) {
                    blinkProgress = 0.0f;
                }
                if (isEyeClosed) {
                    updateEyeClose();
                } else if (shouldOpenEye) {
                    updateEyeOpen();
                } else if (isBlinking) {
                    updateBlink();
                }
            } else if (isEyeClosed) {
                updateEyeClose();
            } else if (shouldOpenEye) {
                updateEyeOpen();
            } else if (isBlinking) {
                updateBlink();
            } else {
                updateAutoBlink();
            }
        } else {
            blinkProgress = 0.0f;
            isBlinking = false;
            shouldOpenEye = false;
        }
        
        drawSleepMask(guiGraphics);
        
        if (blinkProgress <= 0.01f && sleepProgress <= 0.01f) {
            return;
        }
        
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        
        float totalProgress = Math.max(blinkProgress, sleepProgress);
        float smoothProgress = easeInOut(totalProgress);
        float maxMaskHeight = screenHeight * 0.5f;
        float maskHeight = maxMaskHeight * smoothProgress;
        
        // 获取上眼皮占比配置
        float upperPercent = getUpperLidPercent();
        float lowerPercent = 1.0f - upperPercent;
        
        // 计算上下眼皮高度
        int upperHeight = (int)(maskHeight * 2 * upperPercent);
        int lowerHeight = (int)(maskHeight * 2 * lowerPercent);
        
        // 上眼皮：从顶部向下
        drawEyeLid(guiGraphics, screenWidth, 0, upperHeight, true);
        // 下眼皮：从底部向上
        drawEyeLid(guiGraphics, screenWidth, screenHeight - lowerHeight, lowerHeight, false);
    }
    
    @Unique
    private void updateSleepState(Player player) {
        if (player == null) return;
        
        boolean isSleeping = player.isSleeping();
        
        if (isSleeping && !wasSleeping) {
            wasSleeping = true;
        } else if (!isSleeping && wasSleeping) {
            wasSleeping = false;
            sleepProgress = 0.0f;
            return;
        }
        
        if (isSleeping) {
            sleepProgress = Math.min(1.0f, sleepProgress + SLEEP_SPEED);
        }
    }
    
    @Unique
    private void drawSleepMask(GuiGraphics guiGraphics) {
        if (sleepProgress <= 0.01f) return;
        
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int alpha = (int)(255 * sleepProgress);
        
        if (alpha > 5) {
            int color = (alpha << 24) | 0x000000;
            guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
        }
    }
    
    @Unique
    private void updateBlink() {
        long currentTime = System.currentTimeMillis();
        
        float progress = (float)(currentTime - nextBlinkTime) / BLINK_DURATION;
        progress = Math.min(progress, 1.0f);
        
        if (progress <= 0.5f) {
            blinkProgress = progress * 2.0f;
        } else {
            blinkProgress = 2.0f - progress * 2.0f;
        }
        
        if (progress >= 1.0f) {
            isBlinking = false;
            blinkProgress = 0.0f;
            if (!isEyeClosed && !shouldOpenEye) {
                nextBlinkTime = currentTime + getRandomBlinkInterval();
            }
        }
    }
    
    @Unique
    private void updateAutoBlink() {
        long currentTime = System.currentTimeMillis();
        
        if (!isEyeClosed && !shouldOpenEye && !isBlinking && currentTime >= nextBlinkTime) {
            isBlinking = true;
            nextBlinkTime = currentTime;
            updateBlink();
        }
    }
    
    @Unique
    private void updateEyeClose() {
        if (blinkProgress < 1.0f) {
            blinkProgress = Math.min(1.0f, blinkProgress + CLOSE_SPEED);
        }
    }
    
    @Unique
    private void updateEyeOpen() {
        if (blinkProgress > 0.0f) {
            blinkProgress = Math.max(0.0f, blinkProgress - OPEN_SPEED);
        } else {
            shouldOpenEye = false;
            EyeStateProvider.EyeStateHolder.isEyeClosed = false;
            if (!isBlinking) {
                nextBlinkTime = System.currentTimeMillis() + getRandomBlinkInterval();
            }
        }
    }
    
    @Unique
    private float easeInOut(float t) {
        return t < 0.5f ? 2.0f * t * t : 1.0f - (float)Math.pow(-2.0f * t + 2.0f, 2.0f) / 2.0f;
    }
    
    @Unique
    private void drawEyeLid(GuiGraphics guiGraphics, int screenWidth, int startY, int height, boolean isTop) {
        if (height <= 0) return;
        
        int yStart = Math.max(0, startY - FEATHER_SIZE);
        int yEnd = Math.min(startY + height + FEATHER_SIZE, guiGraphics.guiHeight());
        
        for (int y = yStart; y < yEnd; y++) {
            float alpha = calculateAlpha(y, startY, height, isTop);
            
            if (alpha > 0.005f) {
                int alphaByte = (int)(255 * Math.min(alpha, 1.0f));
                if (alphaByte > 2) {
                    int color = (alphaByte << 24) | 0x000000;
                    guiGraphics.fill(0, y, screenWidth, y + 1, color);
                }
            }
        }
    }
    
    @Unique
    private float calculateAlpha(int y, int startY, int height, boolean isTop) {
        if (isTop) {
            if (y >= startY && y <= startY + height) {
                return 1.0f;
            }
            if (y > startY + height && y <= startY + height + FEATHER_SIZE) {
                float t = (float)(y - startY - height) / FEATHER_SIZE;
                return 1.0f - smoothstep(t);
            }
            if (y < startY && y >= startY - FEATHER_SIZE) {
                float t = (float)(startY - y) / FEATHER_SIZE;
                return smoothstep(t);
            }
        } else {
            int bottomEdge = startY + height;
            if (y >= startY && y <= bottomEdge) {
                return 1.0f;
            }
            if (y < startY && y >= startY - FEATHER_SIZE) {
                float t = (float)(startY - y) / FEATHER_SIZE;
                return smoothstep(t);
            }
            if (y > bottomEdge && y <= bottomEdge + FEATHER_SIZE) {
                float t = (float)(y - bottomEdge) / FEATHER_SIZE;
                return 1.0f - smoothstep(t);
            }
        }
        return 0.0f;
    }
    
    @Unique
    private float smoothstep(float t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3 - 2 * t);
    }
}