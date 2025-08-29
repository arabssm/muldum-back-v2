package co.kr.muldum.domain.item.model.enums;

public enum ItemSource {
    DEVICEMART,
    ELEVENMARKET,
    OTHER;

    public static ItemSource fromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return OTHER;
        }
        
        String lowerUrl = url.toLowerCase();
        
        if (lowerUrl.contains("devicemart.co.kr")) {
            return DEVICEMART;
        } else if (lowerUrl.contains("11st.co.kr")) {
            return ELEVENMARKET;
        }
        
        return OTHER;
    }
}