package com.banmi.blink;

public interface EyeStateProvider {
    boolean isEyeClosed();
    
    static boolean isPlayerEyeClosed() {
        return EyeStateHolder.isEyeClosed;
    }
    
    class EyeStateHolder {
        public static volatile boolean isEyeClosed = false;
    }
}