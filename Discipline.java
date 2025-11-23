public class Discipline {
    private final String name;
    public Discipline(String name) { this.name = name; }
    public String getName() { return name; }
    @Override public String toString() { return name; }
}
