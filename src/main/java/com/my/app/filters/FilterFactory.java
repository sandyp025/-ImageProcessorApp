package com.my.app.filters;

import com.my.app.filters.impl.*;
import java.util.*;

public class FilterFactory {
    private static final Map<String, ImageFilter> FILTER_REGISTRY = new LinkedHashMap<>();
    
    static {
        FILTER_REGISTRY.put("Grayscale", new GreyScaleFilter());
        FILTER_REGISTRY.put("Sepia", new SepiaFilter());
        FILTER_REGISTRY.put("Blur", new BlurFilter());
        FILTER_REGISTRY.put("Sharpen", new SharpenFilter());
        FILTER_REGISTRY.put("Edge Detection", new EdgeDetectionFilter());
        FILTER_REGISTRY.put("Emboss", new EmbossFilter());
        FILTER_REGISTRY.put("Vintage", new VintageFilter());
        FILTER_REGISTRY.put("Color Boost", new ColorBoostFilter());
    }
    
    public static Map<String, ImageFilter> getAllFilters() {
        return new LinkedHashMap<>(FILTER_REGISTRY);
    }
    
    public static ImageFilter getFilter(String name) {
        return FILTER_REGISTRY.get(name);
    }
    
    public static Set<String> getFilterNames() {
        return FILTER_REGISTRY.keySet();
    }
    
    public static boolean hasFilter(String name) {
        return FILTER_REGISTRY.containsKey(name);
    }
}