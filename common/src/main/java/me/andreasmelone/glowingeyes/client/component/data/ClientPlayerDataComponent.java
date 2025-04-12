package me.andreasmelone.glowingeyes.client.component.data;

public class ClientPlayerDataComponent {
    private static IClientPlayerDataComponent instance;

    private ClientPlayerDataComponent() {
    }

    public static void sendRequest() {
        instance.sendRequest();
    }

    /**
     * Sets whether the mod is present on the server or not
     */
    public static void setIsModOnServer(boolean isModOnServer) {
        instance.setIsModOnServer(isModOnServer);
    }

    /**
     * Gets whether the mod is present on the server or not
     * @return Whether the mod is on the server or no
     */
    public static boolean isModOnServer() {
        return instance.isModOnServer();
    }

    public static void setImplementation(IClientPlayerDataComponent instance) {
        if(ClientPlayerDataComponent.instance != null) {
            throw new IllegalStateException("ClientPlayerDataComponent implementation is already set");
        }
        ClientPlayerDataComponent.instance = instance;
    }
}
