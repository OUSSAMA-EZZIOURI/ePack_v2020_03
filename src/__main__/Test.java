package __main__;

import entity.ProductionPlan;
import entity.WireBooking;
import entity.WireConfig;
import entity.WireStockLoc;
import helper.Helper;
import java.util.Date;

/**
 *
 * @author Oussama
 */
public class Test {

    public static void main(String[] args) {
        Helper.startSession();
        ProductionPlan pp = new ProductionPlan();

        /* 
        
        */
        pp.setHarnessPart("5802535878");
        pp.setInternalPart("26G003401");
        pp.setPlannedQty(45);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        
        pp.setHarnessPart("5802535883");
        pp.setInternalPart("26G003301");
        pp.setPlannedQty(18);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535888");
        pp.setInternalPart("26G003701");
        pp.setPlannedQty(135);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535894");
        pp.setInternalPart("26G003901");
        pp.setPlannedQty(0);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535901");
        pp.setInternalPart("26G003001");
        pp.setPlannedQty(45);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535903");
        pp.setInternalPart("26G003801");
        pp.setPlannedQty(0);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535910");
        pp.setInternalPart("26G002901");
        pp.setPlannedQty(81);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        
        pp.setHarnessPart("5802535921");
        pp.setInternalPart("26G002301");
        pp.setPlannedQty(81);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        pp.setHarnessPart("5802535966");
        pp.setInternalPart("26G004101");
        pp.setPlannedQty(45);
        pp.setCreateUser("EZZIOURI Oussama");
        pp.setWriteUser("EZZIOURI Oussama");
        pp.setCreateId(1); pp.setWriteId(1);
        pp.setCreateTime(new Date()); pp.setWriteTime(new Date());

        pp.create(pp); Helper.sess.clear();
        
        //pp.trancate(pp);
//        WireStockLoc wsl = new WireStockLoc();
//        WireBooking wb = new WireBooking();
//        WireConfig wc = new WireConfig();
    }

}
