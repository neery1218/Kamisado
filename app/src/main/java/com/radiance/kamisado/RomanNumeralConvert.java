package com.radiance.kamisado;

/**
 * Created by Michael on 6/20/2015.
 */
abstract class RomanNumeralConvert {

    public  static String convertToRomanNumerals(int num){
        String s = "";
        switch(num){
            case 0: s += "-"; break;
            case 1: s += "I"; break;
            case 2: s += "II"; break;
            case 3: s += "III"; break;
            case 4: s += "IV"; break;
            case 5: s += "V"; break;
            case 6: s += "VI"; break;
            case 7: s += "VII"; break;
            case 8: s += "VIII"; break;
            case 9: s += "IX"; break;
            default: s += "IX";
        }
        return s;
    }
}
