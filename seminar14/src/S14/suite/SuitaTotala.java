package S14.suite;

import S14.teste.ConstructorMasinaTest;
import S14.teste.SetAniFabricatieTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({ConstructorMasinaTest.class, SetAniFabricatieTest.class})
public class SuitaTotala {

}