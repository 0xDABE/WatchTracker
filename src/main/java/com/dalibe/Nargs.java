package com.dalibe;

import java.util.HashMap;

public class Nargs {    // Usage:  "<optionWithArg>:<optionW\Arg>"
    public String[] args;
    public String regex;
    public String stdErr = "";

    public String optionArg = null;
    public HashMap<String, Boolean> options = new HashMap<>();  // <Option, IsContainOptArg>
    public HashMap<String, Boolean> optionsBackup;  // <Option, IsContainOptArg>

    public Nargs(String[] args, String regex) {
        this.args = args;
        this.regex = regex;
        if (regex.length() < 3) {
            System.err.println("Error. Less than 3 len");
            System.exit(-1);
        }
        String[] parts = regex.substring(1).split("[<>]");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            if (i + 1 < parts.length) {
                if (parts[i + 1].equals(":")) {
                    options.put(parts[i], true);
                    i++;
                } else options.put(parts[i], false);
            } else options.put(parts[i], false);
        }
        optionsBackup = new HashMap<>(options);
    }

    public String getOpt() {
        optionArg = null;
        for (String option : options.keySet()) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-" + option)) {
                    if (options.get(option)) { // If contains OptArg
                        if (i + 1 < args.length) {
                            optionArg = args[i + 1];
                            for (String checkOption : optionsBackup.keySet()) {
                                if (args[i + 1].equals("-" + option)) {
                                    stdErr = stdErr + "Warning:\n   " +
                                            "\"-" + option + "\" requires arg, but \"-" +
                                            checkOption + "\" given next\n";
                                    break;
                                }
                            }
                            String[] newArray = new String[args.length - 1];
                            System.arraycopy(args, 0, newArray, 0, i);
                            System.arraycopy(args, i + 1, newArray, i, args.length - i - 1);
                            args = newArray;

                            newArray = new String[args.length - 1];
                            System.arraycopy(args, 0, newArray, 0, i);
                            System.arraycopy(args, i + 1, newArray, i, args.length - i - 1);
                            args = newArray;

                            options.remove(option);
                            return option;
                        } else {
                            System.err.println("Error: option \"-" + option + "\" requires argument");
                            System.exit(-1);
                        }
                    } else { // If not
                        if (options.get(option)) {
                            if (args[i + 1].equals("-" + option)) {
                                stdErr = stdErr + "Warning:\n   " +
                                        "\"-" + option + "\" requires arg, but it given last\n";
                            }
                        }
                        String[] newArray = new String[args.length - 1];
                        System.arraycopy(args, 0, newArray, 0, i);
                        System.arraycopy(args, i + 1, newArray, i, args.length - i - 1);
                        args = newArray;
                        optionArg = null;
                        options.remove(option);
                        return option;
                    }
                }
            }
        }
        return null;
    }


}
