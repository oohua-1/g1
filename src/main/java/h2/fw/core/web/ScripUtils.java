package h2.fw.core.web;

import h2.fw.utils.ConfigReader;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ScripUtils {
    private static final Logger LOGGER = LogManager.getLogger(ScripUtils.class.getName());

    public static Integer random(Integer min, Integer max) {
        Random rn = new Random();
        int range = max - min + 1;
        return rn.nextInt(range) + min;
    }

    public static String toUpperCase(String input) {
        return input.toUpperCase();
    }

    public static String randomString(Object length) {
        int _length = Integer.parseInt(String.valueOf(length));
        return RandomStringUtils.randomAlphanumeric(_length);
    }

    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    public static String localDb(String variable){
        Map<Object, Object> map = EvaluationManager.scenarioVariableDb.get();
        if(map.containsKey(variable)){
            return (String) map.get(variable);
        }else {
            return null;
        }
    }

    public static String regex(Object value, String mask){
        return regex(value, mask, false);
    }
    public static String regex(Object value, String mask, boolean isMultiline){
        return regex(value, mask, 1, isMultiline);

    }
    public static String regex(Object value, String mask, int groupNumber) {
        return regex(value, mask, groupNumber, false);
    }
    public static String regex(Object value, String mask, int groupNumber, boolean isMultiline){
        Pattern pattern = isMultiline ? Pattern.compile(mask, Pattern.DOTALL): Pattern.compile(mask);
        Matcher matcher = pattern.matcher(value.toString());
        boolean flag = matcher.matches();
        assertTrue("No match for pattern: " + value, flag);
        if (flag){
            return matcher.group(groupNumber);
        }else {
            return null;
        }

    }
    public static String env(String variable) {
        // First, try to get the value from ConfigReader
        String value = ConfigReader.getInstance(null).getProperty(variable);

        if (value == null) {
            // Try getting it from system property (-D<variable>)
            value = System.getProperty(variable);
            if (value != null) {
                LOGGER.info("Value for '" + variable + "' retrieved from system property: " + value);
            }
        }

        if (value == null) {
            // Try getting it from environment variable
            value = System.getenv(variable);
            if (value != null) {
                LOGGER.info("Value for '" + variable + "' retrieved from environment variable: " + value);
            }
        }

        if (value == null) {
            LOGGER.warn("No value found for '" + variable + "' in ConfigReader, system properties, or environment variables.");
        }

        return value;
    }


    public static String now(){
        return now("");
    }
    public static String now(String format){
        Calendar cl = Calendar.getInstance();
        if(format.isEmpty())
            format = "dd.MM.yyyy";
        DateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        return formatter.format(cl.getTime());

    }

    public static String date(Object input, String shift) throws ParseException {
        return date(input, shift, "", "");
    }
    public static String date(Object input, String shift, String format) throws ParseException {
        return date(input, shift, format, "");
    }
    public static String date(Object input, String shift, String inputFormat, String outputFormat) throws ParseException {
        String pattern = "([\\+\\-])\\s*(\\d+)\\s*([YyMdHhmS])\\s*$";
        Calendar cl = Calendar.getInstance();
        if(inputFormat.isEmpty()){
            inputFormat = "dd.MM.yyyy";
        }
        if(outputFormat.isEmpty()){
            outputFormat = inputFormat;
        }

        DateFormat formatter = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        cl.setTime(formatter.parse(input.toString()));
        if(!shift.trim().equals("")){
            String[] shiftSubPatterns = shift.split(",");
            for(String shiftSubPattern : shiftSubPatterns){
                shiftSubPattern = shiftSubPattern.trim();
                Matcher matcher = Pattern.compile(pattern).matcher(shiftSubPattern);
                String sign = null;
                String amount = null;
                String points = null;
                if(matcher.find()){
                    sign = matcher.group(1);
                    amount = matcher.group(2);
                    points = matcher.group(3);
                }
                if (sign == null) {
                    throw new RuntimeException("Shift pattern "+ shiftSubPattern + " is not correct, should beigin wth + -");


                }else{
                    int fld = -1;
                    switch(points){
                        case "y":
                            fld = Calendar.YEAR;
                        case "Y":
                            fld = Calendar.YEAR;
                        case "M":
                            fld = Calendar.MONTH;
                        case "d":
                            fld = Calendar.DATE;
                        case "H":
                            fld = Calendar.HOUR_OF_DAY;
                        case "h":
                            fld = Calendar.HOUR;
                        case "m":
                            fld = Calendar.MINUTE;
                        case "s":
                            fld = Calendar.SECOND;
                        case "S":
                            fld = Calendar.MILLISECOND;

                    }
                    cl.add(fld, (sign.equals("-")? -1 : 1)* Integer.valueOf(amount));
                }


            }
        }
        formatter = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        return formatter.format(cl.getTime());
    }



    }
