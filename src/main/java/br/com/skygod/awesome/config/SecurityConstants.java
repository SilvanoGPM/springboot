package br.com.skygod.awesome.config;

import java.util.concurrent.TimeUnit;

public class SecurityConstants {

    // Authorization Bearer fkqfqgkqjgqkgjqg015i151qkqg

    static final String SECRET = "SKYGODLINDODEMAIS";
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";
    static final String SING_UP_URL = "/users/sing-up";
    static final long EXPIRATION_TIME = 86400000;


    public static void main(String[] args) {
        System.out.println(TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

}
