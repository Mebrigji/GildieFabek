package pl.saidora.core.commands.system;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    String name();

    String usage();

    String permission();

    String description() default "empty description";

    String[] aliases() default "";

    ExecutorType executors() default ExecutorType.ALL;

}