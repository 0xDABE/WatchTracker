package com.dalibe;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CfgLoader {
    public String currentConfig;
    public String stdwarn = "";
    public String stderr = "";
    private final String[] req;
    private final String[] extra;
    private List<String> reqAsList;
    private HashMap<String, String> configuration = new HashMap<>();


    public CfgLoader(String filename, String[] required, String[] extra) {
        this.currentConfig = filename;
        this.req = required;
        this.extra = extra;
    }

    private void fillConfigurationReq() {
        for (String item : req)
            configuration.put(item, null);
        reqAsList = Arrays.asList(req);
    }

    private void fillConfigurationExtra() {
        for (String item : extra)
            configuration.put(item, null);
    }


    public boolean load() {
        if (req != null) fillConfigurationReq();
        if (extra != null) fillConfigurationExtra();
        try (Scanner scanner = new Scanner(new File(currentConfig))) {
            while (scanner.hasNextLine()) {
                String full = scanner.nextLine();
                if (full.contains("//")) full = full.substring(0, full.indexOf("//")).trim();
                if (full.contains("#")) full = full.substring(0, full.indexOf("#")).trim();
                String[] line = full.split("=", 2);
                if (line.length == 1) continue;
                if (line[1].isEmpty()) {
                    configuration.put(line[0], null);
                    continue;
                }
                if (configuration.containsKey(line[0])) {
                    if (configuration.get(line[0]) != null) {
                        if (reqAsList.contains(line[0])) {
                            stderr = "Error: duplicate obligatory key's \"" + line[0] + "\" values : \"" + configuration.get(line[0]) +
                                    "\" and \"" + line[1] + "\"\n";
                            return false;
                        }
                        stdwarn += "duplicate key's \"" + line[0] + "\" values : \"" + configuration.get(line[0]) +
                                "\" and \"" + line[1] + "\"\n";
                    }
                    configuration.put(line[0], line[1]);
                }
            }
        } catch (IOException e) {
            stderr = "Error: IOException. Can't access config file.";
            return false;
        }
        return checkReq();
    }

    private boolean checkReq() {
        if (req == null) return true;
        for (String item : req) {
            if (configuration.get(item) == null) {
                stderr = "Error: config must contain \"" + item + "\"";
                return false;
            }
        }
        return true;
    }

    public String getCfgValue(String key) {
        return configuration.getOrDefault(key, null);
    }
}
