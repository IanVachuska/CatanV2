package com.mycatan;
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            new Catan("Fog L -d");
        }
        else if (args.length == 1) {
            try {
                new Catan(args[0],"l","-");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("[type]");
            }
        }
        else if (args.length == 2) {
            try {
                new Catan(args[0], args[1],"-");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("[type] [size]");
            }
        }
        else if (args.length == 3) {
            try {
                new Catan(args[0], args[1], args[2]);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("[type] [size] [-RD]");
            }
        }
        else{
            printUsage();
        }
    }
    public static void printUsage(){
        System.out.println("Invalid number of arguments. Please launch with 0 to 3 arguments.");
    }
}