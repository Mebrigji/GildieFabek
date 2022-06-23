package pl.saidora.core.configuration.serializers;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.model.impl.Kit;

public class KitSerializer implements ObjectSerializer<Kit> {

        @Override
        public boolean supports(Class<? super Kit> type) {
            return Kit.class.isAssignableFrom(type);
        }

        @Override
        public void serialize(Kit object, SerializationData data) {
            data.add("id", object.getId());
            data.add("slot", object.getSlot());
            data.add("permission", object.getPermissions());
            data.add("icon", object.getMenuItem());
            data.add("delay", object.getDelay());
            data.add("items", object.getItemStackList());
        }

        @Override
        public Kit deserialize(DeserializationData data, GenericsDeclaration generics) {
            return new Kit(data.get("id", String.class), data.getAsList("items", ItemStack.class), data.get("permission", String.class), data.get("delay", long.class), data.get("slot", int.class), data.get("icon", ItemStack.class));
        }
    }