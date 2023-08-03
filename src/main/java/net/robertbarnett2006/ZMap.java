package net.robertbarnett2006;

import java.io.*;

public class ZMap {

    public static String ZMAP_BIN = (System.getenv("ZMAP_BIN") == null) ? "zmap" : System.getenv("ZMAP_BIN");
    public static String ZMAP_WRAPPER = (System.getenv("ZMAP_WRAPPER") == null) ? "" : System.getenv("ZMAP_WRAPPER");

    private String hosts = "";
    private int port = 0;

    //max-targets

    //max-runtime

    //rate (pps)

    //bandwith (bps)

    //cooldown-time
    int cooldownTime = 8;

    //seed

    //shards

    //shard

    //sender-threads

    //probes
    private Process process = null;
    private InputStream inputStream = null;
    private InputStream errorStream = null;
    public ZMap() throws IOException {

    }

    public String getArgs()   {
        StringBuilder args = new StringBuilder();

        //ip/hostname/range
        if(getTargets().isEmpty() || getTargets().isBlank()) {args.append(" ").append(getTargets());}

        //--target-port=port
        args.append(" --target-port=").append(getTargetPort());
        return args.toString();
    }
    public ZMap run() throws IOException {
        this.stop();
        ProcessBuilder processBuilder = new ProcessBuilder(ZMAP_WRAPPER, ZMAP_BIN, getArgs());
        System.out.println("\t\t" + processBuilder.command());
        process = processBuilder.start();
        inputStream = process.getInputStream();
        errorStream = process.getErrorStream();
        return this;
    }
    public ZMap stop()  {
        if(process != null && !process.isAlive()) process.destroy();
        return this;
    }
    public ZMap setTargetPort(int port) {
        if (0 <= port && port <= 65535) {
            this.port = port;
        }
        return this;
    }
    public int getTargetPort()  {
        return port;
    }
    public ZMap setTargets(String targets)   {
        this.hosts = targets;
        return this;
    }
    public String getTargets()  {
        return hosts;
    }
    public static String getVersion() throws IOException {
        Process versionProcess = Runtime.getRuntime().exec(ZMAP_BIN + " --version");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionProcess.getInputStream()));
        return bufferedReader.readLine();
    }
    public InputStream getInputStream()   {

        return inputStream;
    }
    public InputStream getErrorStream() {
        return errorStream;
    }
}