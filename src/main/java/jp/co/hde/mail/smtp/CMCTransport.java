/*
 * Copyright 2015 Masahiro Okubo HDE,Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.hde.mail.smtp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.SessionGetter;
import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.sun.mail.smtp.SMTPTransport;

public class CMCTransport {

	public static void send(Message msg) throws SendFailedException {
		CMCTransport.send(msg, null, null, null);
	}
	
	public static void send(Message msg, Address[] addresses) throws SendFailedException {
		CMCTransport.send(msg, addresses, null, null);
	}

	public static void send(Message msg, String user, String password) throws SendFailedException {
		CMCTransport.send(msg, null, user, password);
	}
	
	public static void send(Message msg, Address[] addresses, String user, String password) throws SendFailedException {
		
		Properties prop = null;
		Session session = null;
		session = SessionGetter.getSession(msg);
		prop = session.getProperties();
		prop.put("mail.smtp.conectiontimeout", 2000);
		prop.put("mail.smtp.timeout", 2000);
		prop.put("mail.smtp.port", 10025);

		String host = prop.getProperty("mail.smtp.host");
		if (host.isEmpty()) {
			throw new SendFailedException("Can not find mail.smtp.host property's value.");
		}
		
		List<String> ipaddrs = null;
		try {
			ipaddrs = CMCTransport.resolveDNS(host);
			if (ipaddrs == null || ipaddrs.size() == 0) {
				throw new SendFailedException("Can not solve dns: hostname=" + host);
			}
		} catch (NamingException e) {
			throw new SendFailedException(e.getMessage());
		}
		
		List<Exception> errors = new ArrayList<Exception>();
		boolean isSuccess = false;
		for (String ip : ipaddrs) {
			prop.put("mail.smtp.host", ip);
			try {
				URLName url = new URLName("smtp",host,10025,null,user,password);
				Transport t = new SMTPTransport(session,url);
				if( addresses == null) {
					addresses = msg.getAllRecipients();
				}
				try {
					t.connect();
					t.sendMessage(msg, msg.getAllRecipients());
				} finally {
					t.close();
				}
				isSuccess = true;
				break;
			} catch (Exception e) {
				errors.add(e);
			}
		}
		if (!isSuccess) {
			String message = "";
			for (Exception e : errors) {
				message += e.getMessage();
				message += " / ";
			}
			throw new SendFailedException(message);
		}
	}
	
	private static List<String> resolveDNS(String hostname) throws NamingException {
		ArrayList<String> results = new ArrayList<String>();

		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		env.put("com.sun.jndi.dns.timeout.initial", "2000");
		env.put("com.sun.jndi.dns.timeout.retries", "5");

		DirContext ictx = new InitialDirContext(env);
		String[] ids = (new String[] { "A" });
		Attributes attrs = ictx.getAttributes(hostname, ids);

		Attribute attr = attrs.get("A");
		if (attr != null && attr.size() > 0) {
			NamingEnumeration<?> e = attr.getAll();
			while (e.hasMore()) {
				results.add((String) e.next());
			}
		}

		Collections.shuffle(results);

		return results;
	}

}
