package app.entities;

public class RowEntry {
	
	String index;
	IndexesEnum indexType;
	CurrenciesEnum currency;
	String buy;
	String priceDown;
	String sell;
	String priceOver;
	String diffPerc;
	String diff;
	Boolean underline;
	
	public RowEntry(String index, IndexesEnum indexType, CurrenciesEnum currency, String buy,
			String priceDown, String sell, String priceOver,
			String diffPerc, String diff, Boolean underline) {
		this.index = index;
		this.indexType = indexType;
		this.currency = currency;
		this.buy = buy;
		this.priceDown = priceDown;
		this.sell = sell;
		this.priceOver = priceOver;
		this.diffPerc = diffPerc;
		this.diff = diff;
		this.underline = underline;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	public IndexesEnum getIndexType() {
		return indexType;
	}

	public void setIndexType(IndexesEnum indexType) {
		this.indexType = indexType;
	}

	public CurrenciesEnum getCurrency() {
		return currency;
	}

	public void setCurrency(CurrenciesEnum currency) {
		this.currency = currency;
	}

	public CurrenciesEnum getCurruncy() {
		return currency;
	}

	public void setCurruncy(CurrenciesEnum currency) {
		this.currency = currency;
	}

	public String getPriceDown() {
		return priceDown;
	}

	public void setPriceDown(String priceDown) {
		this.priceDown = priceDown;
	}

	public String getPriceOver() {
		return priceOver;
	}

	public void setPriceOver(String priceOver) {
		this.priceOver = priceOver;
	}

	public String getDiffPerc() {
		return diffPerc;
	}

	public void setDiffPerc(String diffPerc) {
		this.diffPerc = diffPerc;
	}

	public String getBuy() {
		return buy;
	}

	public void setBuy(String buy) {
		this.buy = buy;
	}

	public String getSell() {
		return sell;
	}

	public void setSell(String sell) {
		this.sell = sell;
	}

	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}

	public Boolean getUnderline() {
		return underline;
	}

	public void setUnderline(Boolean underline) {
		this.underline = underline;
	}
	
	@Override
	public String toString() { 
		String temp = "";
		if (this.diff != null) {
			temp = ",\nFix: " + this.diff;
		}
		String result = "Index: " + this.index + ",\n" +
				"Buy: " + this.buy + ",\n" +
				"Sell: " + this.sell +
				temp;
				
	    return result;
	} 
	
}
