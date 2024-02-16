import com.dalibe.CfgLoader;
import com.dalibe.Nargs;

import java.io.File;

public class Main {
    public static String jarPath = getJarPath();
    public static String configFileName = "trackerConfig.conf";

    public static String argsRegex = "<-help><h><u>:<U>:<-username>:" +
            "<c>:<C>:<-coefficient>:<o>:<-output>:<-trackerFileName>:" +
            "<p>:<-player>:<e><-extra><i><-info><s>:<-pageSize>:";


    public static void main(String[] args) {
        loadCfg(configFileName);
        loadArgs(args, argsRegex);

        Menu menu = new Menu();
        menu.run();
    }




    private static void loadArgs(String[] args, String regex){
        Nargs nargs = new Nargs(args, regex);
        String opt;
        while ((opt = nargs.getOpt()) != null){
            switch (opt){
                case "-help", "h" -> {
                    Menu.help();
                    System.exit(0);
                }
                case "u", "U", "-username" -> Menu.userName = nargs.optionArg;
                case "c", "C", "-coefficient" -> Menu.C = Float.parseFloat(nargs.optionArg);
                case "o", "-output", "-trackerFileName" -> Menu.trackerFileName = nargs.optionArg;
                case "p", "-player" -> Menu.playerRunWithPath = nargs.optionArg;
                case "e", "-extra", "i", "-info" -> Menu.showExtraInfo = true;
                case "s", "-pageSize" -> Menu.episodesOnPage = Integer.parseInt(nargs.optionArg);
            }
        }
    }


    private static void loadCfg(String filename) {
        CfgLoader cfgLoader = new CfgLoader(jarPath + File.separator + filename,
                new String[]{"playerPath", "trackerFileName"},
                new String[]{"Coefficient", "username", "episodesOnPage"});
        if (cfgLoader.load()) {
            Menu.playerRunWithPath = cfgLoader.getCfgValue("playerPath");
            Menu.trackerFileName = cfgLoader.getCfgValue("trackerFileName");
            String temp = cfgLoader.getCfgValue("username");
            if (temp != null) Menu.userName = temp;
            temp = cfgLoader.getCfgValue("Coefficient");
            if (temp != null) Menu.C = Float.parseFloat(temp);
            Menu.episodesOnPage = Integer.parseInt(cfgLoader.getCfgValue("episodesOnPage"));
        }
    }


    private static String getJarPath() {
        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String absoluteJarPath = new File(jarPath).getCanonicalPath();
            File jarFile = new File(absoluteJarPath);
            return jarFile.getParent();
        } catch (Exception ignored) {}
        return null;
    }
}
