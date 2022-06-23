package pl.saidora.api.helpers;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SystemHelper {

    public static double getCpuUsage(){
        long usage = 0;
        ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
        long full = 0L;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            full += tmxb.getThreadCpuTime(thread.getId());
            if (tmxb.getThreadCpuTime(thread.getId()) > 0L) {
                long l = tmxb.getThreadCpuTime(thread.getId()) * 100L / full;
                if ((double)l > 0.0D) {
                    usage += l;
                }
            }
        }
        return usage / 100.0;
    }

    public static long getFreeRam(){
        return Runtime.getRuntime().freeMemory() / 1024 / 1024;
    }

    public static long getTotalRam(){
        return Runtime.getRuntime().totalMemory() / 1024 / 1024;
    }

    public static long getMaxRam(){
        return Runtime.getRuntime().maxMemory() / 1024 / 1024;
    }

    public static long getUsageRam(){
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    }

}
