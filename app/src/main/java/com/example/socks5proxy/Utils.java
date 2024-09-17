/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.socks5proxy;;

/**
 *
 * @author KDark
 */
public class Utils {
    // Define the constant MAGIC_FLAG
    public static final byte[] MAGIC_FLAG = new byte[]{0x37, 0x37};

    // Method to combine two bytes into a word
    public static int makeWord(byte a, byte b) {
        return ((a & 0xFF) << 8) | (b & 0xFF);
    }
}