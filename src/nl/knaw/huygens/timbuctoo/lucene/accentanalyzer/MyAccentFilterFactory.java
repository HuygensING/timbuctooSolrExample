package nl.knaw.huygens.timbuctoo.lucene.analyzer;

import java.util.Map;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class MyAccentFilterFactory extends TokenFilterFactory {

	public MyAccentFilterFactory(Map<String, String> args) {
		super(args);
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown parameters: " + args);
		}
	}

	@Override
	public TokenFilter create(TokenStream ts) {
		return new MyAccentFilter(ts);
	}

}
