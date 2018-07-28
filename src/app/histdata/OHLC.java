package app.histdata;

public class OHLC {
	String date;
	double open;
	double high;
	double low;
	double close;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	@Override
	public String toString() {
		return "OHLC: " + date + " " + open + " " +  high + " " +  low + " " +  close;
	}
	
	
}
