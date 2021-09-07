# Coshfi

coffeeshopfinder.de

----

## TODO
( )-5 Comments including images, enable for easy simple modaration  
( )-4 Payment Paypal, Amazaon (https://pay.amazon.de/blog/H%C3%A4ufige-gesch%C3%A4ftliche-Fragen-zu-Amazon-Pay)  
( )-3 SpringSecurity, Turn off SessionId, use own construct  
( )-2 IP, Location Lookup for each Session  
( )-1 Links bzgl. Legalisierung.  
Jeder kann Links posten. Dann einfache Verwaltung für mich (Radakteure).
Evtl. mit Schlagworten/Tags versehen z.B: für Land, Themenbereich,...

## Running on a server

    java -jar coffeeshopfinder-X.Y.jar --datadur=<datadir>  --server.port=8080 

## Setup securely with a domain

### Proxy with nginx on a server
_If things are not working probably there is a firewall setup (e.g. fail2ban)!_

Nginx docs: http://nginx.org/en/docs/beginners_guide.html


Domain ist connected to another server.  
Domain serves with 80 (http) and 443 (https) ports.  
These need to be redirected on server side:

    https://coffeeshopfinder.de:80 -> https://coffeeshopfinder.de:443 
    https://coffeeshopfinder.de:443 -> http(s)://SERVER:9081

### Build ssl with sslforfree.com 

    keytool -import -alias ssl-for-free.coffeeshopfinder -file csf-sslforfree-certificate.crt -keystore coffeeshopfinder.p12 -storepass password

https://www.thomasvitale.com/https-spring-boot-ssl-certificate/    
https://stackoverflow.com/a/38136511/845117  


### When The jar is directly served - no proxy

'Directly' means no nginx or apache is the first hand proxying any requests.

There is the need to have a specific `ssl.properties` file on directory where the app starts from..
In the `src/main/resources/ssl.properties` there are hints for a setup.
Do have a `coffeeshopfinder.p12` file on the same folder as well.

Example setuo:

    server.ssl.enabled=true
    server.ssl.key-store-type=PKCS12
    server.ssl.key-store=coffeeshopfinder.p12
    server.ssl.key-store-password=password
    server.ssl.key-alias=coffeeshopfinder

####Generate a PKCS12 key on the server
On the server where the java runs:

    keytool -genkeypair -alias coffeeshopfinder -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore coffeeshopfinder.p12 -validity 3650

* https://www.baeldung.com/spring-boot-https-self-signed-certificate  
  _The certificate has to be created on the server where the instance runs._
*  https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.webserver.configure-ssl

## Web
This is run with vue2. Vue3 currently seems not to support bootstrap-vue.

## Links

- https://linuxconfig.org/how-to-use-nginx-to-redirect-all-traffic-from-http-to-https
- https://regenrek.com/posts/how-to-create-an-animated-vue-sidebar-menu-with-vue-observable
- https://markus.oberlehner.net/blog/using-location-data-with-vue-and-open-street-map/
- Maps with Vuejs, https://ej2.syncfusion.com/vue/documentation/maps/getting-started/
- https://blog.logrocket.com/building-an-interactive-map-with-vue-and-leaflet/
- https://blog.hubspot.com/website/best-free-ssl-certificate-sources
- https://www.sslforfree.com/

- https://weedmaps.com/
- https://www.coffeeshopdirect.com/
- https://dutchcoffeeshops.com/
