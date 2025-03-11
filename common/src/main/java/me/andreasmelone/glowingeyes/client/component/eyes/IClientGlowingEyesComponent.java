package me.andreasmelone.glowingeyes.client.component.eyes;

public interface IClientGlowingEyesComponent {
    /**
     * Client-side-only method to send an update to the server
     * Throws an exception when ran on the wrong side, so make sure to check the side before calling this method
     */
    void sendUpdate();
}
