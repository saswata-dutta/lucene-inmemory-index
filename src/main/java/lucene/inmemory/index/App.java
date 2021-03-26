package lucene.inmemory.index;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {
        Path dataLoc = Paths.get(ClassLoader.getSystemResource("data.csv").getPath());

        Index index = new Index(dataLoc);

        System.out.println("\n");

        search(index, "blu*");
        search(index, "tab*");

        search(index, "blue green");
        search(index, "kitchen");
        search(index, "table");
        search(index, "curtains");
        search(index, "curten");
        search(index, "curtain window");
        search(index, "green table");

        search(index, "\"window curtain\"");
        search(index, "\"curtain window\"");
        search(index, "\"green curtain\"");
        search(index, "\"green table\"");

        search(index, "curten~");
        search(index, "windw~ curtn~");
        search(index, "(plant sink) AND curtaining");

    }

    private static final int LIMIT = 5;

    private static void search(Index index, String query) {
        try {
            System.out.println("Searched : " + query + " = " + index.search(query, LIMIT));
        } catch (ParseException | IOException e) {
            System.err.println("Failed Search " + query);
            e.printStackTrace();
        }
    }
}
