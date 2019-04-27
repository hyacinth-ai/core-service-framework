#!/bin/bash
STOREPASS=password
WORKFILE_PREFIX=tomcat
KEY_ALIAS=tomcat
cat *.pem > $WORKFILE_PREFIX.pem
openssl pkcs12 -export -out $WORKFILE_PREFIX.pkcs12 -in $WORKFILE_PREFIX.pem -password pass:$STOREPASS
keytool -genkey -keyalg RSA -alias $KEY_ALIAS -keystore $WORKFILE_PREFIX.ks -storepass $STOREPASS -dname CN=localhost
keytool -delete -alias $KEY_ALIAS -keystore $WORKFILE_PREFIX.ks -storepass $STOREPASS
keytool -importkeystore -srckeystore $WORKFILE_PREFIX.pkcs12 -srcstorepass $STOREPASS -destkeystore $WORKFILE_PREFIX.ks -deststoretype JKS -deststorepass $STOREPASS
keytool -changealias -alias 1 -destalias $KEY_ALIAS -keystore $WORKFILE_PREFIX.ks -storepass $STOREPASS
keytool -list -keystore $WORKFILE_PREFIX.ks -storepass $STOREPASS

