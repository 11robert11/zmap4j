import net.robertbarnett2006.ZMap;

import java.io.IOException;
import java.util.ArrayList;


public class ZMapTest {
    public static void main(String[] args) throws Exception {
        //System.out.println(ZMap.listProbeModules());
        ArrayList<ZMap.OutputField> a = ZMap.listOutputFields();


        ZMap zMap = new ZMap();
        zMap.setVpn(true);
        //zMap.run();
        System.out.println(zMap.buildProcessBuilder().command());
        /*
        System.out.println(ZMap.ZMAP_BIN);
        System.out.println(ZMap.ZMAP_WRAPPER);
        zMap.setBandwidth(1, ZMap.UNIT_FACTORS.M);
        zMap.enableVPN();
        zMap.setTargetPort(22);
        System.out.println(zMap.getTargetPort());
        zMap.run();

        zMap.getInputStream().transferTo(System.out);
        zMap.getErrorStream().transferTo(System.err);

         */
    }
}
