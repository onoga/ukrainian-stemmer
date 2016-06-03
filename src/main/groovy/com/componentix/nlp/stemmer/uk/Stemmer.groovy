package com.componentix.nlp.stemmer.uk

import groovy.transform.CompileStatic

@CompileStatic
class Stemmer {

    private static String[] word_ends = """
      а ам ами ах та
      в вав вавсь вався вала валась валася вали вались валися вало валось валося вати ватись ватися всь вся
      е еві ем ею
      є ємо ємось ємося ється єте єтесь єтеся єш єшся єю
      и ив ий ила или ило илося им ими имо имось имося ите итесь итеся ити ить иться их иш ишся
      й ймо ймось ймося йсь йся йте йтесь йтеся
      і ів ій ім імо ість істю іть
      ї
      ла лась лася ло лось лося ли лись лися
      о ові овував овувала овувати ого ої ок ом ому осте ості очка очкам очками очках очки очків очкові очком очку очок ою
      ти тись тися
      у ував увала увати
      ь
      ці
      ю юст юсь юся ють ються
      я ям ями ях
    """.trim().split(/\s+/)

    // WAT ?
    // к ка кам ками ках ки кою ку
    // ні ню ня ням нями нях
    private static String[] wends = word_ends.sort {-it.length()}

    // endings in unchangable words

    private static String[] stable_endings = """
      ер
      ск
    """.trim().split(/\s+/)

    private static String[] skip_ends = stable_endings.sort {-it.length()}

    // endings are changing
    private static HashMap change_endings = [
      "аче" : "ак",
      "іче" : "ік",
      "йовував" : "йов", "йовувала" : "йов", "йовувати" : "йов",
      "ьовував" : "ьов", "ьовувала" : "ьов", "ьовувати" : "ьов",
      "цьовував" : "ц", "цьовувала" : "ц", "цьовувати" : "ц",
      "ядер" : "ядр"
    ]

    // words to skip
    private static String[] stable_exclusions = """
      баядер беатріче
      віче
      наче неначе
      одначе
      паче
    """.trim().split(/\s+/)

    // words to replace
    private static HashMap exclusions = [
      "відер" : "відр",
      "був" : "бува"
    ]

    private static HashMap nagolos = [
        "а́" : "а",
        "е́" : "е",
        "є́" : "є",
        "и́" : "и",
        "і́" : "і",
        "ї́" : "ї",
        "о́" : "о",
        "у́" : "у",
        "ю́" : "ю",
        "я́" : "я"
    ]

    @CompileStatic
    public static String stem(String word) {
        //Replace Ukrainian stressed vowels to unstressed ones
        word = word.toLowerCase().collect { nagolos[it] ?: it }.join("")

        // don't change short words
        if (word.length() <= 2 ) return word;

        // check for unchanged exclusions
        if (stable_exclusions.contains(word)) {
            return word;
        }

        // check for replace exclusions
        if (exclusions[word]) {
            return exclusions[word];
        }

        // changing endings
        // TODO order endings by abc DESC
        for (String eow in change_endings.keySet().sort { change_endings[it] }) {
            if (word.endsWith(eow)) {
                return word.substring(0, word.length() - eow.length()) + change_endings[eow]
            }
        }

        // match for stable endings
        for (String eow in skip_ends) {
            if (word.endsWith(eow)) {
                return word
            }
        }

        // try simple trim
        for (String eow in wends) {
            if (word.endsWith(eow)) {
                String trimmed = word.substring(0, word.length() - eow.length())
                if (trimmed.length() > 2) {
                    return trimmed
                }
            }
        }

        return word
    }

}