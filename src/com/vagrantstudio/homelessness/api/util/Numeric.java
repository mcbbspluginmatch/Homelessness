/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author BergStudio
 */
public class Numeric {

    public static boolean insideArea(Location target, Location min, Location max) {
        return target.getBlockX() >= min.getBlockX() && target.getBlockX() <= max.getBlockX()
                && target.getBlockY() >= min.getBlockY() && target.getBlockY() <= max.getBlockY()
                && target.getBlockZ() >= min.getBlockZ() && target.getBlockZ() <= max.getBlockZ();
    }

    public static Entity getCursorTarget(Player p, double range) {
        Block block;
        Entity target;
        Iterator<Entity> entities;
        Location loc = p.getEyeLocation();
        Vector vec = loc.getDirection().multiply(0.15);
        while ((range -= 0.1) > 0 && ((block = loc.getWorld().getBlockAt(loc)).isLiquid() || block.isEmpty())) {
            entities = loc.getWorld().getNearbyEntities(loc.add(vec), 0.001, 0.001, 0.001).iterator();
            while (entities.hasNext()) {
                if ((target = entities.next()) != p) {
                    return target;
                }
            }
        }
        return null;
    }

    public static ConfigurationSection locationToSection(Location location) {
        ConfigurationSection section = new YamlConfiguration();
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        return section;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static int toChunkLoc(int value) {
        return value % 16 == 0 || value <= 16 || value >= -16 ? (value / 16) + 1 : (value / 16);
    }

    public static boolean compareLocation(Location loc0, Location loc1) {
        return loc0.getBlockX() == loc1.getBlockX() && loc0.getBlockY() == loc1.getBlockY() && loc0.getBlockZ() == loc1.getBlockZ();
    }

    /**
     * 将字符串转化成List
     * @param str
     * @return
     */
    public static ArrayList<String> getStringList(String str) {
        ArrayList<String> result = new ArrayList<>();
        String num = "";
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                num = num + str.charAt(i);
            } else {
                if (!"".equals(num)) {
                    result.add(num);
                }
                result.add(str.charAt(i) + "");
                num = "";
            }
        }
        if (!"".equals(num)) {
            result.add(num);
        }
        return result;
    }

    /**
     * 将中缀表达式转化为后缀表达式
     * @param inOrderList
     * @return
     */
    public static ArrayList<String> getPostOrder(ArrayList<String> inOrderList) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < inOrderList.size(); i++) {
            if (Character.isDigit(inOrderList.get(i).charAt(0))) {
                result.add(inOrderList.get(i));
            } else {
                switch (inOrderList.get(i).charAt(0)) {
                    case '(':
                        stack.push(inOrderList.get(i));
                        break;
                    case ')':
                        while (!stack.peek().equals("(")) {
                            result.add(stack.pop());
                        }
                        stack.pop();
                        break;
                    default:
                        while (!stack.isEmpty() && compare(stack.peek(), inOrderList.get(i))) {
                            result.add(stack.pop());
                        }
                        stack.push(inOrderList.get(i));
                        break;
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }

    /**
     * 计算后缀表达式
     * @param postOrder 
     * @return
     */
    public static Integer calculate(ArrayList<String> postOrder) {
        Stack stack = new Stack();
        for (int i = 0; i < postOrder.size(); i++) {
            if (Character.isDigit(postOrder.get(i).charAt(0))) {
                stack.push(Integer.parseInt(postOrder.get(i)));
            } else {
                Integer back = (Integer) stack.pop();
                Integer front = (Integer) stack.pop();
                Integer res = 0;
                switch (postOrder.get(i).charAt(0)) {
                    case '+':
                        res = front + back;
                        break;
                    case '-':
                        res = front - back;
                        break;
                    case '*':
                        res = front * back;
                        break;
                    case '/':
                        res = front / back;
                        break;
                    case '^':
                        res = front * front;
                        break;
                }
                stack.push(res);
            }
        }
        return (Integer) stack.pop();
    }

    /**
     * 比较运算符等级
     * @param peek
     * @param cur
     * @return
     */
    public static boolean compare(String peek, String cur) {
        if ("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur) || "^".equals(cur))) {
            return true;
        } else if ("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur) || "^".equals(cur))) {
            return true;
        } else if ("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("^".equals(cur) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur) || "*".equals(cur))){
            return true;
        }
        return false;
    }
    
    public static int calculate(String str){
        return calculate(getPostOrder(getStringList(str)));
    }
}
