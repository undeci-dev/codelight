package com.project.codelight.link.service;

import com.project.codelight.link.dto.response.LinkPreviewResponse;
import java.net.URI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class LinkPreviewService {

    private static final int TIMEOUT_MS = 5000;

    public LinkPreviewResponse fetchLinkPreview(String url) {
        try {
            Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(TIMEOUT_MS)
                .followRedirects(true)
                .get();

            String title = extractTitle(document);
            String description = extractDescription(document);
            String image = extractImage(document, url);
            String domain = extractDomain(url);

            return LinkPreviewResponse.of(null, url, title, description, image, domain);
        } catch (Exception e) {
            String domain = extractDomain(url);
            return LinkPreviewResponse.of(null, url, null, null, null, domain);
        }
    }

    private String extractTitle(Document document) {
        Element ogTitle = document.selectFirst("meta[property=og:title]");
        if (ogTitle != null && !ogTitle.attr("content").isBlank()) {
            return ogTitle.attr("content");
        }

        Element twitterTitle = document.selectFirst("meta[name=twitter:title]");
        if (twitterTitle != null && !twitterTitle.attr("content").isBlank()) {
            return twitterTitle.attr("content");
        }

        String title = document.title();
        return title.isBlank() ? null : title;
    }

    private String extractDescription(Document document) {
        Element ogDescription = document.selectFirst("meta[property=og:description]");
        if (ogDescription != null && !ogDescription.attr("content").isBlank()) {
            return ogDescription.attr("content");
        }

        Element twitterDescription = document.selectFirst("meta[name=twitter:description]");
        if (twitterDescription != null && !twitterDescription.attr("content").isBlank()) {
            return twitterDescription.attr("content");
        }

        Element metaDescription = document.selectFirst("meta[name=description]");
        if (metaDescription != null && !metaDescription.attr("content").isBlank()) {
            return metaDescription.attr("content");
        }

        return null;
    }

    private String extractImage(Document document, String baseUrl) {
        Element ogImage = document.selectFirst("meta[property=og:image]");
        if (ogImage != null && !ogImage.attr("content").isBlank()) {
            return resolveUrl(ogImage.attr("content"), baseUrl);
        }

        Element twitterImage = document.selectFirst("meta[name=twitter:image]");
        if (twitterImage != null && !twitterImage.attr("content").isBlank()) {
            return resolveUrl(twitterImage.attr("content"), baseUrl);
        }

        return null;
    }

    private String extractDomain(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveUrl(String imageUrl, String baseUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        // 이미 절대 URL인 경우
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // 프로토콜 상대 URL인 경우 (//example.com/image.png)
        if (imageUrl.startsWith("//")) {
            return "https:" + imageUrl;
        }

        // 상대 URL인 경우
        try {
            URI base = URI.create(baseUrl);
            return base.resolve(imageUrl).toString();
        } catch (Exception e) {
            return imageUrl;
        }
    }
}
