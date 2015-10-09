# CustomersMailCloud - Java/SMTP
DNSラウンドロビンで負荷分散した複数台のSMTPサーバにメールを送信するJavaライブラリです。 
JavaMailが提供するTransportクラスの代わり使用することができます。

## Requirement
JDK1.6以上  
JavaMail 1.4以上  

## Build
ご自身でソースからビルドする場合、以下の手順でビルドしてください。  
```
$ git clone https://github.com/hde-cm/java-smtp-client.git
$ cd java-smtp-client
$ mvn install
$ cp target/cmc-smtp-1.0.0.jar ./cmc-smtp.jar
```

## Install
cmc-smtp.jar をクラスパスが通っているディレクトリにコピーしてください。  

## Usage
使用方法は、<https://smtps.jp/doc/javamail.html> を参照してください。

## License
本ソースコードは、Apache License Version 2.0 で提供します。

## Author
Masahiro Okubo HDE, Inc.
