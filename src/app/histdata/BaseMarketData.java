package app.histdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public abstract class BaseMarketData {
	
	public int histPeriod;
	public List<OHLC> finalList;
	public double curPrice;
	public double lastTR;
	public double lastMIN;
	public double lastMAX;
	
	public String index;
	
	public BaseMarketData(int histPeriod, String index) {
		this.histPeriod = histPeriod;
		this.index = index;
	}

	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		print();
	}
	
	public abstract void getLastData();
	
	public void calculateTR() {
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

	public void calculateMinMax() {
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
	
	private double calcPercent(double price, double curPrice) {
		double pers = (price * 100.0f) / curPrice;
		return -(100.0 - pers);
	}
	
	public void print() {
		double b = 1.7021;
		System.out.println("***** TRADING INFO *****");
		System.out.println("Data info for " + (this.finalList.size()) + " day/s period.");
		System.out.println(String.format("Average TR: %.5f", this.lastTR));
		System.out.println(String.format("Current Price: %.5f lev", this.curPrice));
		System.out.println(String.format("Min %s: %.5f lev/%5.2f%s", index, this.lastMIN, calcPercent(this.lastMIN, this.curPrice), "%"));
		System.out.println(String.format("Max %s: %.5f lev/%5.2f%s", index, this.lastMAX, calcPercent(this.lastMAX, this.curPrice), "%"));
		System.out.println(String.format("Bought  %.5f lev/%5.2f%s", b , calcPercent(this.curPrice, b), "%"));
	}
	
	public List<OHLC> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.finalList = finalList;
	}
	
}
