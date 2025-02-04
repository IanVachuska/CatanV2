//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            new Catan();
        } else if (args.length == 3) {
            new Catan(args[0], args[1], args[2]);
        }
        else{
            printUsage();
        }
    }
    public static void printUsage(){
        System.out.println("Invalid number of arguments. Please launch with 0 arguments, or 3 arguments.");
    }
}