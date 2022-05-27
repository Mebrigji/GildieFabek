package pl.saidora.api.functions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface ReplaceFunction {

    Object run();

    static String apply(String value, Map<String, ReplaceFunction> replaceHelperMap){
        AtomicReference<String> text = new AtomicReference<>(value);
        replaceHelperMap.forEach((key, replaceHelper) -> text.set(text.get().replace("%" + key + "%", replaceHelper.run().toString())));
        return text.get();
    }

}
