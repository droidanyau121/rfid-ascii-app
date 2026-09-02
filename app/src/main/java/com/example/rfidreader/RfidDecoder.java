package com.example.rfidreader;

public class RfidDecoder {

    /**
     * 將 Hex EPC 16進位字串轉換為 ASCII 可讀字串
     * @param hexStr 原始 16 進位 Hex (例如: "414243313233")
     * @return 轉換後 ASCII (例如: "ABC123")
     */
    public static String hexToAscii(String hexStr) {
        if (hexStr == null || hexStr.trim().isEmpty()) {
            return "";
        }
        
        // 過濾非 Hex 字元
        String cleanHex = hexStr.replaceAll("[^A-Fa-f0-9]", "");
        
        // 長度補齊為偶數
        if (cleanHex.length() % 2 != 0) {
            cleanHex = "0" + cleanHex;
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < cleanHex.length(); i += 2) {
            String str = cleanHex.substring(i, i + 2);
            try {
                int decimal = Integer.parseInt(str, 16);
                // ASCII 可列印範圍 (32 - 126)
                if (decimal >= 32 && decimal <= 126) {
                    output.append((char) decimal);
                } else {
                    output.append("?"); // 不可列印字元以 ? 替代
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return output.toString();
    }
}
