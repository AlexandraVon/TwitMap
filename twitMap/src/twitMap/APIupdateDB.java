package twitMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class APIupdateDB {
	static String dbName = SecreteParam.dbName;
	static String userName = SecreteParam.userName;
	static String password = SecreteParam.password;
	static String hostname = SecreteParam.hostname;
	static String port = SecreteParam.port;
	static String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
					port + "/" + dbName + "?user=" + userName + "&password=" + password;
	
	static String keyword1 = "love";
	static String keyword2 = "snow";
	static String keyword3 = "friend";
	
	static Statement setupStatement = null;
	static ResultSet resultSet = null;
	
    public static void main(String[] args) throws TwitterException {
    	//just fill this
    	 ConfigurationBuilder cb = new ConfigurationBuilder();
         cb.setDebugEnabled(true)
           .setOAuthConsumerKey(SecreteParam.twitConsumerKey)
           .setOAuthConsumerSecret(SecreteParam.twitConsumerSec)
           .setOAuthAccessToken(SecreteParam.twitAccToken)
           .setOAuthAccessTokenSecret(SecreteParam.twitAccTokenSec);
         
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	if (status.getGeoLocation()!=null){
            		String twit_id = ""+status.getId();
                	String twit_content = status.getText();
            		String twit_location = ""+status.getGeoLocation();
            			String latitude = twit_location.split("\\{")[1].split("\\}")[0].split(",")[0].split("=")[1];
            			String longitude = twit_location.split("\\{")[1].split("\\}")[0].split(",")[1].split("=")[1];
            		String twit_time = ""+status.getCreatedAt();
            			String[] timeSplit = twit_time.split(" ");
            			String formatTime = timeSplit[5]+"-"+timeSplit[1]+"-"+timeSplit[2]+" "+timeSplit[3];
            		
            		Connection conn = DBcontrol.getConn(jdbcUrl);
            		
            		
            		int kw1_id = 0;
            		int kw2_id = 0;
            		int kw3_id = 0;
            		String selectKW1 = "SELECT kw_id FROM twit_kw WHERE kw ='"+keyword1+"';";
            		String selectKW2 = "SELECT kw_id FROM twit_kw WHERE kw ='"+keyword2+"';";
            		String selectKW3 = "SELECT kw_id FROM twit_kw WHERE kw ='"+keyword3+"';";
            		resultSet = DBcontrol.doSelect(conn,selectKW1,setupStatement, resultSet);
            		try {		
            			resultSet.next();
						kw1_id = Integer.parseInt(resultSet.getString("kw_id"));
					} catch (NumberFormatException | SQLException e) {
						e.printStackTrace();
					}
            		resultSet = DBcontrol.doSelect(conn,selectKW2,setupStatement, resultSet);
            		try {
            			resultSet.next();
						kw2_id = Integer.parseInt(resultSet.getString("kw_id"));
					} catch (NumberFormatException | SQLException e) {
						e.printStackTrace();
					}
            		resultSet = DBcontrol.doSelect(conn,selectKW3,setupStatement, resultSet);
            		try {
            			resultSet.next();
						kw3_id = Integer.parseInt(resultSet.getString("kw_id"));
					} catch (NumberFormatException | SQLException e) {
						e.printStackTrace();
					}
            		
            		
            		String insertSQL = "INSERT INTO twit_rec VALUES("
            							+"'"+twit_id+"',STR_TO_DATE('"+formatTime+"','%Y-%M-%d %H:%i:%s'),"
            							+"'"+latitude+"','"+longitude+"');";
            		System.out.println(insertSQL);
            		DBcontrol.doInsert(conn, insertSQL,setupStatement);
            		Pattern p=Pattern.compile(keyword1);
            		Matcher m=p.matcher(twit_content);
            		if (m.find()){
            			insertSQL = "INSERT INTO twit_rel VALUES('"+twit_id+"','"+kw1_id+"');";
            			System.out.println("found keyword: "+keyword1);
            			DBcontrol.doInsert(conn, insertSQL,setupStatement);
            		}
            		p=Pattern.compile(keyword2);
            		m=p.matcher(twit_content);
            		if (m.find()){
            			insertSQL = "INSERT INTO twit_rel VALUES('"+twit_id+"','"+kw2_id+"');";
            			System.out.println("found keyword: "+keyword2);
            			DBcontrol.doInsert(conn, insertSQL,setupStatement);
            		}
            		p=Pattern.compile(keyword3);
            		m=p.matcher(twit_content);
            		if (m.find()){
            			insertSQL = "INSERT INTO twit_rel VALUES('"+twit_id+"','"+kw3_id+"');";
            			System.out.println("found keyword: "+keyword3);
            			DBcontrol.doInsert(conn, insertSQL,setupStatement);
            		}
            		DBcontrol.disConn(conn,setupStatement,resultSet);
            	}
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
               // System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
    }
}