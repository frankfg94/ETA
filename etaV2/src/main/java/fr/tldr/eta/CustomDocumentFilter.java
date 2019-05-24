package fr.tldr.eta;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDocumentFilter extends DocumentFilter
{

    private final static String STANDARD = "std";
    private final static String KEY_WORDS = "keyWord";
    private final static String NUMBERS = "number";
    private final static String FUNCTION = "functions";
    private final static String STRINGS = "strings";


    private final StyledDocument styledDocument;

    private HashMap<String,AttributeSet> attributeSets;
    private HashMap<String,Pattern> patternsSets;


    private JTextPane textPane;

    public CustomDocumentFilter(JTextPane pane)
    {
        this.styledDocument = pane.getStyledDocument();
        this.textPane = pane;


        this.attributeSets = new HashMap<>();
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        this.attributeSets.put(STANDARD, styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(0, 0, 0)));
        this.attributeSets.put(KEY_WORDS, styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(156, 63, 0)));
        this.attributeSets.put(NUMBERS, styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(114, 18, 130)));
        this.attributeSets.put(FUNCTION, styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(0, 185, 185)));
        this.attributeSets.put(STRINGS, styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(0, 135, 0)));

        this.patternsSets =  new HashMap<>();
        this.patternsSets.put(KEY_WORDS, buildPattern(new String[]{"and","break","do", "else", "elsif", "end","for","if","in","local","or","not","repeat","return","then","until","while","function"}, null));
        //language=RegExp
        this.patternsSets.put(NUMBERS, buildPattern(new String[]{"false","nil","true"}, "(^|\\s)-?\\b((?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?|(0[xX][0-9a-fA-F]+))\\b(\\s|$)"));
        this.patternsSets.put(FUNCTION, buildPattern(null, "(?<=function )[\\s]*\\b[^\\(\\s]+\\b"));
        this.patternsSets.put(STRINGS, buildPattern(null, "(?<!\\\\)[\\\"](?:\\\\.|.)*?(?<!\\\\)[\\\"]"));


    }



    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attributeSet) throws BadLocationException {
        super.insertString(fb, offset, text, attributeSet);

        handleTextChanged();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);

        handleTextChanged();
    }

    private void handleTextChanged()
    {
        SwingUtilities.invokeLater(this::updateTextStyles);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
        super.replace(fb, offset, length, text, attributeSet);

        handleTextChanged();
    }


    private void updateTextStyles() {

        styledDocument.setCharacterAttributes(0,textPane.getText().length(),this.attributeSets.get(STANDARD),true);

        System.out.println();
        //number
        changeColor(NUMBERS);
        changeColor(STRINGS);
        changeColor(FUNCTION);
        changeColor(KEY_WORDS);
        //key words

    }

    private void changeColor(String matching)
    {
        Matcher matcher = this.patternsSets.get(matching).matcher(textPane.getText().replaceAll("\r",""));
        while(matcher.find())
        {
           // System.out.println(textPane.getText().substring(matcher.start(), matcher.end()) + "   length : "+(matcher.end() - matcher.start()) + "  start :"+matcher.start()+"   end:"+matcher.end());
            styledDocument.setCharacterAttributes(matcher.start(),matcher.end() - matcher.start(),this.attributeSets.get(matching),false);
        }
    }


    private Pattern buildPattern(String[] str, String regexp)
    {
        StringBuilder sb = new StringBuilder();
        if(str != null)
        {
            for(String word : str)
            {
                sb.append("\\b");
                sb.append(word);
                sb.append("\\b|");
            }
        }
        if (regexp !=null) {

            sb.append(regexp);
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1)=='|') {
            sb.deleteCharAt(sb.length() - 1);
        }

       return Pattern.compile(sb.toString());
    }


}
