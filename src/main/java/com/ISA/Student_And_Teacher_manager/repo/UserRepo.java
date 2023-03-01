package com.ISA.Student_And_Teacher_manager.repo;

import com.ISA.Student_And_Teacher_manager.entity.users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User,Integer> {
    @Query("SELECT u FROM User u WHERE u.userName = ?1")
    public User findUserByUserName(String username);

    @Query(value = "SELECT id,username,role,enabled FROM user",nativeQuery = true)
    public List<Object> getAllUsers();
}
