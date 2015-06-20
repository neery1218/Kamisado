/**
 * Created by Michael on 6/20/2015.
 */
abstract class RomanNumeralConvert {

    public String convertToRomanNumerals(int num){
        String s = "";
        int i = num % 5, v = num - i;
        if(v == 1)
            s += "V";
        switch (i){
            case 0: s += ""; break;
            case 1: s += "I"; break;
            case 2: s += "II"; break;
            case 3: s += "III"; break;
            case 4: s += "IV"; break;
        }

        if(num == 9)
            s = "IX";
        return s;
    }
}
