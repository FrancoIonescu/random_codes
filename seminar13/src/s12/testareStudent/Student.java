package s12.testareStudent;

import java.util.List;

public class Student {
    private String nume;
    private List<Integer> note;
    // metoda care returneaza media celor mai mari 2 note distincte


    public Student(String nume, List<Integer> note) {
        this.nume = nume;
        this.note = note;
    }

    public String getNume() {
        return nume;
    }

    public List<Integer> getNote() {
        return note;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setNote(List<Integer> note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Student{" +
                "nume='" + nume + '\'' +
                ", note=" + note +
                '}';
    }

    public double medieNote() throws ExceptieMedie {

        int a = 0;
        int b = 0;
        for (int i = 1; i <= note.size(); i++) {
            if (note.get(i - 1) > a) {
                a = note.get(i - 1);
            }
        }

        for (int i = 1; i <= note.size(); i++) {
            if (note.get(i - 1) > b && note.get(i - 1) != a) {
                b = note.get(i - 1);
            }
        }
        System.out.println(a + " " + b);
        return (a + b) / 2;
    }

}
