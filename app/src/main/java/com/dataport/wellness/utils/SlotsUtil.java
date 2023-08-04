package com.dataport.wellness.utils;



import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;

import com.baidu.duer.botsdk.BotIntent;
import com.dataport.wellness.utils.BotConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author: zcg
 * @date: 2023/8/3
 * @desc: 解析slots
 */
public class SlotsUtil {

    //通过名字排序
    public static List<BotIntent.Slot> sortByName (List<BotIntent.Slot> slotList, String ...names){
        List<BotIntent.Slot> slots = new ArrayList<>();
        for (String name : names) {
            for (int i = 0; i < slotList.size(); i++) {
            BotIntent.Slot slot = slotList.get(i);
                if (slot.name.equals(name)){
                    slots.add(slot);
                }
            }
        }
        return slots;
    }

    /**
     * 通过 name 获取 value
     * @param slots 槽位列表
     * @param name  槽位名称
     * @return value  槽位值
     */
    //通过 name 获取 value
    public static String getValueByName(List<BotIntent.Slot> slots,@NonNull String name){
        if (slots == null){
            return null;
        }
        for (BotIntent.Slot slot : slots) {
            if (name.equals(slot.name)){
                return slot.value;
            }
        }
        return null;
    }

    /**
     * 是否存在指定名称的槽位
     * @param slots
     * @param name
     * @return
     */
    public static boolean hasSlot(List<BotIntent.Slot> slots,@NonNull String name){
        if (slots == null){
            return false;
        }
        for (BotIntent.Slot slot : slots) {
            if (name.equals(slot.name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过 name 获取 value
     * @param slots 槽位列表
     * @param name  槽位名称
     * @return value  槽位值
     */
    public static int getIntValue(List<BotIntent.Slot> slots,@NonNull String name){
        String valueByName = getValueByName(slots, name);
        if (isNumber(valueByName)){
            return Integer.parseInt(valueByName);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    //获取所有的name
    public static List<String> getAllName(List<BotIntent.Slot> slots){
        if (slots == null){
            return null;
        }
        List<String> names = new ArrayList<>();
        for (BotIntent.Slot slot : slots) {
            names.add(slot.name);
        }
        return names;
    }

    //获取所有的name
    public static List<String> getAllValue(List<BotIntent.Slot> slots){
        if (slots == null){
            return null;
        }
        List<String> values = new ArrayList<>();
        for (BotIntent.Slot slot : slots) {
            values.add(slot.value);
        }
        return values;
    }

    //通过 序号 获取 name
    public static String getNameByNo(List<BotIntent.Slot> slots, int no){
        if (slots == null || no > slots.size()){
            return null;
        }
        return slots.get(no).name;
    }

    //通过 序号 获取 value
    public static String getValueByNo(List<BotIntent.Slot> slots, int no){
        if (slots == null || no > slots.size()){
            return null;
        }
        return slots.get(no).value;
    }

    public static boolean isNumber(String string) {
        String regex = "[0-9]+[\\.]?[0-9]*";
        return Pattern.matches(regex, string);
    }

}
