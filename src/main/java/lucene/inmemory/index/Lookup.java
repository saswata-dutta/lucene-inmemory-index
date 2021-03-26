package lucene.inmemory.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Lookup {
    private static final Analyzer ANALYZER = new EnglishAnalyzer();
    private static final String DESCRIPTION_KEY = "DESCRIPTION";
    private static final String ID_KEY = "ID";

    private final IndexSearcher dirSearcher;

    public Lookup(Path dataLoc) throws IOException {
        ByteBuffersDirectory directory = new ByteBuffersDirectory();

        try (IndexWriter directoryWriter = new IndexWriter(directory, new IndexWriterConfig(ANALYZER));
             Stream<String> lines = Files.lines(dataLoc)) {

            lines.forEach(line -> indexLine(line.trim(), directoryWriter));
        } catch (Exception e) {
            System.err.println("Error processing file : " + dataLoc);
            e.printStackTrace();
        }

        DirectoryReader indexReader = DirectoryReader.open(directory);
        dirSearcher = new IndexSearcher(indexReader);
    }

    private static void indexLine(String line, IndexWriter directoryWriter) {
        String[] data = line.split(",");

        Document doc = new Document();
        doc.add(new StoredField(ID_KEY, data[0]));
        doc.add(new TextField(DESCRIPTION_KEY, data[1], Field.Store.YES));
        try {
            directoryWriter.addDocument(doc);
        } catch (IOException e) {
            System.err.println("Failed to Index : " + line);
            e.printStackTrace();
        }

        System.out.println("Indexed : " + line);
    }

    public List<List<String>> search(String queryString, int limit) throws ParseException, IOException {
        List<List<String>> matches = new ArrayList<>();

        Query query = new QueryParser(DESCRIPTION_KEY, ANALYZER)
                .parse(queryString);

        TopDocs topDocs = dirSearcher.search(query, limit);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            String score = String.valueOf(scoreDoc.score);
            Document matchDoc = dirSearcher.doc(scoreDoc.doc);
            String id = matchDoc.get(ID_KEY);
            matches.add(Arrays.asList(score, id));
        }

        return matches;
    }
}
