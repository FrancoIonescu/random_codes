package s12.testareStudent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import s12.testare.ExceptieNrKm;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StudentMedieTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws ExceptieMedie {
        List<Integer> note = new ArrayList<>();
        note.add(10);
        // Lista este goala
        Student student = new Student("Max", note);
        if (student.getNote().size() < 1) {
            fail("Lista este goala");
            throw new ExceptieMedie("Lista este goala");
        }
    }

    @Test
    public void test2() throws ExceptieMedie {
        List<Integer> note = new ArrayList<>();
        note.add(10);
        Student student = new Student("Max", note);
        for (int nota: student.getNote()) {
            if (nota > 10 || nota < 1) {
                fail("Notele nu sunt in intervalul 1-10");
                throw new ExceptieMedie("Notele nu sunt in intervalul 1-10");
            }
        }
    }
}