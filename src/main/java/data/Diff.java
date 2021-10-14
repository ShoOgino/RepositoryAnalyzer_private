package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Diff {
    List<Integer> linesBefore;
    List<Integer> linesAfter;

    public Diff(){
        linesBefore = new ArrayList<>();
        linesAfter = new ArrayList<>();
    }
}
