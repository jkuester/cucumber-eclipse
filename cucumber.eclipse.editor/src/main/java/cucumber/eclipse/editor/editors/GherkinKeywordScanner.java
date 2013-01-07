package cucumber.eclipse.editor.editors;

import gherkin.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;


public class GherkinKeywordScanner extends RuleBasedScanner {
		
    private static final List<String> FEATURE_ELEMENT_KEYWORD_KEYS = Arrays.asList("feature", "background", "scenario", "scenario_outline", "examples");
    private static final List<String> STEP_KEYWORD_KEYS = Arrays.asList("given", "when", "then", "and", "but");
    private static I18n i18n;   
    private static String _code = "en";
    private ColorManager manager;
	
	public GherkinKeywordScanner(ColorManager manager) {
		this.manager = manager;
		configureRules();
	}

	public void configureRules() {
		
		validateIsoCode();
		
		i18n = new I18n(_code); 
		
		IToken keyword= new Token(new TextAttribute(manager.getColor(GherkinColorConstants.KEYWORD)));
		IToken step= new Token(new TextAttribute(manager.getColor(GherkinColorConstants.STEP)));
		IToken string= new Token(new TextAttribute(manager.getColor(GherkinColorConstants.STRING)));
		IToken comment= new Token(new TextAttribute(manager.getColor(GherkinColorConstants.COMMENT)));
		IToken other= new Token(new TextAttribute(manager.getColor(GherkinColorConstants.DEFAULT)));

		List<IRule> rules= new ArrayList<IRule>();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("#", comment)); //$NON-NLS-1$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
		
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new GherkinWhitespaceDetector()));


		WordRule wordRule= new WordRule(new GherkinWordDetector(), other);
		WordRule wordStarStepRule= new WordRule(new GherkinStarStepWordDetector(), other);
		
		// Add rule to colour the * that can be used instead of steps
		wordStarStepRule.addWord("*", keyword);
	
		for (String featureElement: FEATURE_ELEMENT_KEYWORD_KEYS ) {
			List<String> keywords = i18n.keywords(featureElement);
			for (String e : keywords) {
				  rules.add(new SingleLineRule(e.trim(), " ", keyword ));
			}
		}
		
		for (String featureElement: STEP_KEYWORD_KEYS ) {
			List<String> keywords = i18n.keywords(featureElement);
			for (String e : keywords) {
				rules.add(new SingleLineRule(e.trim(), " ", step ));	
			}
		}
		
		rules.add(wordRule);
		rules.add(wordStarStepRule);

		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	private void validateIsoCode() {
		boolean valid = false;
		try {
			List<I18n> all = gherkin.I18n.getAll();
			for (I18n i18n : all) {
				if (i18n.getIsoCode().equals(_code)) {
					valid = true;
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			_code = "en";
		}
		
		if (!valid) _code = "en";
	}
	
	public static void setCode(String code) {
		_code = code;
	}
}