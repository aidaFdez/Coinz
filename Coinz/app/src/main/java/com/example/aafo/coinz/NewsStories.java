package com.example.aafo.coinz;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

public class NewsStories {

    private String title;
    private String description;

    NewsStories(String titlee, String descriptionn){
        title = titlee;
        description= descriptionn;
    }

    public String getTitle(){return title;}
    public String getDescription(){return description;}

    private static HashMap<String, String> currToCountry = new HashMap<>();
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
        String country = currToCountry.get(currToCountry.get(currency));
        String curr = currToCountry.get(currency);
        String goodFirstTitle = "Fire!";
        String goodFirst = "Last night, in "+country+" , a fire has burnt down the mint, losing around a billion of " +
                curr+ ". Given the shortage of currency in the country and the large amount of gold it has in its vaults, the value " +
                "of currency has gone up.";

        String goodSecondTitle = "Great trade deal";
        String goodSecond = "A trade deal in favour of " +country+ " has set its industrial market as the top one in the world. As a result, its currency has risen.";

        String goodThirdTitle = "Joined the monetary union";
        String goodThird = country+" has voted to join the monetary union formed by its neighbours. This step will aid their economy, and the first signals of it can " +
                "already be seen.";

        String goodFourthTitle = "New discoveries";
        String goodFourth = "Scientists in "+country+" have invented the first teleporter in the world. So far it can only send and receive inert matter, but it is a " +
                "great step towards a completely new way of trade.";

        String goodFifthTitle = "Good season";
        String goodFifth = country+" has had a very good crop this year. Thanks to this, they have exceeded the amount of food needed to sustain the country, so they " +
                "have started to sell their surplus to their neighbours. The economy is flourishing thanks to this, as reflected on the "+curr;
        String [] goods = new String[]{goodFirst, goodSecond, goodThird, goodFourth, goodFifth, goodFirstTitle, goodSecondTitle, goodThirdTitle,
                goodFourthTitle, goodFifthTitle};

        return (new NewsStories(goods[position+5], goods[position]));
    }

    private static NewsStories getInter(String currency, int position){
        String country = currToCountry.get(currToCountry.get(currency));
        String curr = currToCountry.get(currency);
        String interFirstTitle = "Sunny day";
        String interFirst = "Today is a sunny and lazy day in "+country+". People are out enjoying their day off because of the national festivity. We wish everyone " +
                "in "+country+" a very nice day.";

        String interSecondTitle = "Train doing well";
        String interSecond = "The train company founded by "+country+" is stabilising. According to the government, thanks to this the economy is also stable.";

        String interThirdTitle = "Unchanged day";
        String interThird = "Today points to be an exact repeat of yesterday and previous days in "+country+". The citizens are starting to feel like Bill Murray in " +
                "The Groundhog Day, but the situation is stable so far.";

        String interFourthTitle = "Stagnant but happy";
        String interFourth = country+"'s economy is not moving much, but its citizens seem to be happy with their lives, according to several polls. As a welfare state," +
                " this seems like a great position to be in";

        String interFifthTitle = "Friendly relationships";
        String interFifth = "Friends are good. And "+country+" knows that. That's why they are keeping their allies close. Even if they are living stable and peaceful " +
                "times, it is never known when something could change.";

        String interSixthTitle ="Neutrality";
        String interSixth = "Even if the allies of "+country+" have gotten involved in the war at the West, they have decided to stay neutral. This has stabilised their " +
                "market.";

        String interSeventhTitle = "National satisfaction";
        String interSeventh = "According to several surveys from various independent sources, the citizens of "+country+" are satisfied with their lives and would not" +
                " like it to be much different from how it is at the moment.";

        String interEighthTitle = "Fundraising concert";
        String interEighth = country+" has decided to organize a fundraising concert to aid other countries. The biggest bands of the country and some internationals " +
                "will be playing this weekend in the biggest arena of the capital. Everyone is invited to buy tickets and encouraged to donate to the cause.";
        String [] inters = new String[]{interFirst, interSecond, interThird, interFourth, interFifth,interSixth, interSeventh, interEighth,  interFirstTitle,
                interSecondTitle, interThirdTitle, interFourthTitle, interFifthTitle, interSixthTitle, interSeventhTitle, interEighthTitle};

        return (new NewsStories(inters[position+8], inters[position]));
    }


    private static NewsStories getBad(String currency, int position){
        String country = currToCountry.get(currToCountry.get(currency));
        String curr = currToCountry.get(currency);
        String badFirstTitle = country+"exit";
        String badFirst = "The citizens of "+country+" have voted to leave the economic union with their neighbours. The results have not pleased its neighbours. "+
                "Long debates and negotiations await the country.";

        String badSecondTitle = "Revolution";
        String badSecond = "After a long time of protests in "+country+", the citizens have started a campaign against the government. The entire country is on the edge," +
                " and "+curr+" shows it.";

        String badThirdTitle = "Main political party condemned for mafia";
        String badThird = "According to the last news from "+country+", its main polytical party has been found guilty of hiring a hitman in order to get rid of " +
                "a member that had incriminating evidence on the top of the party. The country's reputation has gone down, as well as "+curr;

        String badFourthTitle = "Bad trade deal";
        String badFourth = country+" has signed a new trade deal with a far away country. This deal has been signed under great pressure from its allies, but it does "+
                "not help the country much. This can be seen in the plummeting value of "+curr;

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
        news[0] = getGood(currencies[3], intRandGood);
        news[1] = getInter(currencies[2], intRandInt1);
        news[2] = getInter(currencies[1], intRandInt2);
        news[3] = getBad(currencies[0], inRandBad);


        return news;
    }
}
