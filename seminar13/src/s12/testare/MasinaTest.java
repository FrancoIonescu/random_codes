package s12.testare;

import org.junit.Test;

import static org.junit.Assert.*;

public class MasinaTest {

    @org.junit.Before
    public void setUp() throws Exception {
        System.out.println("Set up");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        System.out.println("Tear down");
    }

    @Test
    public void test1() throws ExceptieNrKm {
        Masina masina = new Masina("Opel", 12000, 200, 2015);
        masina.setNrKm(300);
    }

    @Test(expected = ExceptieNrKm.class)
    public void test2() throws ExceptieNrKm {
        Masina masina = new Masina("Opel", 12000, 200, 2015);
        masina.setNrKm(-300);
    }

    @Test
    public void test3() throws ExceptieNrKm {
        Masina masina = new Masina("Opel", 12000, 200, 2015);
        masina.setNrKm(300);
        assertEquals("verificare cu nr km normal",300, masina.getNrKm());
    }

    @Test (expected = ExceptieNrKm.class)
    public void test4() throws ExceptieNrKm {
        Masina masina = new Masina("Opel", 12000, 200, 2015);
        masina.setNrKm(-300);
        assertEquals("verificare cu nr km negativ", 200, masina.getNrKm());
    }

    @Test
    public void test5() throws ExceptieNrKm {
        Masina masina = new Masina("Opel", 12000, 200, 2015);
        try {
            masina.setNrKm(-300);
            // 1 NU e bine
            //assertTrue(false);
            fail("nu arunca exceptie chiar daca am trimis nr km negativ");
        } catch (ExceptieNrKm e) {
            // 2 e bine
            assertTrue(true);
        }
        assertEquals("s-au modificat nr km pe apel set cu nr km negativ", 200, masina.getNrKm());
    }
}