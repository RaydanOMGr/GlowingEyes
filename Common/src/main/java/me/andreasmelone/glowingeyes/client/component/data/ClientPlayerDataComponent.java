package me.andreasmelone.glowingeyes.client.component.data;

public class ClientPlayerDataComponent {
    private static IClientPlayerDataComponent instance;

    private ClientPlayerDataComponent() {
    }

    public static void sendRequest() {
        instance.sendRequest();
    }

    public static void setImplementation(IClientPlayerDataComponent instance) {
        if(ClientPlayerDataComponent.instance != null) {
            throw new IllegalStateException("ClientPlayerDataComponent implementation is already set");
        }
        ClientPlayerDataComponent.instance = instance;
    }
}
