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

import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class CMCTransportSample {

	public static void main(String argv[]) {

		String host = "cmcdomain";
		String mailFrom = "no-reply@cmcdomain";
		String mailTo = "yourname@yourdomain";


		// Properties を作成して、SMTPサーバ名を設定する。
		Properties prop = new Properties();
		prop.put("mail.smtp.host", host);

		// Session を作成する
		Session session = Session.getInstance(prop);

		// Message を作成する
		MimeMessage message = new MimeMessage(session);
		try {

			// 宛先(To)アドレスを設定
			message.setRecipient(RecipientType.TO, new InternetAddress(mailFrom));

			// 差出人(From)アドレスを設定
			message.setFrom(new InternetAddress(mailTo));

			// 送信日時(Date)を設定
			message.setSentDate(new Date());

			// メールの件名を設定
			message.setSubject("テストメール", "iso-2022-jp");

			// メール本文を設定
			message.setText("テストメッセージ", "iso-2022-jp");
			
			message.setHeader("Content-Transfer-Encoding", "7bit");

			System.out.println("send start");
			CMCTransport.send(message);
			System.out.println("send end");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
