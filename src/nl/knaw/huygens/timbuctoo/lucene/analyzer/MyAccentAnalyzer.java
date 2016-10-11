package nl.knaw.huygens.timbuctoo.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class MyAccentAnalyzer extends Analyzer {
	
	public MyAccentAnalyzer() {
		super();
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer source = new StandardTokenizer();
		TokenStream result = new StandardFilter(source);
		result = new MyAccentFilter(result);
		return new TokenStreamComponents(source, result);
	}

}
