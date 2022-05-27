package pl.saidora.core.configuration;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import pl.saidora.core.configuration.serializers.ChatProviderSerializer;

public class ConfigurationSerdes implements OkaeriSerdesPack {
    @Override
    public void register(SerdesRegistry registry) {
        registry.register(new ChatProviderSerializer());
    }
}
