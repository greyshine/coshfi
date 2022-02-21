# Coshfi

coffeeshopfinder.de

----

## TODO
( )-6 Check canada stores: https://www.tweed.com/en/find-a-store
( )-5 Comments including images, enable for easy simple modaration  
( )-4 Payment Paypal, Amazaon (https://pay.amazon.de/blog/H%C3%A4ufige-gesch%C3%A4ftliche-Fragen-zu-Amazon-Pay)  
( )-3 SpringSecurity, Turn off SessionId, use own construct  
( )-2 IP, Location Lookup for each Session  
( )-1 Links bzgl. Legalisierung.  
Jeder kann Links posten. Dann einfache Verwaltung für mich (Radakteure). Evtl. mit Schlagworten/Tags versehen z.B: für
Land, Themenbereich,...



## Updating 



## Running on local development

### Updating node
When starting the vue frontend (happened to me on local development) and an error is displayed like  
`ERROR You are using Node v13.10.1, but vue-cli-service requires Node ^12.0.0 || >= 14.0.0. Please upgrade your Node version.`

Check here for example: https://medium.com/macoclock/update-your-node-js-on-your-mac-in-2020-948948c1ffb2

And do see here: https://stackoverflow.com/a/65796287/845117

## Running on a server

    java -jar coffeeshopfinder-X.Y.jar --dir.home=<datadir>  --server.port=8080 

### Application directory layout

    <dir.home>
      - data
        <will automatically create subfolder and files for data>
      - locations
        wm.json 

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

- Reverse address lookup: [nominatim.openstreetmap.org](https://nominatim.openstreetmap.org/)  
  e.g.: https://nominatim.openstreetmap.org/search?q=Maaspromenade+11%2C+Maastricht&format=geocodejson&polygon=1&addressdetails=1

## Free image references

- <a href="https://www.flaticon.com/de/autoren/nikita-golubev" title="Nikita Golubev"><img src="data:image/png;base64,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" alt=""/>
  by Nikita Golubev</a>
