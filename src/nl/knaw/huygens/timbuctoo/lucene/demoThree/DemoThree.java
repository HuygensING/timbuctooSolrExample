package nl.knaw.huygens.timbuctoo.lucene.demoThree;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import nl.knaw.huygens.timbuctoo.model.Datable;

public class DemoThree {

	public static void main(String[] args) throws IOException {
		Analyzer myIndexAnalyzer = new StandardAnalyzer();

		// Store the index in memory:
		Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		// Directory directory = FSDirectory.open("/tmp/testindex");

		IndexWriterConfig config = new IndexWriterConfig(myIndexAnalyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);

		System.out.println("indexing 'documents'");
		System.out.println("add: 1893");
		Datable date = new Datable("1893");
		addDateToIndex(date, iwriter, 1);
		System.out.println("add: 1895-10-01");
		date = new Datable("1895-10-01");
		addDateToIndex(date, iwriter, 2);
		System.out.println("add: 1896");
		date = new Datable("1896");
		addDateToIndex(date, iwriter, 3);
		System.out.println("add: 1895-07-01");
		date = new Datable("1895-07-01");
		addDateToIndex(date, iwriter, 4);

		iwriter.close();


		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		System.out.println();
		System.out.println("query: 1890-1899");
		Long lower = getFrom(new Datable("1890"));
		Long upper = getTo(new Datable("1899"));
		Query query = LongPoint.newRangeQuery("date", lower, upper);
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);

		System.out.println();
		System.out.println("query: 1895");
		Datable queryDate = new Datable("1895");
		Long queryFrom = getFrom(queryDate);
		Long queryTo = getTo(queryDate);
		query = LongPoint.newRangeQuery("date", queryFrom, queryTo);
		hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);

		System.out.println();
		System.out.println("query: 189?");
		date = new Datable("189?");
		queryFrom = getFrom(date);
		queryTo = getTo(date);
		query = LongPoint.newRangeQuery("date", queryFrom, queryTo);
		hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);

		System.out.println();
		System.out.println("query: 01-10-1895");
		date = new Datable("01-10-1895");
		queryFrom = getFrom(date);
		queryTo = getTo(date);
		query = LongPoint.newRangeQuery("date", queryFrom, queryTo);
		hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);

		System.out.println();
		System.out.println("query: 02-10-1895");
		date = new Datable("02-10-1895");
		queryFrom = getFrom(date);
		queryTo = getTo(date);
		query = LongPoint.newRangeQuery("date", queryFrom, queryTo);
		hits = isearcher.search(query, 1000).scoreDocs;
		showHits(hits, isearcher, query);

		ireader.close();
		directory.close();

	}

	private static Long getFrom(Datable datable) {
		return Long.parseLong(DateTools.dateToString(datable.getFromDate(),DateTools.Resolution.DAY));
	}

	private static Long getTo(Datable datable) {
		return Long.parseLong(DateTools.dateToString(datable.getToDate(),DateTools.Resolution.DAY));
	}

	private static void addDateToIndex(Datable date, IndexWriter iwriter, int id) throws IOException {
		Document doc = new Document();
		doc.add(new StoredField("id",id));
		doc.add(new LongPoint("date", getFrom(date)));
		doc.add(new LongPoint("date", getTo(date)));
		doc.add(new StoredField("date", date.toString()));
		iwriter.addDocument(doc);
	}

	public static void showHits(ScoreDoc[] hits, IndexSearcher isearcher, Query query) throws IOException {
		System.out.println("query : " + query);
		System.out.println("hits: " + hits.length);
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.toString());
			System.out.println(hitDoc.get("id") + " - date: " + hitDoc.get("date"));
		}
	}
	
	public static void showTokens(Analyzer myIndexAnalyzer, String text) throws IOException {
		TokenStream stream = null;

		try {
			stream = myIndexAnalyzer.tokenStream("fieldName", text);
			CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			System.out.println("tokens:");
			while (stream.incrementToken()) {
				System.out.println(termAtt.toString());
			}
			stream.end();
		} finally {
			stream.close();
		}
		
	}
}
