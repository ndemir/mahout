/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.halbur.analyzer.tr;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author ndemir
 */
public final class CustomFilter extends TokenFilter{
    
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    public CustomFilter(TokenStream input){
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (! input.incrementToken()) {
            return false;
        }

        if (termAtt.toString().contains("'")){
            termAtt.setLength(termAtt.toString().indexOf("'"));            
        }else if(termAtt.toString().contains("’")){
            termAtt.setLength(termAtt.toString().indexOf("’"));
        }

        return true;
    }

}
