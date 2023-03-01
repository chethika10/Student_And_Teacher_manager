package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.entity.jwt.RefreshToken;

public interface TokenService {

    public RefreshToken getRefreshTokenByUsername(String username);
    public RefreshToken getRefreshTokenByToken(String token);
    public RefreshToken addOrUpdateToken(RefreshToken token);
    public void deleteToken(RefreshToken token);
}
