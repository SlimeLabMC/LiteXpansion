package dev.j3fftw.litexpansion.utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Timing {

    private final String name;
    private final List<Long> steps = new ArrayList<>();

    private long start;
    private long end;

    private Timing(@Nonnull String name) {
        this.name = name;
    }

    public void start() {
        this.start = System.nanoTime();
    }

    public void step() {
        this.steps.add(System.nanoTime());
    }

    public void stop() {
        this.end = System.nanoTime();
    }

    public void stop(boolean printResults) {
        stop();
        if (printResults)
            sendResults();
    }

    public void sendResults() {
        final StringBuilder sb = new StringBuilder(name + " 時序 花費: " + time(start, end));

        int idx = 1;
        for (long step : steps) {
            long before = idx == 1 ? start : steps.get(idx - 1);

            sb.append("\n  步驟 ").append(idx++).append(": ").append(time(before, step));
        }

        if (!steps.isEmpty())
            sb.append("\n  最終步: ").append(time(steps.get(steps.size() - 1), end));

        System.out.println(sb.toString());
    }

    private String time(long a, long b) {
        return (b - a) + "奈秒 (" + ((b - a) / 1e6) + "毫秒)";
    }

    public static Timing create(String name) {
        final Timing timing = new Timing(name);
        timing.start();
        return timing;
    }
}
