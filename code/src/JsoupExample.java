
// Jsoup Example

import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class JsoupExample {

    public static void main(String[] args) {
        // 東京電機大学 - Wikipedia
        String url = "https://ja.wikipedia.org/wiki/%E6%9D%B1%E4%BA%AC%E9%9B%BB%E6%A9%9F%E5%A4%A7%E5%AD%A6";

        try {
            Connection conn = Jsoup.connect(url);
            Document doc = conn.get();

            // select the content text and pull out the paragraphs.
            Elements paragraphs = doc.select("#mw-content-text p");

            for (Element p : paragraphs) {
                System.out.println(p.text());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
