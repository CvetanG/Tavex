package app.samples;

import java.io.IOException;

import app.histdata.Myfxbook;

public class Samples {
	public static void main(String[] args) throws IOException {
//		InvestmentParser myParser = new InvestmentParser();
//		myParser.getEthereumPrice();
//		myParser.getBitcoinPrice();
		
		Myfxbook fx10 = new Myfxbook(10);
		fx10.init();
		Myfxbook fx20 = new Myfxbook(20);
		fx20.init();
		Myfxbook fx50 = new Myfxbook(50);
		fx50.init();
	}
}
