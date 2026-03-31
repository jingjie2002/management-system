package com.ch.managementsystem.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 * 用于生成和验证验证码
 */
public class CaptchaUtils {

    /**
     * 验证码图片宽度
     */
    private static final int WIDTH = 120;
    
    /**
     * 验证码图片高度
     */
    private static final int HEIGHT = 40;
    
    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 4;
    
    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 20;
    
    /**
     * 验证码字符集
     */
    private static final String CHARACTERS = "0123456789";
    
    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 生成验证码
     * @return 包含验证码和图片的Captcha对象
     */
    public static Captcha generateCaptcha() {
        // 创建验证码图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)));
            g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT),
                    RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
        }

        // 绘制验证码
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            char c = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
            code.append(c);
            g.setColor(new Color(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
            g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
            g.drawString(String.valueOf(c), 20 + i * 20, 25);
        }

        g.dispose();

        // 转换为Base64
        String base64Image = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Captcha(code.toString(), base64Image);
    }

    /**
     * 验证验证码
     * @param inputCode 用户输入的验证码
     * @param correctCode 正确的验证码
     * @return 是否验证通过
     */
    public static boolean validateCaptcha(String inputCode, String correctCode) {
        return inputCode != null && correctCode != null && inputCode.equalsIgnoreCase(correctCode);
    }

    /**
     * 验证码对象
     * 包含验证码和对应的图片
     */
    public static class Captcha {
        /**
         * 验证码
         */
        private final String code;
        
        /**
         * Base64编码的图片
         */
        private final String base64Image;

        /**
         * 构造方法
         * @param code 验证码
         * @param base64Image Base64编码的图片
         */
        public Captcha(String code, String base64Image) {
            this.code = code;
            this.base64Image = base64Image;
        }

        /**
         * 获取验证码
         * @return 验证码
         */
        public String getCode() {
            return code;
        }

        /**
         * 获取Base64编码的图片
         * @return Base64编码的图片
         */
        public String getBase64Image() {
            return base64Image;
        }
    }
}