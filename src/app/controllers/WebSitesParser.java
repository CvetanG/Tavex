package app.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.entities.CurrenciesEnum;
import app.entities.IndexesEnum;
import app.entities.RowEntry;
import app.entities.Utils;
import app.histdata.Oanda;
import app.histdata.OandaJsonKeys;

public class WebSitesParser {
	
	private static final int TIMEOUT = 50000;
	private static WebSitesParser instance;
	
	private static String divProdClass = "product__content";
	private static String divPriceClass = "product__price";
	private static String divGoldClass = "chart__value";
    
	public WebSitesParser(){
		
	}
    
    public static WebSitesParser getInstance(){
        if(instance == null){
            instance = new WebSitesParser();
        }
        return instance;
    }
	
	
	// Using this in BGNUSD
//	public static String replaceCurr(String curr) {
//		curr = curr.replace(".", ",");
//		return curr;
//	}
	
	public static List<RowEntry> getCoinsFromTavex(List<String> myCoinsStrings) throws IOException {

		List<RowEntry> myCoinRowEntries = new ArrayList<RowEntry>();
		
		String httpsURL = "https://www.tavex.bg/zlato";
		
	        URL myUrl = new URL(httpsURL);
	        HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
	        
	        
	        InputStream is = conn.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);

	        String inputLine;

	        while ((inputLine = br.readLine()) != null) {
	            System.out.println(inputLine);
	        }

	        br.close();
	        Document doc = new Document(inputLine);
	        
//		Document doc = Jsoup.connect(myUrl)
//				.timeout(timeout).validateTLSCertificates(false)
//				.get();
		
		Elements paginations = doc.getElementsByClass("pagination__item");
		
		int i = 0;
		for (Element element : paginations) {
			i ++;
		}
		
		for (int j = 1; j < i; j++) {
			Document tavex = Jsoup.connect(myUrl + "/page/" + j + "/#s")
					.timeout(TIMEOUT).validateTLSCertificates(false)
					.get();
		
			Elements allElements = tavex.getElementsByClass(divProdClass);
			
			// Iterate in arraylist
			for (String myCoinString : myCoinsStrings) {
				// Iterate in coin div
				for (Element element : allElements) {
					if (myCoinString.equals(element.child(0).ownText())) {
						// element.nextElementSibling()!=null
						// ? element.nextElementSibling()
						// .ownText()
						// : "")) {
						String curCoin = element.child(0).ownText();
						System.out.println(curCoin);
						String htmlurElement = element.toString();
						Document document = Jsoup.parse(htmlurElement);
						
						Elements myElements = document.getElementsByClass(divPriceClass);
						
						String result_01 = Utils.clearFormatCurr(myElements.get(0).child(1).child(0).ownText());
						System.out.println("Buy: " + result_01);
						String result_02 = Utils.clearFormatCurr(myElements.get(0).child(0).ownText());
						System.out.println("Sell: " + result_02);
							
						RowEntry rowEtry = new RowEntry(
								curCoin,
								IndexesEnum.GOLD_COIN,
								CurrenciesEnum.BGN,
								result_01,
								null,
								result_02,
								null,
								null,
								null,
								false);
							
						myCoinRowEntries.add(rowEtry);
					}
					
				}
			}
		}
		return myCoinRowEntries;
	}
	
	// Not using this method
	public void getGoldFromTavex() throws IOException {
		String myUrl = "https://www.tavex.bg/zlato/#charts-modal";

		Document tavex = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();

		Elements allElements = tavex.getElementsByClass(divGoldClass);

		for (Element element : allElements) {
			System.out.println("Gold: " + element.child(0).ownText());
		}

	}
	
	public static RowEntry getBGNUSD() throws IOException {
//		String myUrl = "https://ebb.ubb.bg/Log.aspx";
		String myUrl = "https://ebb.ubb.bg/ebank/Log.aspx";

		Document doc = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
		
		Element div = doc.getElementById("currency1").getElementsByTag("tr").get(5);
		
//		String result_01 = replaceCurr(div.child(3).ownText());
		String result_01 = div.child(3).ownText();
		System.out.println("USD Buy:: " + result_01);
		
//		String result_02 = replaceCurr(div.child(4).ownText());
		String result_02 = div.child(4).ownText();
		System.out.println("USD Sell: " + result_02);
		
//		String result_03 = replaceCurr(div.child(2).ownText());
		String result_03 = div.child(2).ownText();
		System.out.println("USD Fixture: " + result_03);
		
		RowEntry rowEntry = new RowEntry(
				"Щатски долар",
				IndexesEnum.USD,
				CurrenciesEnum.BGN,
				result_01,
				null,
				result_02,
				null,
				null,
				result_03,
				false);
		
		return rowEntry;

	}
	
	/*
	public RowEntry getXAUUSD() throws IOException {
		String myUrl = "https://www.bloomberg.com/quote/XAUUSD:CUR";

		Document doc = Jsoup.connect(myUrl)
				.timeout(timeout).validateTLSCertificates(false)
				.get();
		
//		Elements div = doc.getElementsByClass("price");
		Elements div = doc.getElementsByClass("value__b93f12ea");
		
//		System.out.println(div.get(0).ownText());
		String result = Utils.currencyFormater(div.get(0).ownText());
		System.out.println("XAU_USD:: " + result);
		
		RowEntry rowEntry = new RowEntry(
				"XAUUSD:CUR",
				IndexesEnum.XAUUSD,
				CurrenciesEnum.USD,
				null,
				null,
				null,
				null,
				"open",
				result,
				false);
		
		return rowEntry;
		
	}
	*/
	
	public static RowEntry getXAUUSD() throws IOException {
		
		String finalUrl = "https://api-fxpractice.oanda.com/v3/accounts/101-004-8512520-001/pricing?instruments=XAU_USD";
		
		CloseableHttpClient httpClient = Oanda.getHttpClient();
		HttpUriRequest httpGet = new HttpGet(finalUrl);
		httpGet.setHeader(Oanda.UNIX_DATETIME_HEADER);
		httpGet.setHeader(Oanda.createAuthHeader());
		HttpResponse resp = httpClient.execute(httpGet);
		String strResp = Oanda.responseToString(resp);
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(strResp).getAsJsonObject();
		JsonArray data = json.getAsJsonArray(OandaJsonKeys.prices);
		JsonObject prices = data.get(0).getAsJsonObject();
		String result = prices.get(OandaJsonKeys.closeoutAsk).getAsString();
		
		RowEntry rowEntry = new RowEntry(
				"XAUUSD:CUR",
				IndexesEnum.XAUUSD,
				CurrenciesEnum.USD,
				null,
				null,
				null,
				null,
				"open",
				result,
				false);
		
		return rowEntry;
	}
	
	public static RowEntry getXAUBGN() throws IOException {
		String myUrl = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/index.htm";

		Document doc = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
		
		Element div = (Element) doc.getElementsByClass("table").get(0).childNode(4).childNode(63).childNode(7);
		
//		System.out.println(div.ownText());
		String result = Utils.currencyFormater(div.ownText());
		System.out.println("XAU_BGN: " + result);
		
		RowEntry rowEntry = new RowEntry(
				"Злато (в трой унции)",
				IndexesEnum.GOLD,
				CurrenciesEnum.BGN,
				"XAU",
				null,
				"1.00",
				null,
				null,
				result,
				false);
		
		return rowEntry;
	}
	
	public static RowEntry getEthereumPrice() throws IOException {
		String crypto = "ethereum";
		return WebSitesParser.getCryptoCurrency(crypto);
	}
	
	public static RowEntry getBitcoinPrice() throws IOException {
		String crypto = "bitcoin";
		return WebSitesParser.getCryptoCurrency(crypto);
	}
	
	public static RowEntry getMoneroPrice() throws IOException {
		String crypto = "monero";
		return WebSitesParser.getCryptoCurrency(crypto);
	}
	
	public static RowEntry getDogePrice() throws IOException {
		String crypto = "dogecoin";
		return WebSitesParser.getCryptoCurrency(crypto);
	}
	
	private static RowEntry getCryptoCurrency(String crypto) throws IOException {
		String myUrl = "https://coinmarketcap.com/currencies/" + crypto + "/";
		
		Document doc = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
		
//		Element prUSD = (Element) doc.getElementsByClass("col-xs-6 col-sm-8 col-md-4 text-left").get(0).childNode(1);
//		Element prUSD = (Element) doc.getElementById("quote_price");
		Element prUSD = (Element) doc.getElementsByClass("cmc-details-panel-price__price").get(0);
		
//		System.out.println(prUSD.ownText());
		String priceUSD;
		if ("dogecoin".equals(crypto)) {
			priceUSD = Utils.clearFormatCurr1000(prUSD.text());
		} else {
			priceUSD = Utils.clearFormatCurr(prUSD.text());
		}
		System.out.println(crypto.toUpperCase() + " USD: " + priceUSD);
		
//		Element prBTC = (Element) doc.getElementsByClass("col-xs-6 col-sm-8 col-md-4 text-left").get(0).childNode(7);
		Element prBTC = (Element) doc.getElementsByClass("cmc-details-panel-price__crypto-price").get(0);
		
//		System.out.println(prBTC.ownText());
		String priceBTC = prBTC.text();
		System.out.println(crypto.toUpperCase() + " " + priceBTC);
		
		priceBTC = priceBTC.substring(0, priceBTC.length()-4);
		
		String indexName;
		Boolean underline;
		
		switch (crypto) {
        case "ethereum":  indexName = "Ethereum Price";
        					underline = false;
                 break;
        case "monero":  indexName = "Monero Price";
        					underline = false;
        break;
        case "dogecoin":  indexName = "Dogecoin Price x 1000";
					        priceBTC = String.valueOf(Double.parseDouble(priceBTC) * 1000);
        					underline = false;
        break;
        case "bitcoin":  indexName = "Bitcoin Price";
							underline = true;
                 break;
        default: throw new RuntimeException("Invalid Crypto Curruncy");
		}
			
		RowEntry rowEntry = new RowEntry(
				indexName,
				IndexesEnum.CRYPTO,
				CurrenciesEnum.USD,
				priceBTC,
				"BTC",
				null,
				null,
				null,
				priceUSD,
				underline);
		
		return rowEntry;
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Start Program");
		long startTime = System.currentTimeMillis();
		
		
		List<String> myCoinsStrings = new ArrayList<>();
		myCoinsStrings.add("1 унция златен канадски кленов лист");
//		myCoinsStrings.add("1 унция златна австрийска филхармония");
		myCoinsStrings.add("5 грама златно кюлче PAMP Фортуна");
		myCoinsStrings.add("1 унция златна китайска панда от 2009");

//		WebSitesParser.getCoinsFromTavex(myCoinsStrings);
//		getBGNUSD();
		
//		RowEntry rowEntryXAUUSD = parser.getXAUUSD();
//		System.out.println(rowEntryXAUUSD.toString());
		
//		getXAUBGN();
		
//		getEthereumPrice();
		getDogePrice();
		
		/*
		RowEntry rowEntry_02 = new RowEntry(
				"Канадски кленов лист 1 унция",
				IndexesEnum.DEFAULT,
				CurrenciesEnum.BGN,
				"2,120.00",
				null,
				"2,279.00",
				null,
				null,
				"123",
				false);
		
		System.out.println(rowEntry_02.toString());
		*/
		long endTime   = System.currentTimeMillis();
		Utils.duration(startTime, endTime);
		
	}

}
