/* 
 *  Copyright (c) 2024 Cisco Systems, Inc.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package cz.gargoyle.simple.echoapp;

import java.io.*;
import java.net.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.sql.*;

@RestController
public class EchoController {
	// Static Variables
	public static String unresolvedHttpUrl = "http://simple-http.com/index.html";
	public RandomAbstract randomCisco = new RandomCisco(1000,6000);
	public RandomAbstract randomAdidas = new RandomAdidas(1000,6000);
	
    @Autowired
    private RestTemplate restTemplate;

	@RequestMapping("/help")
	@ResponseBody
	public String help(HttpServletRequest request) throws Exception {
		String response = "";

		response += "/echoapp/throw - throws exception \n";
		response += "/echoapp/throwmsg - throws exception with message set \n";
		response += "/echoapp/hello - says hello \n";
		response += "/echo/** - serves any request starting by /echo \n";
		response += "/api/** - serves any request starting by /api \n";
		response += "/external-service - calls external service by query param 'url' \n";
		response += "/db - runs a db query on H2 db \n";
		return response;
	}
	
	@RequestMapping("/echoapp/throw")
	@ResponseBody
	public String throwing(HttpServletRequest request) throws Exception {
		System.out.println("Protocol: " +request.getScheme());
		System.out.println("Host: " +request.getServerName());
		System.out.println("Port: " +request.getServerPort());
		System.out.println("URI: " +request.getRequestURI());
		// Make a HTTP call to www.example.com
		// callHTTP();	
		// Call a Custom Function
		greet();
		try {
			throwException();
		} catch (Throwable e) {
			System.out.println("Exception caught: " + e.getMessage());
		}
		String response = "Protocol: " +request.getScheme() + 
			", Host: " +request.getServerName() + 
			", Port: " +request.getServerPort() + 
			", URI: " +request.getRequestURI() + 
			", Method: " +request.getMethod();
		return "Greetings from Echo - " + response;
	}

	@RequestMapping("/echoapp/throwmsg")
	@ResponseBody
	public String throwingMsg(HttpServletRequest request) throws Exception {
		System.out.println("Protocol: " +request.getScheme());
		System.out.println("Host: " +request.getServerName());
		System.out.println("Port: " +request.getServerPort());
		System.out.println("URI: " +request.getRequestURI());
		// Make a HTTP call to www.example.com
		// callHTTP();	
		// Call a Custom Function
		greet();
		try {
			throwExceptionMessage();
		} catch (Throwable e) {
			System.out.println("Exception caught: " + e.getMessage());
		}
		String response = "Protocol: " +request.getScheme() + 
			", Host: " +request.getServerName() + 
			", Port: " +request.getServerPort() + 
			", URI: " +request.getRequestURI() + 
			", Method: " +request.getMethod();
		return "Greetings from Echo - " + response;
	}

	private void throwException() throws EchoException {
		String exceptionMessage = "Horrible Error";
		System.out.println("Throwing: " + exceptionMessage);
		throw new EchoException();
		// System.out.println("Never should reach this");
	}

	private void throwExceptionMessage() throws EchoException {
		String exceptionMessage = "Horrible Error";
		System.out.println("Throwing: " + exceptionMessage);
		throw new EchoException("HELP!");
		// System.out.println("Never should reach this");
	}

	@RequestMapping("/echoapp/hello")
	@ResponseBody
	public String hello(HttpServletRequest request) throws Exception {
		System.out.println("Protocol: " +request.getScheme());
		System.out.println("Host: " +request.getServerName());
		System.out.println("Port: " +request.getServerPort());
		System.out.println("URI: " +request.getRequestURI());
		// Make a HTTP call to www.example.com
		// callHTTP();	
		// Call a Custom Function
		greet();
		String response = "Protocol: " +request.getScheme() + 
			", Host: " +request.getServerName() + 
			", Port: " +request.getServerPort() + 
			", URI: " +request.getRequestURI() + 
			", Method: " +request.getMethod();
		return "Greetings from Echo - " + response;
	}
		
	@RequestMapping("/echo/**")
	@ResponseBody
	public String echo(HttpServletRequest request) throws Exception {
		System.out.println("Protocol: " +request.getScheme());
		System.out.println("Host: " +request.getServerName());
		System.out.println("Port: " +request.getServerPort());
		System.out.println("URI: " +request.getRequestURI());
		// Make a HTTP call to www.example.com
		// callHTTP();	
		// Call a Custom Function
		String response = "Protocol: " +request.getScheme() + 
			", Host: " +request.getServerName() + 
			", Port: " +request.getServerPort() + 
			", URI: " +request.getRequestURI() + 
			", Method: " +request.getMethod();
		return "Greetings from Echo - " + response;
	}
		
	@RequestMapping("/api/**")
	@ResponseBody
	public String api(HttpServletRequest request) throws Exception {
		System.out.println("Protocol: " +request.getScheme());
		System.out.println("Host: " +request.getServerName());
		System.out.println("Port: " +request.getServerPort());
		System.out.println("URI: " +request.getRequestURI());
		// Make a HTTP call to www.example.com
		// callHTTP();	
		// Call a Custom Function
		String response = "Protocol: " +request.getScheme() + 
			", Host: " +request.getServerName() + 
			", Port: " +request.getServerPort() + 
			", URI: " +request.getRequestURI() + 
			", Method: " +request.getMethod();
		return "Received on API - " + response;
	}

	@GetMapping("/external-service")
    public String callExternalService(@RequestParam(name = "url", defaultValue = "https://external-service.com/api/data") String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

	@GetMapping("/api/customer/cisco/**")
	public String execCisco() {
		long waitMs = Math.round(randomCisco.nextRandom());
		try {
			Thread.sleep(waitMs);
		} catch (Exception e) {}
		return "Waited for " + waitMs + " ms.";
	}

	@GetMapping("/api/customer/adidas/**")
	public String execAdidas() {
		long waitMs = Math.round(randomAdidas.nextRandom());
		try {
			Thread.sleep(waitMs);
		} catch (Exception e) {}
		return "Waited for " + waitMs + " ms.";
	}

	@GetMapping("/db")
	public String execDb() {
		try {
			dbQuery();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return "Error " + e.getMessage();
		}
		
		return "Success - see logs";
	}

	public static void callHTTP() throws Exception {
		URL url = new URL(unresolvedHttpUrl);
		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		System.out.println("=====Response from simple-http.com=====");
		while ((inputLine = in.readLine()) != null) 
			System.out.println(inputLine);
		in.close();
	}
		
	public void greet() throws InterruptedException {
		System.out.println("=====Sleeping for 2 secs in greet()=====");
		Thread.sleep(2000);
		System.out.println("=====Hello from greet() function=====");
	}

	public void dbQuery() throws java.sql.SQLException, java.lang.ClassNotFoundException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");

        // Create a connection to the database
        //Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:mydatabase", "SA", "");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/db0", "SA", "");
        // Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/db0", "SA", "");

		// jdbc:h2:tcp://my-h2/my-db-name
        // Create a table and insert some data
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE mytable (id INT PRIMARY KEY, name VARCHAR(50))");
        stmt.executeUpdate("INSERT INTO mytable (id, name) VALUES (1, 'John')");
        stmt.executeUpdate("INSERT INTO mytable (id, name) VALUES (2, 'Jane')");
		stmt.close();

		stmt = conn.createStatement();
        // Retrieve data from the table
        ResultSet rs = stmt.executeQuery("SELECT id, name FROM mytable");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            System.out.println(id + " " + name);
        }

        // Clean up
        rs.close();
		stmt.close();

		stmt = conn.createStatement();
		stmt.executeUpdate("DROP TABLE mytable");

        stmt.close();
        conn.close();
    }
}
