package nl.knaw.huygens.timbuctoo.lucene.analyzer;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

public class MyAccentFilter extends TokenFilter {

	private CharTermAttribute termAtt;
	private Stack<String> synonymStack;
	private PositionIncrementAttribute posIncrAtt;
	private AttributeSource.State current;
	private TypeAttribute typeAtt;
	private String TYPE_SYNONYM = "SYNONYM";

	public MyAccentFilter(TokenStream input) {
		super(input);
		synonymStack = new Stack<String>();
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
		this.typeAtt = addAttribute(TypeAttribute.class);
		this.current = captureState();
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (synonymStack.size() > 0) {
			String syn = synonymStack.pop();
			restoreState(current);
			typeAtt.setType(TYPE_SYNONYM);
			termAtt.copyBuffer(syn.toCharArray(), 0, syn.length());
			posIncrAtt.setPositionIncrement(0);
			return true;
		}
		if (!input.incrementToken()) {
			return false;
		}
		if (addAliasesToStack()) {
			current = captureState();
		}
		return true;
	}

	private boolean addAliasesToStack() throws IOException {
		String term = termAtt.toString();
		String termLc = term.toLowerCase();
		if (!termLc.equals(term)) {
			synonymStack.push(termLc); // lc met trema
		}
		char[] result = new char[term.length()];
		ASCIIFoldingFilter.foldToASCII(term.toCharArray(), 0, result, 0, term.length());
		String termNoAc = String.valueOf(result);
		if (!termNoAc.equals(term)) {
			synonymStack.push(termNoAc);
			String termNoAcLc = termNoAc.toLowerCase();
			if (!termNoAcLc.equals(termNoAc)) {
				synonymStack.push(termNoAcLc);
			}
		}
		if (synonymStack.isEmpty()) {
			return false;
		}
		return true;
	}
}
