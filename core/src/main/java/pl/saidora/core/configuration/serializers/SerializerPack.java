package pl.saidora.core.configuration.serializers;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;

public class SerializerPack implements OkaeriSerdesPack {

    @Override
    public void register(SerdesRegistry registry) {
        registry.register(new KitSerializer());
        registry.register(new InventorySerializer());
        registry.register(new ChatProviderSerializer());
    }
}