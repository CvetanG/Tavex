package app.entities;

public enum CurrenciesEnum {
	BGN("BGN"),
	USD("USD");
	
	private final String text;

    private CurrenciesEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
