package com.example.aafo.coinz;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

public class NewsStories {

    private String title;
    private String description;

    private NewsStories(String titlee, String descriptionn){
        title = titlee;
        description= descriptionn;
    }

    public String getTitle(){return title;}
    public String getDescription(){return description;}

    private static HashMap<String, String> currToCountry = new HashMap<String, String>();
    static{
        String shel = "Shilling";
        String countryShel = "Shililand";
        currToCountry.put(shel, countryShel);
        String dollar = "Dollar";
        String countryDollar = "Dollaland";
        currToCountry.put(dollar, countryDollar);
        String penny = "Penny";
        String countryPenny = "Pennyland";
        currToCountry.put(penny, countryPenny);
        String countryQuid = "Quidland";
        String quid = "Quid";
        currToCountry.put(quid, countryQuid);
        currToCountry.put("QUID", "Quid");
        currToCountry.put("DOLR", "Dollar");
        currToCountry.put("SHIL", "Shilling");
        currToCountry.put("PENY", "Penny");
    }

    private static String[] getCurrenciesOrdered(){
        HashMap<String, Float> ratesHash = MainActivity.ratesHash;
        Float[] values = ratesHash.values().toArray(new Float[ratesHash.size()]);
        Arrays.sort(values);
        String[] sortedCurrencies = new String[4];
        for(int i=0; i<values.length; i++){
            for (String curr:ratesHash.keySet()){
                if(ratesHash.get(curr).equals(values[i])){
                    sortedCurrencies[i] = curr;
                }
            }
        }
        return sortedCurrencies;
    }

    private static NewsStories getGood(String currency, int position){
        String goodFirstTitle = "Fire!";
        String goodFirst = "Last night, in "+currToCountry.get(currToCountry.get(currency))+" , a fire has burnt down the mint, losing around a billion of " +
                currToCountry.get(currency)+ ". Given the shortage of currency in the country and the large amount of gold it has in its vaults, the value " +
                "of currency has gone up.";

        String goodSecondTitle = "Trade deal gone well";
        String goodSecond = "A trade deal in favour of " + "has set its industrial market as the top one in the world. As a result, its currency has risen.";

        String goodThirdTitle = "3t";
        String goodThird = "3d";

        String goodFourthTitle = "4t";
        String goodFourth = "4d";

        String goodFifthTitle = "5d";
        String goodFifth = "5t";
        String [] goods = new String[]{goodFirst, goodSecond, goodThird, goodFourth, goodFifth, goodFirstTitle, goodSecondTitle, goodThirdTitle,
                goodFourthTitle, goodFifthTitle};

        return (new NewsStories(goods[position+5], goods[position]));
    }

    private static NewsStories getInter(String currency, int position){
        String interFirstTitle = "";
        String interFirst = "";

        String interSecondTitle = "";
        String interSecond = "";

        String interThirdTitle = "";
        String interThird = "";

        String interFourthTitle = "";
        String interFourth = "";

        String interFifthTitle = "";
        String interFifth = "";

        String interSixthTitle ="";
        String interSixth = "";

        String interSeventhTitle = "";
        String interSeventh = "";

        String interEighthTitle = "";
        String interEighth = "";
        String [] inters = new String[]{interFirst, interSecond, interThird, interFourth, interFifth,interSixth, interSeventh, interEighth,  interFirstTitle,
                interSecondTitle, interThirdTitle, interFourthTitle, interFifthTitle, interSixthTitle, interSeventhTitle, interEighthTitle};

        return (new NewsStories(inters[position+8], inters[position]));
    }


    private static NewsStories getBad(String currency, int position){
        String badFirstTitle = "";
        String badFirst = "";

        String badSecondTitle = "";
        String badSecond = "";

        String badThirdTitle = "";
        String badThird = "";

        String badFourthTitle = "";
        String badFourth = "";

        String [] bads = new String[]{badFirst, badSecond, badThird, badFourth, badFirstTitle, badSecondTitle, badThirdTitle,
               badFourthTitle};

        return (new NewsStories(bads[position+4], bads[position]));
    }



    public static NewsStories[] getNew(){
        //Get random numbers for choosing the news
        int intRandGood = ThreadLocalRandom.current().nextInt(4);
        int intRandInt1 = ThreadLocalRandom.current().nextInt(8);
        int intRandInt2 = ThreadLocalRandom.current().nextInt(8);
        int inRandBad = ThreadLocalRandom.current().nextInt(4);

        String[] currencies = getCurrenciesOrdered();
        NewsStories[] news = new NewsStories[currencies.length];

        //Set the news corresponding to their currencies
        news[0] = getGood(currencies[0], intRandGood);
        news[1] = getInter(currencies[1], intRandInt1);
        news[2] = getInter(currencies[2], intRandInt2);
        news[3] = getBad(currencies[3], inRandBad);


        return news;
    }
}
