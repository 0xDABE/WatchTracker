#     WatchTracker
  Allows you to track the progress of series or movies

# Requirements
- [JDK 17](https://www.oracle.com/uk/java/technologies/downloads/) or higher
- [ffmpeg](https://github.com/FFmpeg/FFmpeg) binary in OS PATH
- Terminal that supports ANSI escape chars (Konsole, Windows Terminal)
- Any media-player that can be run from terminal (must be in OS PATH as ffmpeg)

# Before start

There is no reason to use this util without .jar. It is useful only with .jar file and config file already set up. The build should always be reduced to using the .jar file that will be in the release.


# Instructions
### Build (maven)
- Clone repository
  ```shell
  git clone https://github.com/0xDABE/WatchTracker
  ```
  or download .zip archive
- Open folder with Java IDE ([IntelliJ IDEA](https://www.jetbrains.com/idea/) for example)
- Maven will set up a required jline library

### Launching from .jar
- Download release build
- Unzip all files in archive to any directory. (You must not rename config file, but you can do it with .jar file)
- Create a script, that launches .jar file:
## On Windows:
- Make sure your java binaries are in PATH (OS environment). Try
 ```cmd
java
```
If you see info about java usage, then java is in PATH, else you should put it into path (you may not do it using absolute path)
- Create a .bat file:
### If Java in PATH:
```cmd
java -jar C:\Absolute\Path\To\Your\WatchTracker.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
```
- Make sure you are using java 21 binaries from PATH with
```cmd
java --version
```
### If NOT in path:
```cmd
C:\Absolute\Path\To\Java17orAbove\Binaries\java.exe -jar C:\Absolute\Path\To\Your\WatchTracker.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
```
#
- After creating .bat file, move it to any separated directory and add this directory to PATH
- You can name .bat file as you wish, for example, **trk.bat**
- Complete installation from the Finish below
## On Linux:
- Make sure you are using Java 17:
```bash
java --version
```
- Create a bash script and name it as you wish, **trk** for example:
```bash
#!/bin/bash
java -jar /path/to/WatchTracker.jar $1 $2 $3 $4 $5 $6 $7 $8 $9
```
- Put it to `/home` directory or another directory, from which you can run scripts (`/usr/bin` for example)
- Make this script executable:
```bash
chmod u+x /path/to/script
```
- Complete installation from the Finish below
# Finish
- Try:
```cmd
trk --help
```
- Then you need to set up config file

# Config
## Obligatory settings
- playerPath

      Path, that will be used to run episodes

- trackerFileName

      File that will be created in each media folder that uses this tracker. 
      It contains an array of media files, their duration, and the time spent on the episodes.

## Extra settings (you can ignore them)
- Coefficient

      Float value that determines whether or not an episode is considered watched. 
      Recomendations are in config file. This value disregards faster viewing, only media-player process run time.
- username

      Just better visuals in Terminal, used only for it.
- episodesOnPage

      Page's size. The less auxiliary information you use, the more you can set this value.

# Usage

- open terminal in root media directory
- run
  ```bash
  trk
  ```
  in this directory
- when launched first time, it will parse all files durations and put them to a new file
- have fun


# Extra
- Command line arguments have more priority than config file settings

