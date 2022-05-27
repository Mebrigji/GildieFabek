package pl.saidora.core.model.impl.guild;

public class War {

    private Guild attacker, defenders;

    private int attacker_points, defenders_points;
    private long start, end;

    public Guild getAttacker() {
        return attacker;
    }

    public void setAttacker(Guild attacker) {
        this.attacker = attacker;
    }

    public Guild getDefenders() {
        return defenders;
    }

    public void setDefenders(Guild defenders) {
        this.defenders = defenders;
    }

    public int getAttacker_points() {
        return attacker_points;
    }

    public void setAttacker_points(int attacker_points) {
        this.attacker_points = attacker_points;
    }

    public int getDefenders_points() {
        return defenders_points;
    }

    public void setDefenders_points(int defenders_points) {
        this.defenders_points = defenders_points;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
