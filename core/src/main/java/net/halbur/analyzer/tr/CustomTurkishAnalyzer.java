/*
 *  Copyright 2013 ndemir.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package net.halbur.analyzer.tr;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import org.apache.lucene.analysis.core.LowerCaseFilter;

public class CustomTurkishAnalyzer extends org.apache.lucene.analysis.Analyzer {

  private Version matchVersion;

  public CustomTurkishAnalyzer(Version matchVersion) {
    this.matchVersion = matchVersion;
  }

  private CharArraySet getStopwordSet(){


   // File stopwordFile = FileUtils.toFile(getClass().getResource("/opt/mahout/core/src/main/resources/stopwords.txt"));
          File stopwordFile = FileUtils.toFile(getClass().getResource("/resources/stopwords.txt"));
//    File stopwordFile = new File("/opt/mahout/core/src/main/resources/stopwords.txt");
    String[] stopwordList = null;
    try {
        String s = FileUtils.readFileToString(stopwordFile);
        stopwordList = s.split("\n");
    } catch (IOException ex) {
        Logger.getLogger(CustomTurkishAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
    }
    Collection<String> stopwordCollection = new ArrayList<String>(Arrays.asList(stopwordList));
    return new CharArraySet(matchVersion, stopwordCollection, true);
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
    StandardTokenizer standardTokenizer = new StandardTokenizer(matchVersion, reader);
//    TurkishStemmer turkishStemmerFilter = new TurkishStemmer(standardTokenizer);
    LowerCaseFilter lowercaseFilter = new LowerCaseFilter(matchVersion, standardTokenizer);
    CustomFilter customFilter = new CustomFilter(lowercaseFilter);

    return new TokenStreamComponents(standardTokenizer, new StopFilter(matchVersion, customFilter, getStopwordSet()));
  }

  public static void main(String[] args) throws IOException {
    // text to tokenize
    final String text = "Ali'ler bize geldiler; oradan da okula gittiler ve yanlarında ğ,ç,ü,Ü,Ğ,Ç getirdiler. ricciardone. ali.com. Sinop'ta geldi.";

    Version matchVersion = Version.LUCENE_41;
    CustomTurkishAnalyzer analyzer = new CustomTurkishAnalyzer(matchVersion);
    TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

    // get the CharTermAttribute from the TokenStream
    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

    try {
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