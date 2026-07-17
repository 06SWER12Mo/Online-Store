package com.example.demo.image.util;

public final class ImageConstants {

    private ImageConstants() {}

    // ========== BASE PATHS ==========
    public static final String BASE_PATH = "C:\\Users\\Dell\\OneDrive\\Desktop\\store-images";
    
    // ========== FOLDER NAMES ==========
    public static final String STORE_FOLDER = "store";
    public static final String USERS_FOLDER = "users";
    public static final String PRODUCTS_FOLDER = "products";
    public static final String PRODUCT_VARIANTS_FOLDER = "product-variants";
    public static final String CATEGORIES_FOLDER = "categories";
    public static final String SUBCATEGORIES_FOLDER = "subcategories";
    
    // ========== FILE NAMES ==========
    public static final String STORE_LOGO = "logo.png";
    public static final String STORE_FAVICON = "favicon.ico";
    
    // ========== URL PATHS ==========
    public static final String IMAGE_URL_PREFIX = "/api/images/";
    
    // ========== DEFAULT IMAGES ==========
    public static final String DEFAULT_PRODUCT_IMAGE = "/images/default-product.png";
    public static final String DEFAULT_USER_AVATAR = "/images/default-avatar.png";
    public static final String DEFAULT_CATEGORY_IMAGE = "/images/default-category.png";
    public static final String DEFAULT_SUBCATEGORY_IMAGE = "/images/default-subcategory.png";  // ✅ Add this
    public static final String DEFAULT_VARIANT_IMAGE = "/images/default-variant.png";
    public static final String DEFAULT_STORE_LOGO = "/images/default-store-logo.png";
    public static final String DEFAULT_STORE_FAVICON = "/images/default-favicon.ico";
    
    // ========== FILE TYPES ==========
    public static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp", "bmp", "ico"};
    public static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp", "image/x-icon"};
    
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    // ========== IMAGE TYPES ==========
    public static final String IMAGE_TYPE_MAIN = "MAIN";
    public static final String IMAGE_TYPE_GALLERY = "GALLERY";
    public static final String IMAGE_TYPE_THUMBNAIL = "THUMBNAIL";
    public static final String IMAGE_TYPE_AVATAR = "AVATAR";
    public static final String IMAGE_TYPE_VARIANT = "VARIANT";
    public static final String IMAGE_TYPE_CATEGORY = "CATEGORY";
    public static final String IMAGE_TYPE_SUBCATEGORY = "SUBCATEGORY";
    public static final String IMAGE_TYPE_LOGO = "LOGO";
    public static final String IMAGE_TYPE_FAVICON = "FAVICON";
    
    // ========== ENTITY TYPES ==========
    public static final String ENTITY_PRODUCT = "product";
    public static final String ENTITY_VARIANT = "variant";
    public static final String ENTITY_CATEGORY = "category";
    public static final String ENTITY_SUBCATEGORY = "subcategory";
    public static final String ENTITY_USER = "user";
    public static final String ENTITY_STORE = "store";
}