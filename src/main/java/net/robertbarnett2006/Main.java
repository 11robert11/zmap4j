package net.robertbarnett2006;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ZMap k = new ZMap();
        System.out.println(        new ProcessBuilder("aaa").command());
        System.out.println(ZMap.getVersion());
        //fk.setTargets("mc.robertbarnett2006.net");
        k.setTargetPort(25565);
        System.out.println(k.run());
        k.getErrorStream().transferTo(System.err);
        k.getInputStream().transferTo(System.out);
    }
}