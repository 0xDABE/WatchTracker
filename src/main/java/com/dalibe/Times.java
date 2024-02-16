package com.dalibe;

public class Times {
    public static void main(String[] args) {
        long start = System.nanoTime();
        try {
            Thread.sleep(0);
        }catch (InterruptedException ignored){}
        long finish = System.nanoTime() - start;
        System.out.println(getTimeNano(finish));

    }

    public static String getTimeNano(long nanos){
        if (nanos < 1_000_000) return String.format("%.1f",(float) nanos / 1_000) + "mcs";
        if (nanos < 1_000_000_000) return String.format("%.1f",(float) nanos / 1_000_000) + "ms";
        else return String.format("%.1f",(float) nanos / 1_000_000_000)  + "s";
    }

    public static String getTimeMillis(long millis){
        if (millis < 1_000) return millis + "ms";
        if (millis < 60_000) return String.format("%.1f",(float) millis / 1_000)  + "s";
        if (millis < 3_600_000) return String.format("%.1f",(float) millis / 60_000)  + "m";
        if (millis < 86_400_000) return String.format("%.1f",(float) millis / 3_600_000)  + "h";
        else return String.format("%.1f",(float) millis / 86_400_000)  + "d";
    }

}
