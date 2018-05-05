package app.entities;

public enum IndexesEnum {
	DEFAULT("default"),
	GOLD_COIN("gold_coin"),
	GOLD("gold"),
	USD("usd"),
	XAUUSD("xaousd"),
	CRYPTO("crypto");
	
	private final String text;

    private IndexesEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
