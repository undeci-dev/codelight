package com.project.codelight.link.dto.response;

public record LinkPreviewResponse(
    Long linkId,
    String url,
    String title,
    String description,
    String image,
    String domain
) {
    public static LinkPreviewResponse of(Long linkId,String url, String title, String description,
                                          String image, String domain) {
        return new LinkPreviewResponse(linkId, url, title, description, image, domain);
    }
}
