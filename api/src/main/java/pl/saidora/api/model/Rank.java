package pl.saidora.api.model;

public interface Rank {

    int getPoints();

    int getKills();

    int getDeaths();

    int getKDR();

    void setPoints(int points);

    void setKills(int kills);

    void setDeaths(int deaths);

    int getPosition();

}
