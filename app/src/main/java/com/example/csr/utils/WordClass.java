package com.example.csr.utils;

import java.util.*;

// TODO compose시 초성에서 자음 합쳐지지 않는 버그
public class WordClass {
    // 각종 문자열들 리스트로 등록해두는 과정
    private static List<Character> 초성 = new ArrayList<>(Arrays.asList('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
            'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'));

    private static List<Character> 중성 = new ArrayList<>(Arrays.asList('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ',
            'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'));

    private static  List<Character> 종성 = new ArrayList<>(Arrays.asList(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ',
            'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ',
            'ㅍ', 'ㅎ'));

    //ㄲㄳㄵㄶㄺㄻㄼㄽㄾㄿㅀㅄㅆ

    public static void main(String[] args) {
//        long start;
//
//        start = System.currentTimeMillis();
//        for(int i = 0; i < 100000; i++)
//        {
//            System.out.println(
//                    // 갂갂각가가ㅏㄱ갂ㄱ깎
//                    compose("ㄱㅏㄱㄱㄱㅏㄱㄱㄱㅏㄱㄱㅏㄱㅏㅏㄱㄱㅏㄱㄱㄱㄱㅏㄱㄱ")
//            )
//            ;
//
//        }
//        System.out.println(System.currentTimeMillis()-start);
        System.out.println( compose("ㄱㅏㄱㄱㄱㅏㄱㄱㄱㅏㄱㄱㅏㄱㅏㅏㄱㄱㅏㄱㄱㄱㄱㅏㄱㄱ"));
    }

    private static HashMap<Character, String> jongDecompose = new HashMap<>();
    private static HashMap<String, Character> jongCompose = new HashMap<>();

    private static HashMap<Character, String> vowelDecompose = new HashMap<>();
    private static HashMap<String, Character> vowelCompose = new HashMap<>();

    // 된소리를 분리된 두 글자로 바꿔주는 경우의수를 모두 등록
    static {
        addJong('ㄲ', "ㄱㄱ");
        addJong('ㄳ', "ㄱㅅ");
        addJong('ㄵ', "ㄴㅈ");
        addJong('ㄶ', "ㄴㅎ");
        addJong('ㄺ', "ㄹㄱ");
        addJong('ㄻ', "ㄹㅁ");
        addJong('ㄼ', "ㄹㅂ");
        addJong('ㄽ', "ㄹㅅ");
        addJong('ㄾ', "ㄹㅌ");
        addJong('ㄿ', "ㄹㅍ");
        addJong('ㅀ', "ㄹㅎ");
        addJong('ㅄ', "ㅂㅅ");
        addJong('ㅆ', "ㅅㅅ");

        addVowel('ㅑ', "ㅣㅏ");
        addVowel('ㅕ', "ㅣㅓ");
        addVowel('ㅛ', "ㅣㅗ");
        addVowel('ㅠ', "ㅣㅜ");
        //addVowel('ㅐ', "ㅣㅏ");
        //addVowel('ㅔ', "");
        addVowel('ㅒ', "ㅣㅐ");
        addVowel('ㅖ', "ㅣㅔ");
        //addVowel('ㅚ', "");
        addVowel('ㅘ', "ㅗㅏ");
        addVowel('ㅙ', "ㅗㅐ");
        //addVowel('ㅟ', "");
        addVowel('ㅝ', "ㅜㅓ");
        addVowel('ㅞ', "ㅜㅔ");
        addVowel('ㅢ', "ㅡㅣ");
    }

    public static boolean isDiphthong(char ch) {
        return jongDecompose.containsKey(ch) || vowelDecompose.containsKey(ch);
    }

    private static void addJong(char ch, String str) {
        jongCompose.put(str, ch);
        jongDecompose.put(ch, str);
    }

    private static void addVowel(char ch, String str) {
        vowelCompose.put(str, ch);
        vowelDecompose.put(ch, str);
    }

    public static String getJongDecompose(char ch) {
        return jongDecompose.get(ch);
    }

    public static char getJongCompose(String str) {
        Object x = jongCompose.get(str);
        if(x == null) {
            x = ' ';
        }
        return (char) x;
    }

    public static String getVowelDecompose(char ch) {
        return vowelDecompose.get(ch);
    }

    public static char getVowelCompose(String str) {
        Object x = vowelCompose.get(str);
        if(x == null) {
            x = ' ';
        }
        return (char) x;
        //return vowelCompose.getOrDefault(str, ' ');
    }


    // 분리된 문자열을 모두 붙여서 리턴
    public static String decompose(String data, boolean splitFortis) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < data.length(); i++) {
            char nTmp = data.charAt(i);

            sb.append(decompose(nTmp, splitFortis));
        }

        return sb.toString();
    }

    // 분리된 각 문자를 리스트에 넣어서 리턴
    public static List<String> decomposeList(String data, boolean splitFortis) {
        List<String> list = new ArrayList<>();

        for(int i = 0; i < data.length(); i++) {
            char nTmp = data.charAt(i);

            list.add(decompose(nTmp, splitFortis));
        }

        return list;
    }

    // 공식에 맞춰 char를 분리된 2~3개의 char로 변경(String 리턴)
    public static String decompose(char nTmp, boolean splitFortis) {
        if(nTmp>='가' && nTmp<='힣') {
            nTmp -= 0xAC00;

            StringBuilder sb = new StringBuilder(3);

            int jong = nTmp % 28;
            int jung = ((nTmp - jong) / 28) % 21;
            int cho = (((nTmp - jong) / 28) - jung) / 21;

            char choResult = 초성.get(cho);

            // splitFortis가 true면 ㄲ 대신 두 자모를 분리해 ㄱㄱ을 리턴
            if(splitFortis && jongDecompose.containsKey(choResult)) {
                sb.append(getJongDecompose(choResult));
            } else {
                sb.append(choResult);
            }

            sb.append(중성.get(jung));

            if (jong != 0) {
                char jongResult = 종성.get(jong);

                if(splitFortis && jongDecompose.containsKey(jongResult)) {
                    sb.append(getJongDecompose(jongResult));
                } else {
                    sb.append(jongResult);
                }
            }

            return sb.toString();
        } else {
            String decomposed = getJongDecompose(nTmp);
            if(decomposed!=null) {
                return decomposed;
            } else {
                return String.valueOf(nTmp);
            }
        }
    }

    /*  경우의수

        1. 자음(ㄱ)
        2. 모음(ㅏ)
        3. 자음 + 모음(가)
        4. 자음 + 모음 + 자음(각각각각각)
        5. 자음 + 모음 + 자음 + 모음(가나)
        6. 자음 + 모음 + 자음 + 자음 + 모음(각나)
        7. 자음 + 모음 + 자음 + 자음 + 모음 + 자음(각난)

        패턴

        1. ㄱㅏ'ㄱ'ㄴㅏㄴ (앞 모음 뒤 자음)
        2. ㄱㅏ'ㄱ'ㄱㄴㅏㄴ (앞 모음 뒤 자음.받침)
        3. ㄱㅏ'ㄴ'ㅏ  (앞 모음 뒤 모음)
        4. ㄱㅏ'ㄱ'ㄱㄱㅏ   (앞 모음 뒤 자음.받침)
        5. ㄱㅏㄱ'ㄱ'ㄱㅏ (앞 자음 뒤 자음)
        6. ㄱㅏㄱ'ㄱ'ㅏ (앞 자음 뒤 모음)

        1. ㄱ'ㅏ'ㄱㄴㅏㄴ (앞 모음 뒤 자음)
        2. ㄱ'ㅏ'ㄱㄱㄴㅏㄴ (앞 모음 뒤 자음.받침)
        3. ㄱ'ㅏ'ㄴㅏ  (앞 모음 뒤 모음)
    */

    public static char compose(char x1, char x2, char x3) {
        int x = (초성.indexOf(x1) * 21 * 28) + (중성.indexOf(x2) * 28) + 종성.indexOf(x3);
        return (char) (x + 0xAC00);
    }

    public static char compose(char x1, char x2) {
        int x = (초성.indexOf(x1) * 21 * 28) + (중성.indexOf(x2) * 28);
        return (char) (x + 0xAC00);
    }

    public static List<Integer> analyze(String str) {
        List<Integer> type = new ArrayList<Integer>();

        List<Integer> sharpTime = new LinkedList<Integer>();

        for(int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if(ch == '#') {
                sharpTime.add(i);
            } else {
                boolean isCho = 자음(ch);
                boolean isJung = 모음(ch);

                // 앞 2개 비교, 뒤 3개 비교

                if(isCho) {

                } else if(isJung) {

                } else {
                    throw new RuntimeException("cannot analyze: " + ch);
                }
            }
        }

        return type;
    }

    public static String compose(String str) {
        str = decompose(str, true);
        StringBuilder result = new StringBuilder(str.length()*3);

        List<Character> buffer = new LinkedList<Character>();

        int strlen = str.length();
        int strlenp1 = strlen + 1;

        for(int i = 0; i < strlenp1; i++) {
            char ch = ' ';
            boolean isCho = false, isJung = false;

            if (i < strlen) {
                ch = str.charAt(i);
                isCho = 자음(ch);
                isJung = 모음(ch);
            }

            if (isCho) { // 자음일 때
                processBufferCho(buffer, result, ch);
            } else if (isJung) { // 모음일 때
                processBufferJung(buffer, result, ch);
            } else { // 걍 아무것도 아닌 문자옴
                processBufferElse(buffer, result);
                if (i < strlen)
                    result.append(ch);
            }

            //System.out.println(buffer);
        }
        return result.toString();
    }

    public static int processBufferCho(List<Character> buffer, StringBuilder result, char ch) {
        int bufferSize = buffer.size();

        switch (bufferSize) {
            case 1:
                result.append(buffer.remove(0));
                break;
            case 4:
                char jongCompose = getJongCompose(String.valueOf(buffer.get(2)) + String.valueOf(buffer.get(3)));
                if (jongCompose == ' ') {
                    result.append(compose(buffer.remove(0), buffer.remove(0), buffer.remove(0)));
                } else {
                    result.append(compose(buffer.remove(0), buffer.remove(0), jongCompose));
                    buffer.remove(0);
                    buffer.remove(0);
                }
                break;
        }
        buffer.add(ch);

        return bufferSize;
    }

    public static void processBufferJung(List<Character> buffer, StringBuilder result, char ch) {
        switch(buffer.size()) {
            case 0:
                result.append(ch);
                break;
            case 1:
                buffer.add(ch);
                break;
            case 2:
            case 3:
                result.append(compose(buffer.remove(0), buffer.remove(0)));
                buffer.add(ch);
                break;
            case 4:
                result.append(compose(buffer.remove(0), buffer.remove(0), buffer.remove(0)));
                buffer.add(ch);
                break;
        }
    }

    public static void processBufferElse(List<Character> buffer, StringBuilder result) {
        switch(buffer.size()) {
            case 1: // equal cho
                result.append(buffer.remove(0));
                break;
            case 2: // equal jung
                result.append(compose(buffer.remove(0), buffer.remove(0)));
                break;
            case 3:
                result.append(compose(buffer.remove(0), buffer.remove(0), buffer.remove(0)));
                break;
            case 4: // equal cho
                char jongCompose = getJongCompose(String.valueOf(buffer.get(2)) + String.valueOf(buffer.get(3)));
                if (jongCompose == ' ') {
                    result.append(compose(buffer.remove(0), buffer.remove(0), buffer.remove(0)));
                } else {
                    result.append(compose(buffer.remove(0), buffer.remove(0), jongCompose));
                    buffer.remove(0);
                    buffer.remove(0);
                }
                break;
        }
    }

    public static String deComposeDoubleVowel(String str) {
        StringBuilder sb = new StringBuilder(str.length()*2);

        for(int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            String decomposed = getVowelDecompose(ch);

            if (decomposed != null) {
                sb.append(decomposed);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    public static String composeDoubleVowel(String str) {
        StringBuilder sb = new StringBuilder(str.length()*2);

        int strlen = str.length();
        int strlenm1 = strlen-1;

        for(int i = 0; i < strlenm1; i++) {
            char ch = str.charAt(i);
            char ch2 = str.charAt(i+1);

            String mix = String.valueOf(ch) + String.valueOf(ch2);

            char composed = getVowelCompose(mix);

            if (composed != ' ') {
                sb.append(composed);
                i++;
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    public static boolean 자음(char ch) {
        return 초성.contains(ch) || 종성.contains(ch);
    }

    public static boolean 모음(char ch) {
        return 중성.contains(ch);
    }

    public static String compose(List<String> list) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < list.size(); i++) {
            String str = list.get(i);

            sb.append(compose(str));
        }

        return sb.toString();
    }

    public static boolean isNextVowel(String str, int index) {
        boolean next = false;
        for(int i = index; i < str.length(); i++) {
            char ch = str.charAt(i);

            if(!next && ch=='ㅇ') {
                next = true;
                continue;
            }
            if(WordClass.모음(ch)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
