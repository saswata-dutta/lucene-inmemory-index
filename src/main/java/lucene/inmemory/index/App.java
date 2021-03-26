package lucene.inmemory.index;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {
        Path dataLoc = Paths.get(ClassLoader.getSystemResource("data.csv").getPath());

        Lookup lookup = new Lookup(dataLoc);

        search(lookup, "blue OR green");
        search(lookup, "blue");
        search(lookup, "\"window curtain\"");
    }

    private static void search(Lookup lookup, String query) {
        try {
            System.out.println(query + " = " + lookup.search(query, 5));
        } catch (ParseException | IOException e) {
            System.err.println("Failed Search " + query);
            e.printStackTrace();
        }
    }
}
