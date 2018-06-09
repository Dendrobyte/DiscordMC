package me.zaphoo.discordmc.util.Interfaces;

public interface IFunctions {

    static int count(String string, char charToCount) {
        String[] sArr = string.split("");
        int amount = 0;
        for (int i = 0; i < string.length(); i++) {
            if (sArr[i].equalsIgnoreCase(Character.toString(charToCount))) {
                amount++;
            }
        }
        return amount;
    }


}
