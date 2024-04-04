package org.crescent.index.analysis;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {

  // TODO analyzer 역할하는 객체 만들어서 적용

  @GetMapping("/v1/analyzers")
  public List<String> getAnalyzerList() {
    List<String> result = new ArrayList<>();
    result.add("org.apache.lucene.analysis.en.EnglishAnalyzer");

    return result;
  }

  @GetMapping("/v1/analyzers/tokens")
  public List<String> getTokens(
      @RequestParam(name="class_name") String className,
      @RequestParam(name="text") String text
  ) throws IOException {
    Class<Analyzer> analyzerClass;
    Analyzer analyzer = null;
    try {
      analyzerClass = (Class<Analyzer>) Class.forName(className);
      analyzer = analyzerClass.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    TokenStream ts = analyzer.tokenStream("dummy", text);
    CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
    List<String> result = new ArrayList<>();

    ts.reset();
    while (ts.incrementToken()) {
      String word = termAtt.toString();
      result.add(word);
    }

    ts.end();
    return result;
  }
}
