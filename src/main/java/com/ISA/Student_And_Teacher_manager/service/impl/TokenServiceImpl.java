package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.jwt.RefreshToken;
import com.ISA.Student_And_Teacher_manager.repo.TokenRepo;
import com.ISA.Student_And_Teacher_manager.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private TokenRepo tokenRepo;
    @Override
    public RefreshToken getRefreshTokenByUsername(String username) {
        return tokenRepo.findTokenByUserName(username);
    }

    @Override
    public RefreshToken getRefreshTokenByToken(String token) {
        return tokenRepo.findById(token).orElse(null);
    }

    @Override
    public RefreshToken addOrUpdateToken(RefreshToken token) {
        System.out.println("addorupdate");
        //TODO make as users can login from two devices at once
        RefreshToken token1=null;
        try {
            token1=tokenRepo.findTokenByUserName(token.getUsername());

        }catch (Exception e){
            e.printStackTrace();
        }
        if (token1 != null) {
            tokenRepo.delete(token1);
        }
        token1=tokenRepo.save(token);
        return token1;
    }

    @Override
    public void deleteToken(RefreshToken token) {
        RefreshToken token1=null;
        try{
            token1=tokenRepo.findById(token.getToken()).orElse(null);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(token1 != null){
            tokenRepo.delete(token1);
        }

    }
}
