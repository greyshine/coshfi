(function(){var e={6191:function(e,n,r){"use strict";r(6992),r(8674),r(9601),r(7727);var t=r(144),i=r(8345),s=r(6822),a=r(8483),u=(r(44),function(){var e=this,n=e.$createElement,t=e._self._c||n;return t("div",{attrs:{id:"app"}},[t("Sidebar"),t("nav",{staticClass:"main-nav"},[t("img",{staticClass:"logo",attrs:{src:r(8472),alt:"CoffeeShopFinder"}}),t("div",{staticClass:"logo"},[e._v("CoffeeShopFinder")]),t("Burger")],1),t("hr"),t("router-view")],1)}),o=[],l=function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",{attrs:{id:"burger"},on:{click:function(n){return n.preventDefault(),e.toggle.apply(null,arguments)}}},[e._t("default",(function(){return[e._m(0)]}))],2)},d=[function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("button",{staticClass:"burger-button",attrs:{type:"button",title:"Menu"}},[r("span",{staticClass:"burger-bar burger-bar--1"}),r("span",{staticClass:"burger-bar burger-bar--2"}),r("span",{staticClass:"burger-bar burger-bar--3"})])}],c={methods:{toggle:function(){this.$root.bus.$emit("main-menu")}}},h=c,f=r(3736),b=(0,f.Z)(h,l,d,!1,null,"34837a64",null),g=b.exports,m=function(){var e=this,n=e.$createElement,t=e._self._c||n;return t("div",{staticClass:"sidebar"},[e.isPanelOpen?t("div",{staticClass:"sidebar-backdrop",on:{click:e.closeSidebarPanel}}):e._e(),t("transition",{attrs:{name:"slide"}},[e.isPanelOpen?t("div",{staticClass:"sidebar-panel"},[t("div",{staticClass:"top"},[t("img",{staticClass:"logo",attrs:{src:r(8472),alt:"CoffeeShopFinder"}}),e._v(" CoffeeShopFinder ")]),t("ul",{on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[t("li",{on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[t("router-link",{attrs:{to:{name:"home"}},on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[e._v("Home")])],1),t("li",{on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[e._v("Login")]),t("li",{on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[t("router-link",{attrs:{to:{name:"register"}}},[e._v("Register")])],1),t("li",{on:{click:function(n){return n.preventDefault(),e.closeSidebarPanel.apply(null,arguments)}}},[t("router-link",{attrs:{to:{name:"infos"}}},[e._v("Infos & Imprint")])],1)])]):e._e()])],1)},p=[],v={data:function(){return{isPanelOpen:!1}},mounted:function(){var e=this;this.$root.bus.$on("main-menu",(function(){return e.isPanelOpen=!0}))},methods:{closeSidebarPanel:function(){this.isPanelOpen=!1}}},w=v,k=(0,f.Z)(w,m,p,!1,null,null,null),z=k.exports,S={name:"App",components:{Burger:g,Sidebar:z},created:function(){this.$root.bus=new t.default({})}},_=S,D=(0,f.Z)(_,u,o,!1,null,null,null),I=D.exports,A=function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",[r("OpenStreetMap")],1)},G=[],W=function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",{attrs:{id:"mapContainer"}})},y=[],C=(r(1249),r(5243)),E=r.n(C),B={name:"openstreetmap",data:function(){return{defaultLatLon:[50.853392,5.694722],mapDiv:null}},mounted:function(){this.setupLeafletMap()},methods:{setupLeafletMap:function(){this.mapDiv=E().map("mapContainer").setView(this.defaultLatLon,20),E().tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",{attribution:'&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'}).addTo(this.mapDiv)}}},x=B,O=(0,f.Z)(x,W,y,!1,null,"3940dbed",null),P=O.exports,F={components:{OpenStreetMap:P},data:function(){return{user:"USER"}},mount:function(){},methods:{}},Z=F,M=(0,f.Z)(Z,A,G,!1,null,"802eb62c",null),j=M.exports,R=function(){var e=this,n=e.$createElement;e._self._c;return e._m(0)},$=[function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",[r("h1",[e._v("Register")]),r("hr")])}],T={data:function(){return{}},mount:function(){},methods:{}},U=T,L=(0,f.Z)(U,R,$,!1,null,"28f536b1",null),V=L.exports,N=function(){var e=this,n=e.$createElement;e._self._c;return e._m(0)},H=[function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",[r("h1",[e._v("Infos & Imprint")]),r("div",{staticStyle:{"text-align":"right"}},[r("a",{attrs:{href:"/"}},[e._v("back")])]),r("hr"),r("h2",[e._v("Infos")]),r("p",[e._v("Dieser Webauftritt wird zur Zeit noch gebaut...")]),r("h3",[e._v("Thank-Yous")]),r("p",[e._v(" Diese App entstand aus einer Idee auf dem Junggesselnabschied in den Niederlanden von Sascha."),r("br"),e._v(" Danke für das Wochenende!"),r("br"),e._v(" Und Euch, Mÿri und Sascha, alles Gute! - Und den Kids natürlich auch! ")]),r("hr"),r("h2",[e._v("Imprint")]),r("div",{staticClass:"impressum"},[r("p",[e._v("Angaben gemäß § 5 TMG")]),r("p",[e._v("Dirk Schumacher, Am Falkenberg 2, 51381 Leverkusen")]),r("p",[e._v("Vertreten durch: Dirk Schumacher")]),r("p",[e._v(" Kontakt:"),r("br"),e._v(" Telefon: 01635836637"),r("br"),e._v(" E-Mail: info@coffeeshopfinder.de ")]),r("p",[e._v(" Haftungsausschluss:"),r("br"),r("br"),e._v(" Haftung für Inhalte"),r("br"),e._v(" Die Inhalte unserer Seiten wurden mit größter Sorgfalt erstellt. Für die Richtigkeit, Vollständigkeit und Aktualität der Inhalte können wir jedoch keine Gewähr übernehmen. Als Diensteanbieter sind wir gemäß § 7 Abs.1 TMG für eigene Inhalte auf diesen Seiten nach den allgemeinen Gesetzen verantwortlich. Nach §§ 8 bis 10 TMG sind wir als Diensteanbieter jedoch nicht verpflichtet, übermittelte oder gespeicherte fremde Informationen zu überwachen oder nach Umständen zu forschen, die auf eine rechtswidrige Tätigkeit hinweisen. Verpflichtungen zur Entfernung oder Sperrung der Nutzung von Informationen nach den allgemeinen Gesetzen bleiben hiervon unberührt. Eine diesbezügliche Haftung ist jedoch erst ab dem Zeitpunkt der Kenntnis einer konkreten Rechtsverletzung möglich. Bei Bekanntwerden von entsprechenden Rechtsverletzungen werden wir diese Inhalte umgehend entfernen."),r("br"),r("br"),e._v(" Haftung für Links"),r("br"),r("br"),e._v(" Unser Angebot enthält Links zu externen Webseiten Dritter, auf deren Inhalte wir keinen Einfluss haben. Deshalb können wir für diese fremden Inhalte auch keine Gewähr übernehmen. Für die Inhalte der verlinkten Seiten ist stets der jeweilige Anbieter oder Betreiber der Seiten verantwortlich. Die verlinkten Seiten wurden zum Zeitpunkt der Verlinkung auf mögliche Rechtsverstöße überprüft. Rechtswidrige Inhalte waren zum Zeitpunkt der Verlinkung nicht erkennbar. Eine permanente inhaltliche Kontrolle der verlinkten Seiten ist jedoch ohne konkrete Anhaltspunkte einer Rechtsverletzung nicht zumutbar. Bei Bekanntwerden von Rechtsverletzungen werden wir derartige Links umgehend entfernen."),r("br"),r("br"),e._v(" Urheberrecht"),r("br"),r("br"),e._v(" Die durch die Seitenbetreiber erstellten Inhalte und Werke auf diesen Seiten unterliegen dem deutschen Urheberrecht. Die Vervielfältigung, Bearbeitung, Verbreitung und jede Art der Verwertung außerhalb der Grenzen des Urheberrechtes bedürfen der schriftlichen Zustimmung des jeweiligen Autors bzw. Erstellers. Downloads und Kopien dieser Seite sind nur für den privaten, nicht kommerziellen Gebrauch gestattet. Soweit die Inhalte auf dieser Seite nicht vom Betreiber erstellt wurden, werden die Urheberrechte Dritter beachtet. Insbesondere werden Inhalte Dritter als solche gekennzeichnet. Sollten Sie trotzdem auf eine Urheberrechtsverletzung aufmerksam werden, bitten wir um einen entsprechenden Hinweis. Bei Bekanntwerden von Rechtsverletzungen werden wir derartige Inhalte umgehend entfernen."),r("br"),r("br"),e._v(" Datenschutz"),r("br"),r("br"),e._v(" Die Nutzung unserer Webseite ist in der Regel ohne Angabe personenbezogener Daten möglich. Soweit auf unseren Seiten personenbezogene Daten (beispielsweise Name, Anschrift oder eMail-Adressen) erhoben werden, erfolgt dies, soweit möglich, stets auf freiwilliger Basis. Diese Daten werden ohne Ihre ausdrückliche Zustimmung nicht an Dritte weitergegeben. "),r("br"),e._v(" Wir weisen darauf hin, dass die Datenübertragung im Internet (z.B. bei der Kommunikation per E-Mail) Sicherheitslücken aufweisen kann. Ein lückenloser Schutz der Daten vor dem Zugriff durch Dritte ist nicht möglich. "),r("br"),e._v(" Der Nutzung von im Rahmen der Impressumspflicht veröffentlichten Kontaktdaten durch Dritte zur Übersendung von nicht ausdrücklich angeforderter Werbung und Informationsmaterialien wird hiermit ausdrücklich widersprochen. Die Betreiber der Seiten behalten sich ausdrücklich rechtliche Schritte im Falle der unverlangten Zusendung von Werbeinformationen, etwa durch Spam-Mails, vor."),r("br"),r("br"),e._v(" Google Analytics"),r("br"),r("br"),e._v(" Diese Website benutzt Google Analytics, einen Webanalysedienst der Google Inc. (''Google''). Google Analytics verwendet sog. ''Cookies'', Textdateien, die auf Ihrem Computer gespeichert werden und die eine Analyse der Benutzung der Website durch Sie ermöglicht. Die durch den Cookie erzeugten Informationen über Ihre Benutzung dieser Website (einschließlich Ihrer IP-Adresse) wird an einen Server von Google in den USA übertragen und dort gespeichert. Google wird diese Informationen benutzen, um Ihre Nutzung der Website auszuwerten, um Reports über die Websiteaktivitäten für die Websitebetreiber zusammenzustellen und um weitere mit der Websitenutzung und der Internetnutzung verbundene Dienstleistungen zu erbringen. Auch wird Google diese Informationen gegebenenfalls an Dritte übertragen, sofern dies gesetzlich vorgeschrieben oder soweit Dritte diese Daten im Auftrag von Google verarbeiten. Google wird in keinem Fall Ihre IP-Adresse mit anderen Daten der Google in Verbindung bringen. Sie können die Installation der Cookies durch eine entsprechende Einstellung Ihrer Browser Software verhindern; wir weisen Sie jedoch darauf hin, dass Sie in diesem Fall gegebenenfalls nicht sämtliche Funktionen dieser Website voll umfänglich nutzen können. Durch die Nutzung dieser Website erklären Sie sich mit der Bearbeitung der über Sie erhobenen Daten durch Google in der zuvor beschriebenen Art und Weise und zu dem zuvor benannten Zweck einverstanden."),r("br"),r("br"),e._v(" Google AdSense"),r("br"),r("br"),e._v(" Diese Website benutzt Google Adsense, einen Webanzeigendienst der Google Inc., USA (''Google''). Google Adsense verwendet sog. ''Cookies'' (Textdateien), die auf Ihrem Computer gespeichert werden und die eine Analyse der Benutzung der Website durch Sie ermöglicht. Google Adsense verwendet auch sog. ''Web Beacons'' (kleine unsichtbare Grafiken) zur Sammlung von Informationen. Durch die Verwendung des Web Beacons können einfache Aktionen wie der Besucherverkehr auf der Webseite aufgezeichnet und gesammelt werden. Die durch den Cookie und/oder Web Beacon erzeugten Informationen über Ihre Benutzung dieser Website (einschließlich Ihrer IP-Adresse) werden an einen Server von Google in den USA übertragen und dort gespeichert. Google wird diese Informationen benutzen, um Ihre Nutzung der Website im Hinblick auf die Anzeigen auszuwerten, um Reports über die Websiteaktivitäten und Anzeigen für die Websitebetreiber zusammenzustellen und um weitere mit der Websitenutzung und der Internetnutzung verbundene Dienstleistungen zu erbringen. Auch wird Google diese Informationen gegebenenfalls an Dritte übertragen, sofern dies gesetzlich vorgeschrieben oder soweit Dritte diese Daten im Auftrag von Google verarbeiten. Google wird in keinem Fall Ihre IP-Adresse mit anderen Daten der Google in Verbindung bringen. Das Speichern von Cookies auf Ihrer Festplatte und die Anzeige von Web Beacons können Sie verhindern, indem Sie in Ihren Browser-Einstellungen ''keine Cookies akzeptieren'' wählen (Im MS Internet-Explorer unter ''Extras > Internetoptionen > Datenschutz > Einstellung''; im Firefox unter ''Extras > Einstellungen > Datenschutz > Cookies''); wir weisen Sie jedoch darauf hin, dass Sie in diesem Fall gegebenenfalls nicht sämtliche Funktionen dieser Website voll umfänglich nutzen können. Durch die Nutzung dieser Website erklären Sie sich mit der Bearbeitung der über Sie erhobenen Daten durch Google in der zuvor beschriebenen Art und Weise und zu dem zuvor benannten Zweck einverstanden. ")]),r("p",[e._v(" Impressum vom "),r("a",{attrs:{href:"https://www.impressum-generator.de"}},[e._v("Impressum Generator")]),e._v(" der "),r("a",{attrs:{href:"https://www.kanzlei-hasselbach.de/"}},[e._v("Kanzlei Hasselbach, Rechtsanwälte für Arbeitsrecht und Familienrecht")])])])])}],K={data:function(){return{}},mount:function(){},methods:{}},J=K,X=(0,f.Z)(J,N,H,!1,null,"21a96e85",null),Y=X.exports;t.default.use(s.XG7),t.default.use(a.A7),t.default.use(i.Z),t.default.config.productionTip=!1;var q=[{name:"home",path:"",component:j},{name:"register",path:"/register",component:V},{name:"infos",path:"/infos",component:Y}],Q=new i.Z({mode:"history",routes:q});new t.default({router:Q,render:function(e){return e(I)}}).$mount("#app")},8472:function(e,n,r){e.exports=r.p+"img/logo.e4d34525.png"}},n={};function r(t){var i=n[t];if(void 0!==i)return i.exports;var s=n[t]={exports:{}};return e[t].call(s.exports,s,s.exports,r),s.exports}r.m=e,function(){var e=[];r.O=function(n,t,i,s){if(!t){var a=1/0;for(d=0;d<e.length;d++){t=e[d][0],i=e[d][1],s=e[d][2];for(var u=!0,o=0;o<t.length;o++)(!1&s||a>=s)&&Object.keys(r.O).every((function(e){return r.O[e](t[o])}))?t.splice(o--,1):(u=!1,s<a&&(a=s));if(u){e.splice(d--,1);var l=i();void 0!==l&&(n=l)}}return n}s=s||0;for(var d=e.length;d>0&&e[d-1][2]>s;d--)e[d]=e[d-1];e[d]=[t,i,s]}}(),function(){r.n=function(e){var n=e&&e.__esModule?function(){return e["default"]}:function(){return e};return r.d(n,{a:n}),n}}(),function(){r.d=function(e,n){for(var t in n)r.o(n,t)&&!r.o(e,t)&&Object.defineProperty(e,t,{enumerable:!0,get:n[t]})}}(),function(){r.g=function(){if("object"===typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"===typeof window)return window}}()}(),function(){r.o=function(e,n){return Object.prototype.hasOwnProperty.call(e,n)}}(),function(){r.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}}(),function(){r.p="/"}(),function(){var e={143:0};r.O.j=function(n){return 0===e[n]};var n=function(n,t){var i,s,a=t[0],u=t[1],o=t[2],l=0;if(a.some((function(n){return 0!==e[n]}))){for(i in u)r.o(u,i)&&(r.m[i]=u[i]);if(o)var d=o(r)}for(n&&n(t);l<a.length;l++)s=a[l],r.o(e,s)&&e[s]&&e[s][0](),e[a[l]]=0;return r.O(d)},t=self["webpackChunkCoffeeShopFinder"]=self["webpackChunkCoffeeShopFinder"]||[];t.forEach(n.bind(null,0)),t.push=n.bind(null,t.push.bind(t))}();var t=r.O(void 0,[998],(function(){return r(6191)}));t=r.O(t)})();
//# sourceMappingURL=app-legacy.36a39699.js.map