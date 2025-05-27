package s12.testareStudent;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ExceptieMedie {

        List<Integer> note = new ArrayList<>();
        note.add(10);
        note.add(7);
        note.add(9);
        note.add(8);
        Student student = new Student("Max", note);
        double medieNote = student.medieNote();
        System.out.println(medieNote);
    }
}
