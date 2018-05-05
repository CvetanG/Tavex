package app.samples;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class Example5Main {
	
	static String htmlText = "<!DOCTYPE html>" +
			"<html>" +
			"<head>" +
			"<title>Java Magazine</title>" +
			"</head>" +
			"<body>" +
			"<h1>Hello World!</h1>" +
			"</body>" +
			"</html>";
	
	public static void main(String... args) throws IOException {
		Document document = Jsoup.parse(htmlText);
		document.traverse(new NodeVisitor() {
			public void head(Node node, int depth){
				System.out.println("Node start: "
					+ node.nodeName());
				}
			public void tail(Node node, int depth){
				System.out.println("Node end: " +
						node.nodeName());
			}
		});
	}
}
