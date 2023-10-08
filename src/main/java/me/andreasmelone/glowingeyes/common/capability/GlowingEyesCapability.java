package me.andreasmelone.glowingeyes.common.capability;

public class GlowingEyesCapability implements IGlowingEyesCapability {
    private String glowingEyes = "false:0";

    private boolean getHasGlowingEyesFromString() {
        String[] glowingEyesArray = this.glowingEyes.split(":");
        return Boolean.parseBoolean(glowingEyesArray[0]);
    }

    private void setHasGlowingEyesToString(boolean hasGlowingEyes) {
        String[] glowingEyesArray = this.glowingEyes.split(":");
        this.glowingEyes = hasGlowingEyes + ":" + glowingEyesArray[1];
    }

    private int getGlowingEyesTypeFromString() {
        String[] glowingEyesArray = this.glowingEyes.split(":");
        return Integer.parseInt(glowingEyesArray[1]);
    }

    private void setGlowingEyesTypeToString(int glowingEyesType) {
        String[] glowingEyesArray = this.glowingEyes.split(":");
        this.glowingEyes = glowingEyesArray[0] + ":" + glowingEyesType;
    }

    @Override
    public int getGlowingEyesType() {
        return this.getGlowingEyesTypeFromString();
    }

    @Override
    public boolean hasGlowingEyes() {
        return this.getHasGlowingEyesFromString();
    }

    @Override
    public void setGlowingEyesType(int type) {
        this.setGlowingEyesTypeToString(type);
    }

    @Override
    public void setHasGlowingEyes(boolean hasGlowingEyes) {
        this.setHasGlowingEyesToString(hasGlowingEyes);
    }

    @Override
    public String getRawString() {
        return this.glowingEyes;
    }
}
