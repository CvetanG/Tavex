package app.histdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.poi.util.IOUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Oanda extends BaseMarketData{
	
	private static final double EUR_BGN = 1.956;
	public static final BasicHeader UNIX_DATETIME_HEADER = new BasicHeader("X-Accept-Datetime-Format", "UNIX");
	
	private static final String instrument = "EUR_USD";

	public Oanda(int histPeriod, String index) {
		super(histPeriod, index);
	}

	// https://api-fxpractice.oanda.com/v3/instruments/EUR_USD/candles?price=M&&granularity=Dto=2018-07-28T00%3A00%3A00.000000000Z&count=100
	String createUrl(String instrument, DateTime dateTime) {
		return String.format(
				"%s/%s/candles?price=M&granularity=D&to=%s&count=%d",
				"https://api-fxpractice.oanda.com/v3/instruments", instrument, 
				dateTime.toString(), period);
	}
	
	@Override
	public void getLastData() {
		File file = new File(instrument + "_dataList.json");
		this.periodList = new ArrayList<OHLC>();

		BufferedReader rd;
		JsonArray data =  null;
		String line;
		FileReader fr;

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		StringBuilder sb = new StringBuilder();
		
		// check for file last updated
		int minutes = 3;
		int seconds = minutes * 60;
		long backupPeriodMillis = (seconds * 1000);
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + backupPeriodMillis) > millis;

		if (file.exists() && check) {
			try {
				fr = new FileReader(file);
				rd = new BufferedReader(fr);
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				data = parser.parse(sb.toString()).getAsJsonArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				data = downloadData(file, parser);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		if (data.size() < period) {
			try {
				data = downloadData(file, parser);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < data.size(); i++) {
			this.periodList.add(jsonElementToOHLC(gson, data.get(i)));
		}
		this.lastPrice =  this.periodList.get(period - 1).getClose();
		this.lastOpen =  this.periodList.get(period - 1).getOpen();

	}

	private JsonArray downloadData(File file, JsonParser parser) throws IOException, ClientProtocolException {
		JsonArray data;
		System.out.println("... Downloading New Data From " + this.getClass().getSimpleName());
		DateTime dt = DateTime.now();
		String finalUrl = createUrl(instrument, dt);
		
		CloseableHttpClient httpClient = getHttpClient();
		HttpUriRequest httpGet = new HttpGet(finalUrl);
		httpGet.setHeader(UNIX_DATETIME_HEADER);
		httpGet.setHeader(createAuthHeader());
		HttpResponse resp = httpClient.execute(httpGet);
		String strResp = responseToString(resp);
			
		JsonObject json = parser.parse(strResp).getAsJsonObject();
		data = json.getAsJsonArray(OandaJsonKeys.candles);

		FileWriter  fw = new FileWriter(file, false);

		fw.write(data.toString());
		fw.close();
		return data;
	}
	
	private OHLC jsonElementToOHLC(Gson gson, JsonElement jsonElement) {
		OHLC result = new OHLC();
		
		result.setDate(jsonElement.getAsJsonObject().get(OandaJsonKeys.time).toString());
		JsonElement prices = jsonElement.getAsJsonObject().get(OandaJsonKeys.mid);
		
		double open = prices.getAsJsonObject().get(OandaJsonKeys.open).getAsDouble();
		double high = prices.getAsJsonObject().get(OandaJsonKeys.high).getAsDouble();
		double low =  prices.getAsJsonObject().get(OandaJsonKeys.low).getAsDouble();
		double close =  prices.getAsJsonObject().get(OandaJsonKeys.close).getAsDouble();
		
		// Convert to BGNUSD
		result.setOpen(EUR_BGN / open);
		result.setHigh(EUR_BGN / high);
		result.setLow(EUR_BGN / low);
		result.setClose(EUR_BGN / close);
		
		return result;
	}
	
	public static CloseableHttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}
	
	public static final String responseToString(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
				&& entity != null) {
			InputStream stream = entity.getContent();
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			StringBuilder strResp = new StringBuilder();
			while ((line = br.readLine()) != null) {
				strResp.append(line);
			}
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(br);
			return strResp.toString();
		} else {
			return "";
		}
	}
	
	public static final BasicHeader createAuthHeader() throws IOException {
		String filename = "oanda_token.txt";
		File file = new File(filename);
		String line;
		FileReader fr = new FileReader(file);
		BufferedReader rd = new BufferedReader(fr);

		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		return new BasicHeader("Authorization", "Bearer " + sb.toString());
	}
	
	// Test
	public static void main(String[] args) {
		String index = "BGNUSD";
		Oanda o20 = new Oanda(20, index);
		o20.init();
	}

}
