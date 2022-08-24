package com.blog.util;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    /**
     * 两个常量： 过期时间；秘钥
     */
    public static final long EXPIRE = 1000*60*60*24;
    public static final String SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    /**
     * 生成token字符串的方法
     * @param
     * @param
     * @return
     */
    public static String getJwtToken(Long userId){
        Map<String,Object> claims=new HashMap<>();
        claims.put("userId",userId);
        String JwtToken = Jwts.builder()
                //JWT头信息
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS2256")
                //设置分类；设置过期时间 一个当前时间，一个加上设置的过期时间常量
                //.setSubject("lin-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                //设置token主体信息，存储用户信息
                //.claim("userId", userId)
                .setClaims(claims)
                //.signWith(SignatureAlgorithm.ES256, SECRET)
                .signWith(SignatureAlgorithm.HS256, SECRET)    //签发算法，密钥为SECRET
                .compact();
        return JwtToken;
    }

    public static Map<String,Object> checkToken(String token){
        try{
            Jwt parese = Jwts.parser().setSigningKey(SECRET).parse(token);
            return (Map<String,Object>) parese.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
