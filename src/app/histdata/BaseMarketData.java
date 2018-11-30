package app.histdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public abstract class BaseMarketData {
	
	public int period;
	public List<OHLC> periodList;
	public double lastPrice;
	public double lastOpen;
	public double periodTR;
	public double periodMIN;
	public double periodMAX;
	
	public String pair;
	
	public BaseMarketData(int period, String pair) {
		this.period = period;
		this.pair = pair;
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
		
		for (int i = 1; i < this.periodList.size(); i++) {
			TR1 = Math.abs(this.periodList.get(i).getHigh() - this.periodList.get(i).getLow());
			TR2 = Math.abs(this.periodList.get(i - 1).getClose() - this.periodList.get(i).getHigh());
			TR3 = Math.abs(this.periodList.get(i - 1).getClose() - this.periodList.get(i).getLow());

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
		this.periodTR =  sum.doubleValue() / listTRMax.size();

	}

	public void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.periodList.size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.periodList.size(), Collections.reverseOrder());
		
		pqMin.add(this.lastPrice);
		pqMax.add(this.lastPrice);
		
		for (OHLC element : this.periodList) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}

		this.periodMIN = pqMin.peek();
		this.periodMAX = pqMax.peek();;

	}
	
	private String calcPercent(double a, double b) {
		double pers = (a * 100.0f) / b;
		if (pers > 100.0) {
			return String.format("(+%.2f%s)", -(100.0 - pers), "%");
		} else {
			return String.format("(%.2f%s)", -(100.0 - pers), "%");
		}
	}
	
	public void print() {
		double b = 1.7021;
		System.out.println("***** TRADING INFO *****");
		System.out.println("Data info for " + (this.periodList.size()) + " day/s period.");
		System.out.println(String.format("Average TR: %.5f", this.periodTR));
		System.out.println(String.format("Curr Price: %.5f lev %s", this.lastPrice, calcPercent(this.lastOpen, this.lastPrice)));
		System.out.println(String.format("Min %s: %.5f lev %s", pair, this.periodMIN, calcPercent(this.periodMIN, this.lastPrice)));
		System.out.println(String.format("Max %s: %.5f lev %s", pair, this.periodMAX, calcPercent(this.periodMAX, this.lastPrice)));
		System.out.println(String.format("    Bought: %.5f lev %s", b , calcPercent(this.lastPrice, b)));
	}
	
	public List<OHLC> getFinalList() {
		return periodList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.periodList = finalList;
	}
	
}
