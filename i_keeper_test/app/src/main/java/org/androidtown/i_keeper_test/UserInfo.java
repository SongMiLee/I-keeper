package org.androidtown.i_keeper_test;

/**
 * 사용자가 로그인한 id 정보를 다른 fragment나 activity에 넘겨주기
 * 위해서 사용하는 클래스 이다.
 */
public class UserInfo {
    static String userid;
    public void setUserID(String textID){
        userid=textID;
    }
    public String returnUserID(){
        return userid;
    }
}
