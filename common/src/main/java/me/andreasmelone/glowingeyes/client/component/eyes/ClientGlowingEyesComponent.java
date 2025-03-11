package me.andreasmelone.glowingeyes.client.component.eyes;

public class ClientGlowingEyesComponent {
    private static IClientGlowingEyesComponent instance;

    private ClientGlowingEyesComponent() {
    }

    /**
     * Client-side-only method to send an update to the server
     * Throws an exception when ran on the wrong side, so make sure to check the side before calling this method
     */
    public static void sendUpdate() {
        instance.sendUpdate();
    }

    public static void setImplementation(IClientGlowingEyesComponent instance) {
        if(ClientGlowingEyesComponent.instance != null) {
            throw new IllegalStateException("ClientGlowingEyesComponent implementation is already set");
        }
        ClientGlowingEyesComponent.instance = instance;
    }
}
