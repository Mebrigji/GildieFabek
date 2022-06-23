package pl.saidora.anti.grief;

import eu.okaeri.configs.OkaeriConfig;

public class Configuration extends OkaeriConfig {

    public String MESSAGE = "&7Postawiony przez Ciebie blok zniknie za &d%time%";

    public long TIME = 10000 * 60;

    public String TIME_PARSER = "%minute% %seconds% %millis%";

    public String TIME_PARSER_MINUTE = " min";
    public String TIME_PARSER_SECOND = " sek";
    public String TIME_PARSER_MILLISECOND = " ms";

}
