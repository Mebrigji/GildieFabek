package pl.saidora.api.helpers;

import pl.saidora.api.functions.BiReplaceFunction;

import java.util.*;
import java.util.stream.Collectors;

public class StringHelper {

    public static String join(String[] args, String split, int start, int end){
        if(start > end) throw new IndexOutOfBoundsException();
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if(i >= start && i < end)
                if(first) {
                    builder.append(args[i]);
                    first = false;
                }
                else builder.append(split).append(args[i]);
        }
        return builder.toString();
    }

    public static char getFirstCharacter(String text){
        return text.charAt(0);
    }

    public static <K, V> String join(BiReplaceFunction<K, V> biReplaceFunction, CharSequence delimiter, Map<K, V> objectMap){
        StringBuilder builder = new StringBuilder();
        objectMap.forEach((key, value) -> {
            if(builder.length() != 0) builder.append(delimiter);
            builder.append(biReplaceFunction.accept(key, value).toString());
        });
        return builder.toString();
    }

    public static String setFirstCharacterToUpperCase(String text){
        StringBuilder builder = new StringBuilder();
        String character = String.valueOf(getFirstCharacter(text));
        return builder.append(text.replaceFirst(character, character.toUpperCase())).toString();
    }

    public static List<String> startWith(Object[] arguments, String text, boolean ignoreEmpty, int limiter){
        return startWith(Arrays.asList(arguments), text, ignoreEmpty, limiter);
    }

    public static List<String> startWith(List<Object> arguments, String text, boolean ignoreEmpty, int limiter){
        if(!ignoreEmpty && arguments.isEmpty() && text.isEmpty()) return new ArrayList<>();
        if(limiter == -1) return arguments.stream()
                .map(Objects::toString)
                .filter(s -> s.regionMatches(true, 0, text, 0, text.length()))
                .collect(Collectors.toList());
        return subList(0, limiter, arguments.stream()
                .map(Objects::toString)
                .filter(s -> s.regionMatches(true, 0, text, 0, text.length()))
                .collect(Collectors.toList()));
    }

    public static <T> List<T> subList(int from, int to, List<T> list){
        if(from > to) from = 0;
        if(to < from) to = from + 1;
        if(to > list.size()) to = list.size();
        return list.subList(from, to);
    }

    public static String setMaxSize(String message, int maxSize) {
        return message.substring(0, Math.max(0, Math.min(message.length(), maxSize)));
    }
}
