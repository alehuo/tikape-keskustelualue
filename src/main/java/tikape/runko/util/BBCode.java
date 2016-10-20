package tikape.runko.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BBCode -luokka
 */
public class BBCode {

    /**
     * HashMap -olio korvattavista kohdista
     */
    private static HashMap<String, String> replaceTags;

    public BBCode() {
        replaceTags = new HashMap<>();
        //Alustetaan BBCode -muunnokset
        replaceTags.put("[b]", "<b>");
        replaceTags.put("[/b]", "</b>");
        replaceTags.put("[i]", "<i>");
        replaceTags.put("[/i]", "</i>");
        replaceTags.put("[u]", "<u>");
        replaceTags.put("[/u]", "</u>");
        replaceTags.put("[header]", "<h4>");
        replaceTags.put("[/header]", "</h4>");
        replaceTags.put("[quote]", "<blockquote>");
        replaceTags.put("[/quote]", "</blockquote>");
        replaceTags.put("[code]", "<code>");
        replaceTags.put("[/code]", "</code>");
    }

    public String parse(String content) {
        String replacedContent = content;
        //Regex
        String regex = "\\[(.*?)](.*?)\\[\\1\\]";
        //Lista kaikista jotka löytyy
        HashMap<String, String> replaced = new HashMap<>();
        //Matcher
        Matcher m = Pattern.compile(regex).matcher(replacedContent);
        //Lisää kaikki esiintyvät tagit
        while (m.find()) {
            System.out.println(m.group());
            replaced.put(m.group(), "");
        }
        for (String toBeReplaced : replaced.keySet()) {
            replaced.put(toBeReplaced, convertTags(toBeReplaced));
        }
        for (String key : replaced.keySet()) {
            replacedContent = replacedContent.replace(key, replaced.get(key));
        }

        return replacedContent;
    }

    public String convertTags(String s) {
        String tmp = s;
        for (String replaceTag : replaceTags.keySet()) {
            tmp.replace(replaceTag, replaceTags.get(replaceTag));
        }
        return tmp;
    }

}
