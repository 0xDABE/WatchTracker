import com.dalibe.ColoredMessage;
import com.dalibe.Times;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Menu implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static String playerRunWithPath = "";
    public static String userName = "";
    public static String trackerFileName = "zzzTracker.dat";
    public static float C = 0.65f;
    public static boolean showExtraInfo = true;
    public static boolean wrongCommand = false;
    public static boolean showHelp = true;
    public static int episodesOnPage = 6;

    private static int currentItem = 0;
    private static int currentPage = 0;

    private static final Scanner scanner = new Scanner(System.in);

    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private long totalDuration = 0;

    public void run() {
        ColoredMessage.clear();
        try (Terminal terminal = TerminalBuilder.builder()
                .jna(true)
                .build()) {
            terminal.enterRawMode();
            while (true) {
                if (loadFromFile(trackerFileName) == -1) parseFiles();
                else if (!quickParse()) parseFiles();
                if (!wrongCommand) show();
                ColoredMessage.yellow("\n" + getDiskLatency());
                ColoredMessage.purple("*" + new File(System.getProperty("user.dir")).getName());
                ColoredMessage.redLn("@" + userName);
                if (showHelp) helpIn();

                int key;
                if ((key = terminal.reader().read()) != 'q') {
                    switch (key) {
                        case 65 -> { // up
                            if (currentItem - 1 < 0) {
                                currentItem = mediaFiles.size() - 1;
                                currentPage = currentItem / episodesOnPage;
                            } else {
                                currentItem--;
                                currentPage = currentItem / episodesOnPage;
                            }

                        }
                        case 68 -> previousPage(); // left
                        case 66 -> { // down
                            if (currentItem + 1 >= mediaFiles.size()) {
                                currentItem = 0;
                                currentPage = 0;
                            } else {
                                currentItem++;
                                currentPage = currentItem / episodesOnPage;
                            }
                        }
                        case 67 -> nextPage(); // right
                        case (int) 'w' -> watch(currentItem + 1);
                        case (int) 'h' -> showHelp = !showHelp;
                        case (int) 'n' -> next(true);
                        case (int) 'i' -> showExtraInfo = !showExtraInfo;
                        case (int) 'a' -> setNotWatched(new String[]{"a", String.valueOf(currentItem + 1)});
                        case (int) 'd' -> setDone(new String[]{"d", String.valueOf(currentItem + 1)});
                        case (int) 's' -> setStarted(new String[]{"s", String.valueOf(currentItem + 1)});

                        case (int) 'c' -> inputCommand();
                    }
                } else System.exit(0);
                saveToFile(trackerFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPage() {
        int totalPages = (mediaFiles.size() + episodesOnPage - 1) / episodesOnPage;
        currentPage = (currentPage + 1) % totalPages;
        currentItem = currentPage * episodesOnPage;
        currentItem = Math.min(currentItem, mediaFiles.size() - 1);
    }

    private void previousPage() {
        int totalPages = (mediaFiles.size() + episodesOnPage - 1) / episodesOnPage;
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        currentItem = currentPage * episodesOnPage;
    }


    private void show() {
        wrongCommand = false;
        ColoredMessage.clear();
        if (showExtraInfo) {
            ColoredMessage.darkBlueLn("Watched after: " + String.format("%.1f", C * 100) + "%");
            ColoredMessage.darkBlueLn("Total duration: " + Times.getTimeMillis(totalDuration * 1000));
            ColoredMessage.darkBlueLn("Files: " + mediaFiles.size());
        }
        int len = mediaFiles.size();
        int start = currentPage * episodesOnPage;
        int end = Math.min(start + episodesOnPage, len);
        ColoredMessage.purpleLn("                       Page " + (currentPage + 1) + "/" + (len + episodesOnPage - 1) / episodesOnPage);
        for (int i = start; i < end; i++) {
            System.out.print((i + 1) + " -> ");
            if (showExtraInfo) {
                if (currentItem == i)
                    ColoredMessage.blueLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")) +
                            "   " + String.format("%.1f", ((float) mediaFiles.get(i).spent / (float) mediaFiles.get(i).duration) * 100) + "%");
                else if (mediaFiles.get(i).watched)
                    ColoredMessage.greenLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")) +
                            "   " + String.format("%.1f", ((float) mediaFiles.get(i).spent / (float) mediaFiles.get(i).duration) * 100) + "%");
                else if (mediaFiles.get(i).spent > 0)
                    ColoredMessage.yellowLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")) +
                            "   " + String.format("%.1f", ((float) mediaFiles.get(i).spent / (float) mediaFiles.get(i).duration) * 100) + "%");
                else ColoredMessage.redLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")));
            } else {
                if (currentItem == i)
                    ColoredMessage.blueLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")));
                else if (mediaFiles.get(i).watched)
                    ColoredMessage.greenLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")));
                else if (mediaFiles.get(i).spent > 0)
                    ColoredMessage.yellowLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")));
                else ColoredMessage.redLn(mediaFiles.get(i).name.substring(0, mediaFiles.get(i).name.lastIndexOf(".")));
            }
        }
    }

    // todo: add subs and sound files

    private void parseFiles() {
        ColoredMessage.clear();
        ColoredMessage.blueLn("Parsing files...");
        this.mediaFiles = new ArrayList<>();
        File[] files = new File(System.getProperty("user.dir")).listFiles((dir, name) -> name.endsWith(".mkv") ||
                name.endsWith(".mp4"));

        if (files == null || files.length == 0) {
            ColoredMessage.redLn("Empty dir");
            System.exit(-1);
        }

        Arrays.sort(files);

        for (File file : files) {
            int duration = getDuration(file.getName());
            this.mediaFiles.add(new MediaFile(file.getName(), duration));
            totalDuration += duration;
        }
    }

    private boolean quickParse() {
        File[] files = new File(System.getProperty("user.dir")).listFiles((dir, name) -> name.endsWith(".mkv") ||
                name.endsWith(".mp4"));

        if (files == null || files.length == 0) {
            ColoredMessage.redLn("Empty dir");
            System.exit(-1);
        }

        return files.length == mediaFiles.size();
    }



    private void inputCommand() {
        String line = "";
        ColoredMessage.clear();
        show();
        System.out.println();
        showAvailableCommands();
        ColoredMessage.red("\n>> ");
        if (scanner.hasNextLine()) line = scanner.nextLine().trim();
        String comm = line.split(" ")[0];
        switch (comm) {
            case "g", "go" -> next(false);
            case "d", "done" -> setDone(line.split(" "));
            case "r", "reset", "red" -> setNotWatched(line.split(" "));
            case "y", "yellow" -> setStarted(line.split(" "));
            case "e", "exit", "q", "quit" -> System.exit(0);
        }
        wrongCommand = false;
    }


    private void showAvailableCommands() {
        System.out.println("""
                Available commands:
                    w (or watch) <episode> - watch specific episode on current directory
                    n (or next) - watch next(first) unwatched episode (red)
                    g (or go) - watch unstoppable all unwatched episodes (red) from first unwatched
                    
                    d (or done) <episode> - set specific episode done (green)
                    r (or red, reset) <episode> - set specific episode unwatched (red)
                    s (or started, y, yellow) <episode> - set specific episode started watching (yellow)
                             
                    e (or exit) - exit command, terminates current trk process
                                
                """);
    }


    private void setDone(String[] eps) {
        wrongCommand = false;
        try {
            if (eps.length == 2) mediaFiles.get(Integer.parseInt(eps[1]) - 1).watched = true;
            else if (eps.length == 3) {
                int first = Integer.parseInt(eps[1]), second = Integer.parseInt(eps[2]);
                for (int i = first; i <= second && i > 0 && i <= mediaFiles.size(); i++)
                    mediaFiles.get(i - 1).watched = true;
            } else new OnWrong("Wrong \"done\" usage. Use number to set any episode done");
        } catch (NumberFormatException ignored) {
            new OnWrong("Wrong \"done\" usage. Use number to set any episode done");
        }

    }

    private void setNotWatched(String[] eps) {
        wrongCommand = false;
        try {
            if (eps.length == 2) {
                mediaFiles.get(Integer.parseInt(eps[1]) - 1).watched = false;
                mediaFiles.get(Integer.parseInt(eps[1]) - 1).spent = 0;
            } else if (eps.length == 3) {
                int first = Integer.parseInt(eps[1]), second = Integer.parseInt(eps[2]);
                for (int i = first; i <= second && i > 0 && i <= mediaFiles.size(); i++) {
                    mediaFiles.get(i - 1).spent = 0;
                    mediaFiles.get(i - 1).watched = false;
                }
            } else new OnWrong("Wrong \"red\" usage. Use number to set any episode done");
        } catch (NumberFormatException ignored) {
            new OnWrong("Wrong \"red\" usage. Use number to set any episode done");
        }
    }

    private void setStarted(String[] eps) {
        wrongCommand = false;
        try {
            if (eps.length == 2) {
                mediaFiles.get(Integer.parseInt(eps[1]) - 1).spent = 1;
                mediaFiles.get(Integer.parseInt(eps[1]) - 1).watched = false;
            } else if (eps.length == 3) {
                int first = Integer.parseInt(eps[1]), second = Integer.parseInt(eps[2]);
                for (int i = first; i <= second && i > 0 && i <= mediaFiles.size(); i++) {
                    mediaFiles.get(i - 1).spent = 1;
                    mediaFiles.get(i - 1).watched = false;
                }
            } else new OnWrong("Wrong \"yellow\" usage. Use number to set any episode done");
        } catch (NumberFormatException ignored) {
            new OnWrong("Wrong \"yellow\" usage. Use number to set any episode done");
        }
    }

    private void saveToFile(String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(gzipOut)) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadFromFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(gzipIn)) {
            Menu menu = (Menu) ois.readObject();
            this.mediaFiles = menu.mediaFiles;
            this.totalDuration = menu.totalDuration;
            return 0;
        } catch (IOException | ClassNotFoundException e) {
            ColoredMessage.yellow("Tracker object file not loaded. Creating new...\n");
        }
        return -1;
    }

    private void next(boolean stopOnFirst) {
        int len = mediaFiles.size();
        for (int i = 0; i < len; i++)
            if (!mediaFiles.get(i).watched) {
                watch(i + 1);
                if (stopOnFirst) return;
            }
    }

    private void showCurrentWatching(int ep) {
        wrongCommand = false;
        if (showExtraInfo) ColoredMessage.darkBlueLn("Watched after: " + C * 100 + "%");
        ColoredMessage.clear();
        ColoredMessage.purple((ep) + " -> ");
        ColoredMessage.white(mediaFiles.get(ep - 1).name.substring(0, mediaFiles.get(ep - 1).name.lastIndexOf(".")));
        ColoredMessage.purpleLn("  -> NOW PLAYING");

    }


    private void watch(int ep) {
        wrongCommand = false;
        String[] command = new String[2];
        command[0] = playerRunWithPath;
        command[1] = mediaFiles.get(ep - 1).name;
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process process = pb.start();
            long start = System.currentTimeMillis();
            showCurrentWatching(ep);
            process.waitFor();
            mediaFiles.get(ep - 1).spent += (int) ((System.currentTimeMillis() - start) / 1_000);
            if (mediaFiles.get(ep - 1).duration * C <= mediaFiles.get(ep - 1).spent)
                mediaFiles.get(ep - 1).watched = true;
        } catch (IOException | InterruptedException e) {
            ColoredMessage.redLn("Error creating media player process");
            System.exit(-1);
        }
    }

    private static String getDiskLatency() {
        long start = System.nanoTime();
        File file = new File("tempFile.tmp");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(1);
            fileWriter.close();
            file.delete();
            return Times.getTimeNano(System.nanoTime() - start);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    private static void helpIn() {
        ColoredMessage.blueLn("""
                                            
                                            Control hints:
                ↑, ↓ - change episode                   w - watch current episode
                ←, → - change pages                       n - watch next episode
                 
                i - switch extra info
                h - switch control hints (this)
                c - use built-in commands
                                
                a - make current episode Awaiting watching (red). RESET spent time
                s - make current episode Started watching (yellow). SET spent time to 1 second
                d - make current episode Done (green). NOT change spent time
                                
                q - exit application""");
    }

    public static void help() {
        System.out.println("""
                                 
                                                ******************
                                                *      HELP      *
                                                ******************
                            
                Description:
                    "trk" is the script (may be binary or kinda bash or bat, but must be in PATH) that uses this java utility to recursively send files from specified folders or just a files with TelegramAPI
                    
                Usage:
                    trk <options>
                    
                Options:
                    -p, --player <Path>: Set media player's path to run with it
                    -h, --help: Help flag. Show this message.
                    -u, -U, --username <NAME>: Use custom username in trk-in command prompts
                    -c, -C, --coefficient <0-1_float_value>: Set time limit to consider it is done
                    -o, --output, --trackerFileName <FileName>: Set custom filename for trackerFile
                    -e , -i, --extra, --info : Extra info flag. Shows extra info
                    
                   
                    
                                                *******************
                                                *  trk by 0xDABE  *
                                                *******************""");
    }


    private static int getDuration(String filename) {
        int timeInSec = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", filename);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration")) {
                    break;
                }
            }
            if (line == null) {
                System.err.println("Not a video: " + filename);
                System.exit(-1);
            }
            String hours, minutes, seconds;
            hours = line.split("[,.:]")[1].trim();
            minutes = line.split("[,.:]")[2].trim();
            seconds = line.split("[,.:]")[3].trim();
            timeInSec = Integer.parseInt(hours) * 60 * 60 +
                    Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);

            reader.close();
        } catch (IOException e) {
            ColoredMessage.redLn("Error creating ffmpeg process. Check if it is in bin path.");
            System.exit(-1);
        }
        return timeInSec;
    }


}
