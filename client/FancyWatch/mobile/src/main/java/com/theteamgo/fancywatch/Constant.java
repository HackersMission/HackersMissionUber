package com.theteamgo.fancywatch;

/**
 * Created by houfang on 16/1/16.
 */
public class Constant {

    public static final String UBER_CLIENT_ID = "kpiHpDJ0KcxlqkF9rsUPyWOBafZlaXlF";
    public static final String UBER_SECRET = "n4vU5mM4bFNa2cLHwwkKskMnYY_n3f6xd5JXe033";
    public static final String UBER_GRANT_TYPE = "authorization_code";
    public static final String UBER_RESPONSE_TYPE = "code";
    public static final String UBER_SCOPE = "request profile history history_lite request_receipt";

    public static final String UBER_AUTH = "https://login.uber.com/oauth/v2/authorize";
    public static final String UBER_TOKEN = "https://login.uber.com/oauth/v2/token";

    public static final String BASE_URL = "https://tripfm.localtunnel.me/api/";
    public static final String LOGIN = BASE_URL + "account/login/";
    public static final String REGISTER = BASE_URL + "account/register/";
    public static final String UBER_REDIRECT_URL = BASE_URL + "account/callback/";
    public static final String ADD_TOKEN = BASE_URL + "account/addtoken/";
}
