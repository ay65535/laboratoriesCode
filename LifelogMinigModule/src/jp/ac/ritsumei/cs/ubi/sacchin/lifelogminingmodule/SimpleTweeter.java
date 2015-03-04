package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SimpleTweeter {
	private static String oauthConsumerKey_ = "wUTjbLkRd9ZiFPCjlfAg";
	private static String oauthConsumerSecret_ = "Re93kYi6G3f6AdvqRUdPJCixxPLB6bud0zRNR8g";
	private static String oauthAccessToken_ = "980295422-mxZ8S4xypjRhl0bD1NyfzSLLY4zRTsFxd2YEz97k";
	private static String oauthAccessTokenSecret_ = "FQfoCVGJ2iaSBStoqVBogtG7mpnHd2qjdoy4h12he1g";
	
	public static void tweet(String word){
        ConfigurationBuilder builder = new ConfigurationBuilder();
		
        builder.setOAuthAccessToken(oauthAccessToken_);
        builder.setOAuthAccessTokenSecret(oauthAccessTokenSecret_);
		
        builder.setOAuthConsumerKey(oauthConsumerKey_);
        builder.setOAuthConsumerSecret(oauthConsumerSecret_);
		
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();

        try {
            twitter.updateStatus(word);
        }
        catch (TwitterException ex) {
            ex.printStackTrace();
        }
	}
}
