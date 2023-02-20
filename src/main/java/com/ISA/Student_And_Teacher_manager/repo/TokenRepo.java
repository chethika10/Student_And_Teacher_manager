package com.ISA.Student_And_Teacher_manager.repo;

import com.ISA.Student_And_Teacher_manager.jwt.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends CrudRepository<RefreshToken,String> {
    @Query("SELECT t FROM RefreshToken t WHERE t.username = ?1")
    public RefreshToken findTokenByUserName(String username);

}
