package twitMap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

@ServerEndpoint(value = "/TwitMap")
public class ServerDispatch {

	static String dbName = SecreteParam.dbName;
	static String userName = SecreteParam.userName;
	static String password = SecreteParam.password;
	static String hostname = SecreteParam.hostname;
	static String port = SecreteParam.port;
	static String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
					port + "/" + dbName + "?user=" + userName + "&password=" + password;
	Connection conn = null;
	static Statement setupStatement = null;
	static ResultSet resultSet = null;
	
	String keyword = null;
	
    private static final Logger LOGGER = 
            Logger.getLogger(ServerDispatch.class.getName());
    
    @OnOpen
    public void onOpen(Session session) {
    	conn = DBcontrol.getConn(jdbcUrl);
        LOGGER.log(Level.INFO, "New connection with client: {0}", 
                session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
    	keyword = message;
    	JSONObject locOBJ = new JSONObject();
    	int count=1;
    	String selectSQL = null;
		for (Session peer : session.getOpenSessions()) {
            //while (true) {
                System.out.println("sending ...");
                try {
        			if (keyword.equals("")){
        				selectSQL = "SELECT * FROM twit_rec;";
        			}else{			
        				selectSQL = "SELECT * FROM twit_rel tl LEFT JOIN twit_rec tc "
        						+ "ON tl.twit_id = tc.twit_id "
        						+ "left join twit_kw k "
        						+ "on k.kw_id = tl.kw_id"
        						+ " WHERE k.kw = '"+keyword+"';";
        			}
        			System.out.println(selectSQL);
        			resultSet = DBcontrol.doSelect(conn, selectSQL, setupStatement, resultSet);
        			while(resultSet.next()){
        				String lng = resultSet.getString("longi");
        				String lat = resultSet.getString("lati");
        				JSONObject loc = new JSONObject();
        				loc.put("longitude", lng);
        				loc.put("latitude", lat);
        				locOBJ.put("loc"+count, loc);
        				count++;
        			}
        		} catch (SQLException | JSONException e) {
        			e.printStackTrace();
        		}
                peer.getBasicRemote().sendText(locOBJ.toString());
                Thread.sleep(2000);
            //}
		}
    }
    
    @OnClose
    public void onClose(Session session) {
    	DBcontrol.disConn(conn, setupStatement, resultSet);
        LOGGER.log(Level.INFO, "Close connection for client: {0}", 
                session.getId());
    }
    
    @OnError
    public void onError(Throwable exception, Session session) {
        LOGGER.log(Level.INFO, "Error for client: {0}", session.getId());
    }

}
