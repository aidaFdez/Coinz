package com.example.aafo.coinz;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

public class NewsStories {
    static String countryShel = "Sheliland";
    static String countryPenny = "Pennyland";
    static String countryDollar = "Dollaland";
    static String countryQuid = "Quidland";
    static String quid = "Quid";
    static String shel = "Shelling";
    static String penny = "Penny";
    static String dollar = "Dollar";
    static HashMap<String, String> currToCountry = new HashMap<String, String>();
    static{
        currToCountry.put(shel, countryShel);
        currToCountry.put(dollar, countryDollar);
        currToCountry.put(penny, countryPenny);
        currToCountry.put(quid, countryQuid);
    }

    String goodFirst = "This morning, in %1$s, a fire has burnt down the mint, losing around a billion of " +
            "%2$s. Given the shortage of currency in the country, ";
    String goodSecond = "";
    String goodThird = "";
    String goodFourth = "";
    String goodFifth = "";
    String [] goods = new String[]{goodFifth, goodFirst, goodSecond, goodThird, goodFourth};

    String interFirst = "";
    String interSecond = "";
    String interThird = "";
    String interFourth = "";
    String interFifth = "";
    String interSitxh = "";
    String interSeventh = "";
    String interEighth = "";
    String [] inter = new String []{interFirst, interSecond, interThird, interFourth, interFifth, interSitxh, interSeventh, interEighth};

    String badFirst = "";
    String badSecond = "";
    String badThird = "";
    String badFourth = "";
    String [] bads = new String []{badFirst, badSecond, badThird, badFourth};

    public String getNew(String [] currency ){
        int intRandGood = ThreadLocalRandom.current().nextInt(4);
        int intRandInt1 = ThreadLocalRandom.current().nextInt(8);
        int intRandInt2 = ThreadLocalRandom.current().nextInt(8);
        int inRandBad = ThreadLocalRandom.current().nextInt(4);
        //Hacerlo pasando el currency y el número de lo "fuerte" que es ya al principio.


        return "";
    }
}
