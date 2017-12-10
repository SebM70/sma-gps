package sma;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import sma.gps.fich.FichTtqv3Test;
import sma.polish.PoiMapMgrTest;

/**
 * 
 * @author marsolle
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ FichTtqv3Test.class, PoiMapMgrTest.class })
public class AllGpsSmaTestSuite {
}
