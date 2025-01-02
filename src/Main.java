//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String flags = "-";
        new Catan("Standard "+"S "+flags);
        new Catan("Standard "+"L "+flags);
        new Catan("Seafarers "+"S "+flags);
        new Catan("Seafarers "+"L "+flags);
        new Catan("FogIsland "+"L "+flags);
    }
}