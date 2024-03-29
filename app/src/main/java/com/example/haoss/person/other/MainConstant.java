package com.example.haoss.person.other;

import java.io.File;
import java.util.ArrayList;

/**
 */

public class MainConstant {
    public static final String IMG_LIST = "img_list"; //第几张图片
    public static final String POSITION = "position"; //第几张图片
    public static final String PIC_PATH = "pic_path"; //图片路径
    public static final String PIC = "ispic";
    public static ArrayList<File> PATH_FILES = new ArrayList<>();//图片地址
    public static final int MAX_SELECT_PIC_NUM = 6; // 最多上传5张图片
    public static final int REQUEST_CODE_MAIN = 10; //请求码
    public static final int RESULT_CODE_VIEW_IMG = 11; //查看大图页面的结果码
}
