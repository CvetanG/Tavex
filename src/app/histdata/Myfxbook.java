package app.histdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class Myfxbook {
	public static final String STR_URL = "https://www.myfxbook.com/forex-market/currencies/EURUSD-historical-data";
	public static final double EUR_BGN = 1.956;
	private static final int timeout = 50000;
	
	public int histPeriod;
	public List<OHLC> finalList;
	public double curPrice;
	public double lastTR;
	public double lastMIN;
	public double lastMAX;

	public Myfxbook(int histPeriod) {
		this.histPeriod = histPeriod;
	}

	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		print();
	}

	private void getLastData() {
		String subElement = "EURUSD";
		File file = new File(subElement + "_dataList.json");
		this.finalList = new ArrayList<OHLC>();

		BufferedReader rd;
		JsonArray data =  null;
		String line;
		FileReader fr;

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		StringBuilder sb = new StringBuilder();
		
		// check for file last updated
		int minute = 30;
		int periodMinutes = minute * 60 * 5;
		long OneMinuteMillis = (periodMinutes * 1000)  * 60;
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + OneMinuteMillis) > millis;

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
				System.out.println("... Downloading New Data From myfxbook");
				
				Document doc = Jsoup.connect(STR_URL)
						.timeout(timeout).validateTLSCertificates(false)
						.get();
				
				Element table = doc.getElementById("symbolMarket");
				String curPrice = doc.getElementById("symbolHistoryBid").text();
				data = tableToJSON(table, curPrice);
				System.out.println();
				FileWriter  fw = new FileWriter(file, false);

				fw.write(data.toString());
				fw.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		for (int i = 0; i < histPeriod; i++) {
			this.finalList.add(jsonElementToOHLC(gson, data.get(i)));
		}
		this.curPrice =  jsonElementToOHLC(gson, data.get(100)).getClose();

	}

	private OHLC jsonElementToOHLC(Gson gson, JsonElement jsonElement) {
		OHLC result = new OHLC();

		List<String> yourList = gson.fromJson(jsonElement, new TypeToken<List<String>>(){}.getType());

		result.setDate(yourList.get(0));
		
		// Convert to BGNUSD
		result.setOpen(EUR_BGN / Double.parseDouble(yourList.get(1)));
		result.setHigh(EUR_BGN / Double.parseDouble(yourList.get(2)));
		result.setLow(EUR_BGN / Double.parseDouble(yourList.get(3)));
		result.setClose(EUR_BGN / Double.parseDouble(yourList.get(4)));
		
		// Keep EURUSD
//		result.setOpen(Double.parseDouble(yourList.get(1)));
//		result.setHigh(Double.parseDouble(yourList.get(2)));
//		result.setLow(Double.parseDouble(yourList.get(3)));
//		result.setClose(Double.parseDouble(yourList.get(4)));
		
		result.setChangePip(yourList.get(5));
		result.setChangeProcent(yourList.get(6));
		return result;
	}

	private void calculateTR() {
		// TR1 today H/L
		double TR1;
		// TR2 yest close today H
		double TR2;
		// TR3 yest close today L
		double TR3;
		List<Double> listTRMax = new ArrayList<>();
		PriorityQueue<Double> pq;
		
		for (int i = 1; i < this.finalList.size(); i++) {
			TR1 = Math.abs(this.finalList.get(i).getHigh() - this.finalList.get(i).getLow());
			TR2 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getHigh());
			TR3 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getLow());

			pq = new PriorityQueue<>(3, Collections.reverseOrder());
			pq.add(TR1);
			pq.add(TR2);
			pq.add(TR3);
			listTRMax.add(pq.peek());
		}

		Double sum = 0.0;
		for (Double TRMax: listTRMax) {
			sum += TRMax;
		}
		this.lastTR =  sum.doubleValue() / listTRMax.size();

	}

	private void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.finalList.size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.finalList.size(), Collections.reverseOrder());
		
		pqMin.add(this.curPrice);
		pqMax.add(this.curPrice);
		
		for (OHLC element : this.finalList) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}

		this.lastMIN = pqMin.peek();
		this.lastMAX = pqMax.peek();;

	}

	private void print() {
		System.out.println("***** TRADING INFO *****");
		System.out.println("Data info for " + (this.finalList.size()) + " day/s period.");
		System.out.println(String.format("Average TR: %.5f", this.lastTR));
		System.out.println(String.format("Current Price: %.5f lev", this.curPrice));
		System.out.println(String.format("Min XMRUSD: %.5f lev/%.5f", this.lastMIN, this.curPrice - this.lastMIN));
		System.out.println(String.format("Max XMRUSD: %.5f lev/%.5f", this.lastMAX, this.curPrice - this.lastMAX));
	}
	
	private JsonArray tableToJSON(Element table, String curPrice) {
		JsonArray finalJsonArray = new JsonArray();
		
		Elements tRow = table.getElementsByTag("tr");

		//skip the first row because there are the column names 
		for (int i = 1; i < tRow.size(); i++) {
			JsonArray jArrRow = new JsonArray();
			Elements tData = tRow.get(i).getElementsByTag("td");
			for (int j = 0; j < tData.size(); j++) {
				jArrRow.add(new JsonPrimitive(tData.get(j).text()));
			}
			finalJsonArray.add(jArrRow);
		}
		
		JsonArray jArrCPrice = new JsonArray();
		jArrCPrice.add(0);
		jArrCPrice.add(0);
		jArrCPrice.add(0); // hight
		jArrCPrice.add(0); // low
		
		jArrCPrice.add(curPrice);
		jArrCPrice.add(0);
		jArrCPrice.add(0);
		
		// add to 100 place
		finalJsonArray.add(jArrCPrice);
		
		return finalJsonArray;
	}
	
	public List<OHLC> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.finalList = finalList;
	}
	
	// Test
	public static void main(String[] args) {
		Myfxbook fx20 = new Myfxbook(20);
		fx20.init();
	}

}
