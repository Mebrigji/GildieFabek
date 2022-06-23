package pl.saidora.core.configuration.serializers;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import pl.saidora.core.model.impl.ChatProvider;

public class ChatProviderSerializer implements ObjectSerializer<ChatProvider> {

    @Override
    public boolean supports(Class<? super ChatProvider> type) {
        return ChatProvider.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ChatProvider object, SerializationData data) {
        data.add("name", object.getGroupName());
        data.add("format", object.getFormat());
    }

    @Override
    public ChatProvider deserialize(DeserializationData data, GenericsDeclaration generics) {
        return new ChatProvider(data.get("name", String.class), data.get("format", String.class));
    }
}
