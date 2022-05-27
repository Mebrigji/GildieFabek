package pl.saidora.core.model;

public interface Options {

    boolean isEnabled(Class<?> classOption);

    void registerOption(Class<?>... classOption);

    void changeOption(Class<?> classOption, boolean enable);

    boolean changeOption(Class<?> classOption);

}
