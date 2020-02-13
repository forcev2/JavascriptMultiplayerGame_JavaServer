package  core.web;

import java.io.*;
import java.net.URLDecoder;
import java.sql.Time;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.sun.java.util.jar.pack.ConstantPool.Index;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class MyHttpServer implements HttpHandler{
		class Player {
			int x;
			int y;
			int hp;
			String name;
			String color;
			String shoots;
			String mouseX;
			String mouseY;
			String number;
			Player(String name, String color){
				this.x = 250;
				this.y = 250;
				this.hp = 100;
				this.name = name;
				this.color = color;	
			}
		}
		
		public static List<Boolean> connection = new ArrayList<Boolean>(); 
	
		public static List<Player> players = new ArrayList<Player>();
		
		public static List<String> shoots = new ArrayList<String>();

        public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            String query = url;
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        }
        
        public String parseName(String str) {
        	return str.substring(str.indexOf('=') + 1, str.indexOf('&'));
        }
        public String parseColor(String str) {
        	return str.substring(str.indexOf('&') + 1, str.indexOf("/n"));
        }
        
        public String allPlayersResp() {
        	String response = "";
        	for(int i = 0; i < players.size(); i++) {
        		if(i != 0)
        			response += "&";
        		response += "{";
        		response += "\"name\":" +" \""+players.get(i).name + "\",";
        		response += "\"x\":" +" "+players.get(i).x + ",";
        		response += "\"y\":" +" "+players.get(i).y + ",";
        		response += "\"hp\":" +" "+players.get(i).hp + ",";
        		response += "\"color\":" +" \""+players.get(i).color + "\",";
        		response += "\"number\":" +" \""+ players.get(i).number + "\"";
        		response += "}";
        	}
        	return response;
        }
        
        void updatePlayers(String forUpdate){
        	String[] part = forUpdate.substring(forUpdate.indexOf('=') + 1).split("&");
        	int i = Integer.parseInt(part[0]);
        	players.get(i).x = Integer.parseInt(part[1]);
        	connection.set(i, true);
        	players.get(i).y = Integer.parseInt(part[2]);
        	players.get(i).hp = Integer.parseInt(part[3]);       	
        }
        
        public void newPlayer(String s) {
        	String name = parseName(s);
        	String color = parseColor(s);
        	players.add(new Player(name,color));  
        	players.get(players.size() - 1).number = Integer.toString(players.size() - 1);
        	shoots.add("/non");
        	connection.add(true);
        }
        
        public void shoot(String st) {
        	st = st.substring(st.indexOf("=") + 1);
        	System.out.println(st);
        	for(int i = 0; i < shoots.size(); i++) {
        		shoots.set(i, st);
        	}
        }
        
        public String getInfo(String st) {
        	//System.out.println(st);
        	String s = st.substring(st.indexOf("=") + 1,  st.indexOf("&"));
        	//System.out.println(s);
        	int i = Integer.parseInt(s);
        	//System.out.println(i + " from " + st);
        	String response = shoots.get(i);
        	shoots.set(i,"/non");
        	
        	return response;
        }
        
        @Override
        public void handle(HttpExchange t) throws IOException {

            String response = "domsylne";
            String temp =  t.getRequestURI().getQuery();
            String temp2 =  t.getRequestURI().getPath();

           // System.out.println(temp2);
            
            response = temp2;
            
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            
            if(temp2.startsWith("/NEW_PLAYER")) {
            	newPlayer(temp2);
            	response = "number=" + Integer.toString(players.size() - 1);
            }else if(temp2.startsWith("/PLAYER=")) {
            	updatePlayers(temp2);
            	response = allPlayersResp();
            	String bullet = "|bullet=" + getInfo(temp2);
            	response += bullet;
            }else if(temp2.startsWith("/Shoots=")) {
            	 shoot(temp2);              	
            }else if(temp2.startsWith("/Info=")) {
            	 response = getInfo(temp2);
            }
            
           // System.out.println(response);
            

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    t.sendResponseHeaders(204, -1);
                    return;
            }
                     
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        public class connectionCheck extends Thread {
        	public void run() {
        		Date t = new Date();
        		Long start = t.getTime();
        		
        		while(true) {
            		t = new Date();
            		long end = t.getTime();
            		
        			if(end - start > 2000) {
        				
	        			for(int i = 0; i < connection.size(); i++) {
	        				if(connection.get(i) == false) {
	        					
	        					players.get(i).x = -100;
	        					players.get(i).y = -100;
	        				}
	        			}
	        			for(int i = 0; i < connection.size(); i++) {
	        				connection.set(i, false);
	        			}
	        			t = new Date();
	        			start = t.getTime();
        			}            		
        		}
        	}
        }

}
