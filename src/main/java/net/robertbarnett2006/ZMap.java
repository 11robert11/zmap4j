package net.robertbarnett2006;

import java.io.*;
import java.util.ArrayList;

public class ZMap {

    public static String ZMAP_BIN = (System.getenv("ZMAP_BIN") == null) ? "zmap" : System.getenv("ZMAP_BIN");
    public static String ZMAP_WRAPPER = (System.getenv("ZMAP_WRAPPER") == null) ? "" : System.getenv("ZMAP_WRAPPER");
    public static final int NEWWEST_VERSION = 2_01_01;
    public static final int OLDEST_VERSION = 00_00_00;
    private String hosts = ""; //Done
    //target-port
    private int port = 0; //Done
    //max-targets
    int maxTargets = 0;
    //max-results
    int maxResults=0;
    //max-runtime
    int maxRuntime = 0;
    //rate (pps)
    int rate = 0;
    //bandwidth
    double bandwidthBPS = 0;
    //cooldown-time
    int cooldownTime = 8;
    //seed
    boolean seedEnable = false;
    long seed = 0;
    //shards, seed must be set
    int shards = 1;
    //shard, seed must be set
    int shard = 1;
    //sender-threads
    int senderThreads = 4;
    //probes
    int probes = 1;
    //dryrun
    boolean dryRun = false;
    //source-port
    boolean sourcePortSet = false;
    String sourcePort = "";
    //source-ip
    boolean sourceIpSet = false;
    String sourceIP = "";
    //gateway-mac
    String gatewayMac = "";
    //interface
    String network_interface = "";
    //probe-module
    String probeModule = (listProbeModules().contains("tcp_synscan")) ? "tcp_synscan" : listProbeModules().get(0);
    //probe-args
    String probeArgs = "";
    //output-module
    String outputModule = "csv";
    //output-args
    String outputArgs = "";
    //output-fields
    String outputFields = "";
    //output-filter
    String outputFilter = "";
    //quiet
    boolean quiet = false;
    //summary
    boolean summary = false;
    //verbosity
    int verbosity = 3;
    //UDP payload path
    File UDPPayload;
    //UDP text
    String text = "";
    private InputStream inputStream;
    private InputStream errorStream;

    boolean vpn = false;



    //probes
    private Process process = null;


    public ZMap() throws Exception {
        checkVersion();
    }

    public ArrayList<String> getArgsList()   {
        ArrayList<String> args = new ArrayList<>();

        //ip/hostname/range
        if(!getHosts().isEmpty() || !getHosts().isBlank()) {
            args.add(getHosts());
        }

        //--target-port=port
        args.add("--target-port=" + getPort());

        //--vpn
        if(isVpn()) {
            args.add("--vpn");
        }

        //--rate
        if(getRate() > 0)   {
            args.add(String.format("--rate=%d", getRate()));
        }

        //--bandwidth=
        if(getBandwidthBPS() > 0)   {
            args.add(String.format("--bandwidth=%.0f", getBandwidthBPS()));
        }
        args.add(String.format("--cooldown-time=%d", getCooldownTime()));
        if(isSeedEnabled()) {
            args.add(String.format("--seed=%d", getSeed()));
            args.add(String.format("--shards=%d", getShards()));
            args.add(String.format("--shard=%d", getShard()));
        }
        if(getSenderThreads() > 0)  {
            args.add(String.format("--sender-threads=%d", getSenderThreads()));
        }
        args.add(String.format("--probes=%d", getProbes()));
        if(isDryRun())  {
            args.add("--dryrun");
        }
        if(!getSourcePort().isBlank())  {
            args.add("--source-port=" + getSourcePort());
        }
        if(!getSourceIP().isBlank())    {
            args.add("--source-ip=" + getSourceIP());
        }
        if(!getGatewayMac().isBlank()) {
            args.add("--gateway-mac=" + getGatewayMac());
        }
        if(!getNetwork_interface().isBlank())   {
            args.add("--interface=" + getNetwork_interface());
        }
        if(!getProbeModule().isBlank()) {
            args.add("--probe-module=" + getProbeModule());
        }
        if(!getProbeArgs().isBlank())   {
            args.add("--probe-args=" + getProbeArgs());
        }
        args.add("--output-module=" + getOutputModule());
        if(!getOutputArgs().isBlank())  {
            args.add("--output-args=" + getOutputArgs());
        }
        if(!getOutputFields().isBlank())    {
            args.add("--output-fields=" + getOutputFields());
        }
        //TODO --output-filter, wtf is that for
        if(isQuiet())   {
            args.add("--quiet");
        }
        if(isSummary()) {
            args.add("--summary");
        }
        args.add("--verbosity=" + getVerbosity());
        return args;
    }

    public ZMap run() throws IOException {
        this.stop();
        process = buildProcessBuilder().start();
        errorStream = process.getErrorStream();
        inputStream = process.getInputStream();
        return this;
    }
    public ProcessBuilder buildProcessBuilder()   {
        ArrayList<String> command = new ArrayList<>();
        if(!ZMAP_WRAPPER.isBlank()) {
            command.add(ZMAP_WRAPPER);
        }
        command.add(ZMAP_BIN);
        command.addAll(getArgsList());
        return new ProcessBuilder(command);
    }
    public ZMap stop()  {
        if(process != null && !process.isAlive())
            process.destroy();
        return this;
    }
    public ZMap kill()  {
        if(process != null && !process.isAlive()) process.destroyForcibly();
        return this;
    }


    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        //TODO implement host validation
        this.hosts = hosts;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if(!isValidPort(port))  {
            //TODO invalid port
        }
        this.port = port;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }

    public void setMaxRuntime(int maxRuntime) {
        this.maxRuntime = maxRuntime;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public double getBandwidthBPS() {
        return bandwidthBPS;
    }
    public void setBandwidth(double bandwidth, UNIT_FACTORS unitFactor)   {
        this.bandwidthBPS = bandwidth * unitFactor.getFactor();
    }

    public void setBandwidthBPS(long bandwidthBPS) {
        this.bandwidthBPS = bandwidthBPS;
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public boolean isSeedEnabled() {
        return seedEnable;
    }

    public void setSeedEnable(boolean seedEnable) {
        this.seedEnable = seedEnable;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        if(shards < getShard()) {
            //TODO handle selected shard out of proposed shard count
        }
        this.shards = shards;
    }

    public int getShard() {
        return shard;
    }

    public void setShard(int shard) {
        if(shard > getShards()) {
            //TODO handle selected shard out of bounds of shard count
        }
        this.shard = shard;
    }

    public int getSenderThreads() {
        return senderThreads;
    }

    public void setSenderThreads(int senderThreads) {
        this.senderThreads = senderThreads;
    }

    public int getProbes() {
        return probes;
    }

    public void setProbes(int probes) {
        if(probes < 0)  {
            //TODO handle negative probe count
        }
        this.probes = probes;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isSourcePortSet() {
        return sourcePortSet;
    }

    public void setSourcePortSet(boolean sourcePortSet) {
        this.sourcePortSet = sourcePortSet;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        //if (!isValidPort(sourcePort))   {
            //TODO handle
        //}
        this.sourcePort = sourcePort;
    }

    public boolean isSourceIpSet() {
        return sourceIpSet;
    }

    public void setSourceIpSet(boolean sourceIpSet) {
        this.sourceIpSet = sourceIpSet;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        //blank gatewayMax = disable flag
        this.gatewayMac = gatewayMac;
    }

    public String getNetwork_interface() {
        return network_interface;
    }

    public void setNetwork_interface(String network_interface) {
        //TODO check if interface exists
        this.network_interface = network_interface;
    }

    public String getProbeModule() {
        return probeModule;
    }

    public void setProbeModule(String probeModule) {
        try {
            if(!listProbeModules().contains(probeModule))   {
                //TODO probeModule doesn't exist
            }

            this.probeModule = probeModule;
        } catch (IOException e) {
            //Ehhhh... somethings wrong
            throw new RuntimeException(e);
        }

    }

    public String getProbeArgs() {
        return probeArgs;
    }

    public void setProbeArgs(String probeArgs) {

        this.probeArgs = probeArgs;
    }

    public String getOutputModule() {
        return outputModule;
    }

    public void setOutputModule(String outputModule) {
        //TODO check if outputModule exists in listOutputFields
        this.outputModule = outputModule;
    }

    public String getOutputArgs() {
        return outputArgs;
    }

    public void setOutputArgs(String outputArgs) {
        this.outputArgs = outputArgs;
    }

    public String getOutputFields() {
        return outputFields;
    }

    public void setOutputFields(String outputFields) {
        this.outputFields = outputFields;
    }

    public String getOutputFilter() {
        return outputFilter;
    }

    public void setOutputFilter(String outputFilter) {
        this.outputFilter = outputFilter;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public int getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(int verbosity) {
        if(! (verbosity < 0 || verbosity > 5) )   {
            //if verbosity is not between 0-5
        }
        this.verbosity = verbosity;
    }

    public File getUDPPayload() {
        return UDPPayload;
    }

    public void setUDPPayload(File UDPPayload) {
        this.UDPPayload = UDPPayload;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setErrorStream(InputStream errorStream) {
        this.errorStream = errorStream;
    }

    public boolean isVpn() {
        return vpn;
    }

    public void setVpn(boolean vpn) {
        this.vpn = vpn;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public InputStream getErrorStream() {
        return errorStream;
    }
    public static boolean isValidPort(int port) {
        return port > 65535 || port < 0;
    }

    public static String getVersion() throws IOException {
        Process versionProcess = Runtime.getRuntime().exec(ZMAP_BIN + " --version");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionProcess.getInputStream()));
        return bufferedReader.readLine();
    }
    public static String getHelp() throws IOException {
        Process versionProcess = Runtime.getRuntime().exec(ZMAP_BIN + " --help");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionProcess.getInputStream()));
        String line = bufferedReader.readLine();
        StringBuilder help = new StringBuilder();
        while (line != null)    {
            line = bufferedReader.readLine();
            help.append(line);
        }
        return help.toString();
    }

    public static ArrayList<String> listProbeModules() throws IOException {

        Process versionProcess = Runtime.getRuntime().exec(ZMAP_BIN + " --list-probe-modules");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionProcess.getInputStream()));
        String line = bufferedReader.readLine();
        ArrayList<String> modules = new ArrayList<>();
        while (line != null)    {
            modules.add(line);
            line = bufferedReader.readLine();
        }
        return modules;
    }
    public static ArrayList<OutputField> listOutputFields() throws IOException {
        Process versionProcess = Runtime.getRuntime().exec(ZMAP_BIN + " --list-output-fields");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionProcess.getInputStream()));
        String line = bufferedReader.readLine();
        ArrayList<OutputField> outputFields = new ArrayList<>();
        while (line != null)    {
            String[] parts = line.split(":");
            String module = parts[0].split(" ", 2)[0];
            String datatype = parts[0].split(" ", 2)[1].replaceAll(" ", "");
            String description = parts[1].split(" ", 2)[1];
            outputFields.add(new OutputField(module, datatype, description));
            line = bufferedReader.readLine();

        }
        return outputFields;
    }
    public enum UNIT_FACTORS {
        G(1e9),
        M(1e6),
        K(1000);
        final double factor;
        UNIT_FACTORS(double factor) {
            this.factor = factor;
        }
        public double getFactor()   {
            return factor;
        }
    }
    public static class OutputField {
        String module;
        String dataType;
        String description;
        OutputField(String module, String dataType, String description)     {
            this.module = module;
            this.dataType = dataType;
            this.description = description;
        }
        public String getModule() {
            return module;
        }

        public String getDataType() {
            return dataType;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "OutputField{" +
                    "module='" + module + '\'' +
                    ", dataType='" + dataType + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
    private static void checkVersion() throws IOException {
        String currentBuild = "";
        String[] version = getVersion().split(" ")[1].split("\\.");
        for (int i = 0; i < version.length; i++) {
            currentBuild += String.format("%02d", Integer.parseInt(version[i]));
        }
        int currentBuildNum = Integer.parseInt(currentBuild);
        if (OLDEST_VERSION > currentBuildNum) {
            throw new RuntimeException("zmap outdated");
        } else if (NEWWEST_VERSION < currentBuildNum) {
            throw new RuntimeException("zmap library outdated");
        }
    }
}
/*
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
        //TODO implements target verification
        this.hosts = targets;
        return this;
    }
    public String getTargets()  {
        return hosts;
    }














    //--bandwidth
    public double getBandwidth()   {
        return bandwidthBPS;
    }
    public void setBandwidth(long bandwidthBPS)  {
        this.bandwidthBPS = bandwidthBPS;
    }

    public void setVpn(boolean vpn) {
        this.vpn = vpn;
    }
    public void enableVPN() {
        setVpn(true);
    }
    public void disableVPN()    {
        setVpn(false);
    }
    public boolean getVpn() {
        return vpn;
    }

 */