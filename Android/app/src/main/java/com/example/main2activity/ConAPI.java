package com.example.main2activity;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConAPI {
    private static String serverURL = "http://47.104.189.187:5000";

    private static String MD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String register(String telephone,String birthday ,String password1,String password2) throws Exception{
        String URL = serverURL + "/API-Register/";
        password1 = ConAPI.MD5(password1);
        password2 = ConAPI.MD5(password2);
        String data = "telephone=" + URLEncoder.encode(telephone, "UTF-8") +
                "&birthday=" + URLEncoder.encode(birthday, "UTF-8") +
                "&password1=" + URLEncoder.encode(password1, "UTF-8") +
                "&password2=" + URLEncoder.encode(password2, "UTF-8");
        return MyHTTP.postJson(URL,data);
    }

    public static String login(String telephone,String password) throws Exception{
        String URL = serverURL + "/API-Login/";
        password = ConAPI.MD5(password);
        String data = "telephone=" + URLEncoder.encode(telephone, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8");
        return MyHTTP.postJson(URL,data);
    }

    public static String getCurrentUser() throws Exception{
        String URL = serverURL + "/API-CurrentUser/";
        return MyHTTP.getJson(URL);
    }

    public static String logout() throws Exception{
        String URL = serverURL + "/API-Logout/";
        return MyHTTP.getJson(URL);
    }

    public static String sendPhoneCode(String telephone) throws Exception{
        String URL = serverURL + "/API-SendPhoneCode/" + telephone;
        return MyHTTP.getJson(URL);
    }

    public static String checkPhoneCode(String code) throws Exception{
        String URL = serverURL + "/API-CheckPhoneCode/" + code;
        return MyHTTP.getJson(URL);
    }

    public static String getAllMovies() throws Exception{
        String URL = serverURL + "/API-AllMovies/";
        return MyHTTP.getJson(URL);
    }

    public static byte[] getImage(String filename) throws Exception{
        String URL = serverURL + "/static/APP_images/" + filename;
        return MyHTTP.getImage(URL);
    }

    public static byte[] getTicketImage(String filename) throws Exception{
        String URL = serverURL + "/static/tickets/" + filename;
        return MyHTTP.getImage(URL);
    }

    public static String findSchedule(int movie_id) throws Exception{
        String URL = serverURL + "/API-FindSchedule/" + movie_id;
        System.out.println("enter find schedule");
        return MyHTTP.getJson(URL);
    }

    public static String findOrderDetailByScheduleID(int schedule_id) throws Exception{
        String URL = serverURL + "/API-FindOrderDetailByScheduleID/" + schedule_id;
        return MyHTTP.getJson(URL);
    }

    public static String newOrder(int schedule_id,int seat_row,int seat_column,String pay_method) throws Exception{
        String URL = serverURL + "/API-NewOrder/";
        String data = "schedule_id=" + schedule_id +
                "&seat_row=" + seat_row +
                "&seat_column=" + seat_column +
                "&pay_method=" + URLEncoder.encode(pay_method, "UTF-8");
        return MyHTTP.postJson(URL,data);
    }

    public static String getUserRecords() throws Exception{
        String URL = serverURL + "/API-GetUserRecords/";
        return MyHTTP.getJson(URL);
    }

    public static String getUserTickets() throws Exception{
        String URL = serverURL + "/API-GetUserTickets/";
        return MyHTTP.getJson(URL);
    }

    public static String searchMovie(String search_info) throws Exception{
        String URL = serverURL + "/API-SearchMovie/" + search_info;
        return MyHTTP.getJson(URL);
    }

    public static String searchMovieByDate(String date) throws Exception{
        String URL = serverURL + "/API-searchMovieByDate/" + date;
        return MyHTTP.getJson(URL);
    }

    public static String findHallNameByScheduleID(int schedule_id) throws Exception{
        String URL = serverURL + "/API-FindHallNameByScheduleID/" + schedule_id;
        return MyHTTP.getJson(URL);
    }

    public static String isVIPHall(int schedule_id) throws Exception{
        String URL = serverURL + "/API-IsVIPHall/" + schedule_id;
        return MyHTTP.getJson(URL);
    }

    public static String sendEmailCode(String email) throws Exception{
        String URL = serverURL + "/API-SendEmailCode/" + email;
        return MyHTTP.getJson(URL);
    }

    public static String checkEmailCode(String code) throws Exception{
        String URL = serverURL + "/API-CheckEmailCode/" + code;
        return MyHTTP.getJson(URL);
    }

    public static String getCurrentUserEmail() throws Exception{
        String URL = serverURL + "/API-CurrentUserEmail/";
        return MyHTTP.getJson(URL);
    }

    public static String resetEmail() throws Exception{
        String URL = serverURL + "/API-ResetEmail/";
        return MyHTTP.getJson(URL);
    }

    public static String getRecordByID(int id) throws Exception{
        String URL = serverURL + "/API-GetRecordByID/" + id;
        return MyHTTP.getJson(URL);
    }

    public static String payOrder(int id) throws Exception{
        String URL = serverURL + "/API-PayOrder/" + id;
        return MyHTTP.getJson(URL);
    }

    public static String getCurrentUserCard() throws Exception{
        String URL = serverURL + "/API-CurrentUserCard/";
        return MyHTTP.getJson(URL);
    }

    public static String setCard(String card) throws Exception{
        String URL = serverURL + "/API-SetCard/" + card;
        return MyHTTP.getJson(URL);
    }

    public static String resetCard() throws Exception{
        String URL = serverURL + "/API-ResetCard/";
        return MyHTTP.getJson(URL);
    }

    public static String currentUserIsVIP() throws Exception{
        String URL = serverURL + "/API-CurrentUserIsVIP/";
        return MyHTTP.getJson(URL);
    }

    public static String setUserVIP() throws Exception{
        String URL = serverURL + "/API-SetUserVIP/";
        return MyHTTP.getJson(URL);
    }

}


class MyHTTP {

    private static String cookie = "";

    // ????????????byte[]????????????
    public static byte[] getImage(String path) throws Exception {
        String NowCookie = "";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // ?????????????????????5???
        conn.setConnectTimeout(5000);
        // ?????????????????????Get??????
        conn.setRequestMethod("GET");
        if(cookie != ""){
            conn.setRequestProperty("Cookie",cookie);
        }
        if (conn.getResponseCode() != 200) {
            NowCookie = conn.getHeaderField("Set-Cookie");
            if(NowCookie != null && NowCookie.length() > 1){
                cookie = NowCookie.split(";")[0];
            }
            throw new RuntimeException("??????url??????");
        }
        NowCookie = conn.getHeaderField("Set-Cookie");
        if(NowCookie != null && NowCookie.length() > 1){
            cookie = NowCookie.split(";")[0];
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = StreamTool.read(inStream);
        inStream.close();
        return bt;
    }

    // ??????String?????????Json?????????
    public static String getJson(String path) throws Exception {
        String NowCookie = "";
        String json = null;
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        System.out.println("test_getjson_1");
        int response_code;
        if(cookie != ""){
            conn.setRequestProperty("Cookie",cookie);
            System.out.println("test_getjson_2");
        }
        response_code = conn.getResponseCode();
        if (response_code == 200) {
            System.out.println("test_getjson_3");
            InputStream in = conn.getInputStream();
            byte[] data = StreamTool.read(in);
            System.out.println("path: " + path + " get json: " + data);
            json = new String(data, "UTF-8");
        }
        else{
            System.out.println(response_code);
        }
        NowCookie = conn.getHeaderField("Set-Cookie");
        if(NowCookie != null && NowCookie.length() > 1){
            cookie = NowCookie.split(";")[0];
        }
        return json;
    }

    // ??????String?????????Json?????????
    public static String postJson(String url,String data)
    {
        String msg = "";
        String NowCookie = "";
        try{

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //??????????????????,??????????????????
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            //??????????????????,??????:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //Post??????????????????,??????????????????false
            conn.setUseCaches(false);
            if(cookie != ""){
                conn.setRequestProperty("Cookie",cookie);
            }
            //???????????????????????????????????????...
            //???????????????
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            NowCookie = conn.getHeaderField("Set-Cookie");
            if(NowCookie != null && NowCookie.length() > 1){
                cookie = NowCookie.split(";")[0];
            }
            if (conn.getResponseCode() == 200) {
                // ??????????????????????????????
                InputStream is = conn.getInputStream();
                // ???????????????????????????
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // ?????????????????????
                int len = 0;
                // ???????????????
                byte buffer[] = new byte[1024];
                // ???????????????????????????????????????
                while ((len = is.read(buffer)) != -1) {
                    // ??????????????????????????????os?????????
                    message.write(buffer, 0, len);
                }
                // ????????????
                is.close();
                message.close();
                // ???????????????
                msg = new String(message.toByteArray());
                return msg;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return msg;
    }
}



class StreamTool {
    //?????????????????????(??????????????????)
    public static byte[] read(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer,0,len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}


