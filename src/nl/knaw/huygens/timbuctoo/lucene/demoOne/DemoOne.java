package nl.knaw.huygens.timbuctoo.lucene.demoOne;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.QueryBuilder;
import nl.knaw.huygens.timbuctoo.lucene.analyzer.MyAccentAnalyzer;
import nl.knaw.huygens.timbuctoo.lucene.analyzer.MySearchAnalyzer;

public class DemoOne {

	public static void main(String[] args) throws IOException {
		Analyzer mySearchAnalyzer = new MySearchAnalyzer();
		Analyzer myIndexAnalyzer = new MyAccentAnalyzer();

		// Store the index in memory:
		Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		// Directory directory = FSDirectory.open("/tmp/testindex");

		IndexWriterConfig config = new IndexWriterConfig(myIndexAnalyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);

		String text = "Agnes Grey is a book by Anne Bronte.";
		addTextToIndex(text, iwriter);

		text = "Jane Eyre is a book by Charlotte Brontë.";
		addTextToIndex(text, iwriter);
		showTokens(myIndexAnalyzer, text);

		text = "Pride and Prejudice is a book by Jane Austen.";
		addTextToIndex(text, iwriter);

		text = "Sense and Sensibility is a book by Jane Austen.";
		addTextToIndex(text, iwriter);

		iwriter.close();


		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		QueryBuilder builder = new QueryBuilder(mySearchAnalyzer);

		doQuery("book", builder, isearcher);
		doQuery("Brontë", builder, isearcher);
		doQuery("Bronte", builder, isearcher);
		doQuery("brontë", builder, isearcher);
		doQuery("Charlotte brontë", builder, isearcher);
		doQuery("bronte", builder, isearcher);
		doQuery("Austen", builder, isearcher);
		doQuery("austen", builder, isearcher);
		doQuery("Pride and Prejudice", builder, isearcher);
		doQuery("Prejudice", builder, isearcher);

		ireader.close();
		directory.close();

	}

	private static void addTextToIndex(String text, IndexWriter iwriter) throws IOException {
		Document doc = new Document();
		doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
		iwriter.addDocument(doc);
	}

	private static void doQuery(String queryString, QueryBuilder builder, IndexSearcher isearcher) throws IOException {
		Query query = builder.createPhraseQuery("fieldname", queryString);
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);
	}

	public static void showHits(ScoreDoc[] hits, IndexSearcher isearcher, Query query) throws IOException {
		System.out.println("\nquery : " + query);
		System.out.println("hits: " + hits.length);
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("fieldname"));
		}
	}
	
	public static void showTokens(Analyzer myIndexAnalyzer, String text) throws IOException {
		TokenStream stream = null;

		try {
			stream = myIndexAnalyzer.tokenStream("fieldName", text);
			CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			// print all tokens until stream is exhausted
			while (stream.incrementToken()) {
				System.out.println(termAtt.toString());
			}
			stream.end();
		} finally {
			stream.close();
		}
		
	}
}
