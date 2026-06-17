package vn.edu.nhom8.ui;

import java.awt.*;

//Lưu trữ tất cả màu sắc, font chữ
//các hằng số giao diện để toàn bộ hệ thống sử dụng chung.
public class UITheme {

    //Màu nền 
    public static final Color NAVY        = new Color(15,  31,  61);   // topbar chính
    public static final Color NAVY2       = new Color(22,  40,  71);   // toolbar
    public static final Color NAVY3       = new Color(30,  53,  96);   // accent tối
    public static final Color BG_PAGE     = new Color(241, 245, 249);  // nền trang
    public static final Color BG_CARD     = Color.WHITE;
    public static final Color BG_INPUT    = new Color(248, 250, 252);

    //Màu chính
    public static final Color BLUE        = new Color(37,  99,  235);
    public static final Color BLUE_LIGHT  = new Color(59,  130, 246);
    public static final Color BLUE_PALE   = new Color(219, 234, 254);

    //Màu semantic
    public static final Color GREEN       = new Color(22,  163, 74);
    public static final Color GREEN_PALE  = new Color(220, 252, 231);
    public static final Color AMBER       = new Color(217, 119, 6);
    public static final Color AMBER_PALE  = new Color(254, 243, 199);
    public static final Color RED         = new Color(220, 38,  38);
    public static final Color RED_PALE    = new Color(254, 226, 226);
    public static final Color TEAL        = new Color(8,   145, 178);

    //Màu chữ
    public static final Color TEXT        = new Color(15,  23,  42);
    public static final Color TEXT2       = new Color(51,  65,  85);
    public static final Color MUTED       = new Color(100, 116, 135);
    public static final Color BORDER      = new Color(203, 213, 225);

    //Font
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,   20);
    public static final Font FONT_HEAD    = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN,  13);
    public static final Font FONT_CLOCK   = new Font("Consolas",  Font.BOLD,   16);

    private UITheme() {}
}
