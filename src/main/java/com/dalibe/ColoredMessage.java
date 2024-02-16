package com.dalibe;

import java.io.IOException;
import java.util.ArrayList;

public class ColoredMessage {
    public static boolean isWindows = System.getProperty("os.name").contains("Windows");

    public static void black(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[30m" + message + "\u001B[0m");
    }
    public static void red(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[31m" + message + "\u001B[0m");
    }
    public static void green(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[32m" + message + "\u001B[0m");
    }
    public static void yellow(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[33m" + message + "\u001B[0m");
    }
    public static void darkBlue(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[34m" + message + "\u001B[0m");
    }
    public static void purple(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[35m" + message + "\u001B[0m");
    }
    public static void blue(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001b[36m" + message + "\u001B[0m");
    }
    public static void white(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001b[37m" + message + "\u001B[0m");
    }
    public static void blackLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[30m" + message + "\u001B[0m\n");
    }
    public static void redLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[31m" + message + "\u001B[0m\n");
    }
    public static void greenLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[32m" + message + "\u001B[0m\n");
    }
    public static void yellowLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[33m" + message + "\u001B[0m\n");
    }
    public static void darkBlueLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[34m" + message + "\u001B[0m\n");
    }
    public static void purpleLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001B[35m" + message + "\u001B[0m\n");
    }
    public static void blueLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001b[36m" + message + "\u001B[0m\n");
    }
    public static void whiteLn(String message, boolean CompatibilityModeOff){
        if (!CompatibilityModeOff) System.out.print(message);
        else System.out.print("\u001b[37m" + message + "\u001B[0m\n");
    }

    public static void black(String message){System.out.print("\u001B[30m" + message + "\u001B[0m");}
    public static void red(String message){System.out.print("\u001B[31m" + message + "\u001B[0m");}
    public static void green(String message){System.out.print("\u001B[32m" + message + "\u001B[0m");}
    public static void yellow(String message){System.out.print("\u001B[33m" + message + "\u001B[0m");}
    public static void darkBlue(String message){System.out.print("\u001B[34m" + message + "\u001B[0m");}
    public static void purple(String message){System.out.print("\u001B[35m" + message + "\u001B[0m");}
    public static void blue(String message){System.out.print("\u001b[36m" + message + "\u001B[0m");}
    public static void white(String message){System.out.print("\u001b[37m" + message + "\u001B[0m");}
    public static void blackLn(String message){System.out.print("\u001B[30m" + message + "\u001B[0m\n");}
    public static void redLn(String message){System.out.print("\u001B[31m" + message + "\u001B[0m\n");}
    public static void greenLn(String message){System.out.print("\u001B[32m" + message + "\u001B[0m\n");}
    public static void yellowLn(String message){System.out.print("\u001B[33m" + message + "\u001B[0m\n");}
    public static void darkBlueLn(String message){System.out.print("\u001B[34m" + message + "\u001B[0m\n");}
    public static void purpleLn(String message){System.out.print("\u001B[35m" + message + "\u001B[0m\n");}
    public static void blueLn(String message){System.out.print("\u001b[36m" + message + "\u001B[0m\n");}
    public static void whiteLn(String message){System.out.print("\u001b[37m" + message + "\u001B[0m\n");}

    public static void clear(){
        ArrayList<String> command = new ArrayList<>();
        if (isWindows){
            command.add("cmd");
            command.add("/c");
            command.add("cls");
        }
        else{
            // fix: linux probably doesn't work
            command.add("/bin/bash");
            command.add("-c");
            command.add("clear");
        }
        try{
            new ProcessBuilder(command).inheritIO().start().waitFor();
            command.clear();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
