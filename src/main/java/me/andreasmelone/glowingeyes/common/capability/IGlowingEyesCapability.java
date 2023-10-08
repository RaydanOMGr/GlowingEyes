package me.andreasmelone.glowingeyes.common.capability;

public interface IGlowingEyesCapability {
    int getGlowingEyesType();
    boolean hasGlowingEyes();
    void setGlowingEyesType(int type);
    void setHasGlowingEyes(boolean hasGlowingEyes);

    String getRawString();
}